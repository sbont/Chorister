package nl.stevenbontenbal.chorister.configuration

import io.netty.channel.ChannelOption
import io.netty.handler.logging.LogLevel
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import nl.stevenbontenbal.chorister.authorization.AccessPermissionEvaluator
import nl.stevenbontenbal.chorister.interfaces.UserAuthorizationService
import nl.stevenbontenbal.chorister.model.*
import nl.stevenbontenbal.chorister.model.entities.*
import nl.stevenbontenbal.chorister.repository.*
import nl.stevenbontenbal.chorister.service.*
import org.modelmapper.ModelMapper
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.convert.support.ConfigurableConversionService
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer
import org.springframework.http.HttpMethod
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.security.access.PermissionEvaluator
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import reactor.netty.Connection
import reactor.netty.http.client.HttpClient
import reactor.netty.transport.logging.AdvancedByteBufFormat
import java.util.concurrent.TimeUnit


@EnableWebSecurity(debug = true)
@EnableMethodSecurity
@Configuration
class ChoristerConfiguration {

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http.authorizeHttpRequests { auth ->
            auth.requestMatchers(HttpMethod.POST, "/api/registration").permitAll()
                .requestMatchers("/api/invite/**").permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
                .requestMatchers("/api/**").authenticated()
                .and().oauth2ResourceServer().jwt()
        }.headers { headers: HeadersConfigurer<HttpSecurity?> ->
            headers.frameOptions().disable()
        }.csrf().disable()
//            .csrf { csrf: CsrfConfigurer<HttpSecurity?> ->
//                csrf
//                    .ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**"))
//            }
            .build()

    @Bean
    fun permissionEvaluator(userService: UserService): PermissionEvaluator = AccessPermissionEvaluator(userService)

    @Bean
    fun corsConfigurer(): WebMvcConfigurer? {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("http://localhost:8080/", "http://localhost:8091/")
            }
        }
    }

    @Bean
    fun repositoryRestConfigurer(): RepositoryRestConfigurer? {
        return object : RepositoryRestConfigurer {
            override fun configureRepositoryRestConfiguration(
                configuration: RepositoryRestConfiguration,
                corsRegistry: CorsRegistry
            ) {
                configuration.exposeIdsFor(Song::class.java)
                configuration.exposeIdsFor(Score::class.java)
                configuration.exposeIdsFor(Category::class.java)
                configuration.exposeIdsFor(Setlist::class.java)
                configuration.exposeIdsFor(User::class.java)
                configuration.exposeIdsFor(Invite::class.java)
                configuration.exposeIdsFor(SetlistEntry::class.java)
                corsRegistry.addMapping("/api/**")
                    .allowedMethods("*")
                    .allowedOrigins("http://localhost:8080/")
            }

            override fun configureConversionService(conversionService: ConfigurableConversionService?) {
                super.configureConversionService(conversionService)
                conversionService?.addConverter(SetlistEntryIdConverter())
            }
        }
    }

    @Bean
    fun corsFilter(): FilterRegistrationBean<*> {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.addAllowedOrigin("http://localhost:8080/")
        config.addAllowedOrigin("http://localhost:8091/")
        config.addAllowedHeader("*")
        config.addAllowedMethod("*")
        source.registerCorsConfiguration("/**", config)
        val bean: FilterRegistrationBean<*> = FilterRegistrationBean(CorsFilter(source))
        bean.order = Ordered.HIGHEST_PRECEDENCE
        return bean
    }

    @Bean
    fun authorizedClientManager(
        clientRegistrationRepository: ClientRegistrationRepository,
        authorizedClientRepository: OAuth2AuthorizedClientRepository
    ): OAuth2AuthorizedClientManager {
        val authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
            .clientCredentials()
            .build()

        val authorizedClientManager = DefaultOAuth2AuthorizedClientManager(
            clientRegistrationRepository, authorizedClientRepository
        )
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider)
        return authorizedClientManager
    }

    @Bean
    fun authorizedWebClient(
        authorizedClientManager: OAuth2AuthorizedClientManager
    ): WebClient {
        val oauth2Client = ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager)
        return buildOAuthClient(oauth2Client)
    }

    /*@Bean
    fun zitadelClient(
        authorizedClientManager: OAuth2AuthorizedClientManager
    ): WebClient {
        val oauth2Client = ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager)
        oauth2Client.setDefaultClientRegistrationId("zitadel")
        return buildOAuthClient(oauth2Client)
    }*/

    private fun buildOAuthClient(oauth2Client: ServletOAuth2AuthorizedClientExchangeFilterFunction) =
        WebClient.builder()
            .apply(oauth2Client.oauth2Configuration())
            .clientConnector(
                ReactorClientHttpConnector(
                    HttpClient
                        .create()
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                        .doOnConnected { connection: Connection ->
                            connection.addHandlerLast(ReadTimeoutHandler(10000, TimeUnit.MILLISECONDS))
                            connection.addHandlerLast(WriteTimeoutHandler(10000, TimeUnit.MILLISECONDS))
                        }
                        .wiretap("reactor.netty.http.client.HttpClient", LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)
                )
            )
            .build()


    @Bean
    fun zitadelClientService(
        zitadelConfiguration: ZitadelProperties,
        oauthClientManager: OAuth2AuthorizedClientManager
    ): UserAuthorizationService = ZitadelUserService(zitadelConfiguration, authorizedWebClient(oauthClientManager))

    @Bean
    fun registrationService(
        userRepository: UserRepository,
        choirRepository: ChoirRepository,
        inviteRepository: InviteRepository,
        userAuthorizationService: UserAuthorizationService,
        categorisationService: CategorisationService,
        userService: UserService
    ): RegistrationService = RegistrationService(userRepository, choirRepository, inviteRepository, userAuthorizationService, categorisationService, userService)

    @Bean
    fun userService(userRepository: UserRepository): UserService = UserService(userRepository)

    @Bean
    fun categorisationService(choristerProperties: ChoristerProperties, categoryRepository: CategoryRepository): CategorisationService = CategorisationService(choristerProperties, categoryRepository)

    @Bean
    fun databaseInitializer(choirRepository: ChoirRepository,
                            userRepository: UserRepository,
                            songbookRepository: SongbookRepository,
                            songRepository: SongRepository
    ) = ApplicationRunner {

    }

    @Bean
    fun modelMapper(): ModelMapper = ModelMapper()

}