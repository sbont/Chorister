package nl.stevenbontenbal.chorister.configuration

import io.netty.channel.ChannelOption
import io.netty.handler.logging.LogLevel
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import nl.stevenbontenbal.chorister.authorization.ChoirAccessPermissionEvaluator
import nl.stevenbontenbal.chorister.model.Category
import nl.stevenbontenbal.chorister.model.Score
import nl.stevenbontenbal.chorister.model.Setlist
import nl.stevenbontenbal.chorister.model.Song
import nl.stevenbontenbal.chorister.repository.*
import nl.stevenbontenbal.chorister.service.CategorisationService
import nl.stevenbontenbal.chorister.service.KeycloakUserService
import nl.stevenbontenbal.chorister.service.RegistrationService
import nl.stevenbontenbal.chorister.service.UserService
import org.modelmapper.ModelMapper
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer
import org.springframework.http.HttpMethod
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.security.access.PermissionEvaluator
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.security.web.SecurityFilterChain
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
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
class ChoristerConfiguration {

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.mvcMatcher("/api/**")
            .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/registration")
                .permitAll()
                .and()
            .authorizeRequests()
                .mvcMatchers("/api/**")
                .access("hasAuthority('SCOPE_cms')")
                .and()
            .oauth2ResourceServer()
                .jwt()
        http.csrf().disable()
        return http.build()
    }

    @Bean
    fun permissionEvaluator(userService: UserService): PermissionEvaluator = ChoirAccessPermissionEvaluator(userService)

    @Bean
    fun corsConfigurer(): WebMvcConfigurer? {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("http://localhost:8080/")
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
                corsRegistry.addMapping("/api/**")
                    .allowedMethods("*")
                    .allowedOrigins("http://localhost:8080/")
            }
        }
    }

    @Bean
    fun corsFilter(): FilterRegistrationBean<*> {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.addAllowedOrigin("http://localhost:8080/")
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
    fun keycloakClient(
        authorizedClientManager: OAuth2AuthorizedClientManager
    ): WebClient {
        val oauth2Client = ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager)
        oauth2Client.setDefaultClientRegistrationId("keycloak")
        return WebClient.builder()
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
    }

    @Bean
    fun keycloakClientService(
        webClient: WebClient,
        keycloakConfiguration: KeycloakConfiguration
    ): KeycloakUserService = KeycloakUserService(keycloakConfiguration, webClient)

    @Bean
    fun registrationService(
        userRepository: UserRepository,
        choirRepository: ChoirRepository,
        keycloakUserService: KeycloakUserService,
        categorisationService: CategorisationService
    ): RegistrationService = RegistrationService(userRepository, choirRepository, keycloakUserService, categorisationService)

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