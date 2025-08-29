package nl.stevenbontenbal.chorister.application.config

import com.zaxxer.hikari.HikariDataSource
import nl.stevenbontenbal.chorister.api.CanonicalLinkCollector
import nl.stevenbontenbal.chorister.api.ChoirContextFilter
import nl.stevenbontenbal.chorister.api.SpaCsrfTokenRequestHandler
import nl.stevenbontenbal.chorister.application.CategorisationService
import nl.stevenbontenbal.chorister.application.FileService
import nl.stevenbontenbal.chorister.application.RegistrationService
import nl.stevenbontenbal.chorister.domain.events.Event
import nl.stevenbontenbal.chorister.domain.events.EventEntry
import nl.stevenbontenbal.chorister.domain.songs.*
import nl.stevenbontenbal.chorister.domain.users.*
import nl.stevenbontenbal.chorister.persistence.ChoirAwareDataSource
import org.modelmapper.ModelMapper
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.Ordered
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.data.rest.core.mapping.RepositoryDetectionStrategy
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer
import org.springframework.data.rest.webmvc.mapping.LinkCollector
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import javax.sql.DataSource

@EnableWebSecurity(debug = true)
@EnableMethodSecurity
@Configuration
class ChoristerConfiguration(
    private val properties: ChoristerProperties
) {

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity, userService: UserService): SecurityFilterChain {
        http {
            authorizeHttpRequests {
                authorize(HttpMethod.POST, "/api/registration", permitAll)
                authorize("/api/invite/**", permitAll)
                authorize("/api/**",authenticated)
            }
            addFilterAfter<SecurityContextHolderAwareRequestFilter>(ChoirContextFilter(userService))
            headers {
                frameOptions { disable() }
            }
            csrf {
                ignoringRequestMatchers("/api/registration", "/api/invite/**")
                csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse()
                csrfTokenRequestHandler = SpaCsrfTokenRequestHandler()
            }
            cors {
                configurationSource = corsConfigurationSource()
            }
        }
        http.oauth2ResourceServer { it.jwt(Customizer.withDefaults()) }
        return http.build()
    }

    @Bean
    fun repositoryRestConfigurer(): RepositoryRestConfigurer? {
        return object : RepositoryRestConfigurer {
            override fun configureRepositoryRestConfiguration(
                configuration: RepositoryRestConfiguration,
                corsRegistry: CorsRegistry
            ) {
                configuration.exposeIdsFor(Song::class.java)
                configuration.exposeIdsFor(Notation::class.java)
                configuration.exposeIdsFor(Chords::class.java)
                configuration.exposeIdsFor(Score::class.java)
                configuration.exposeIdsFor(Category::class.java)
                configuration.exposeIdsFor(CategoryType::class.java)
                configuration.exposeIdsFor(Event::class.java)
                configuration.exposeIdsFor(EventEntry::class.java)
                configuration.exposeIdsFor(User::class.java)
                configuration.exposeIdsFor(Invite::class.java)
                configuration.exposeIdsFor(File::class.java)
                configuration.repositoryDetectionStrategy = RepositoryDetectionStrategy.RepositoryDetectionStrategies.ANNOTATED
            }

            override fun customizeLinkCollector(collector: LinkCollector): LinkCollector {
                return CanonicalLinkCollector(collector)
            }
        }
    }

    fun corsConfigurationSource(): UrlBasedCorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.addAllowedOrigin(properties.baseUrl)
        config.addAllowedHeader("*")
        config.addAllowedMethod("*")
        source.registerCorsConfiguration("/**", config)
        return source
    }

    @Bean
    fun modelMapper(): ModelMapper = ModelMapper()

    @Bean
    fun registrationService(
        userRepository: IUserRepository,
        choirRepository: IChoirRepository,
        inviteRepository: IInviteRepository,
        userAuthorizationService: IUserAuthorizationService,
        categorisationService: CategorisationService,
        userService: UserService
    ): RegistrationService = RegistrationService(
        userRepository,
        choirRepository,
        inviteRepository,
        userAuthorizationService,
        categorisationService,
        userService
    )

    @Bean
    fun categorisationService(
        categoryRepository: ICategoryRepository,
        categoryTypeRepository: ICategoryTypeRepository
    ): CategorisationService = CategorisationService(categoryRepository, categoryTypeRepository)

    @Bean
    fun fileService(
        fileRepository: IFileRepository,
        s3Configuration: S3Configuration,
        userService: UserService
        ): FileService = FileService(fileRepository, s3Configuration, userService)

    @Bean
    fun scoreService(
        scoreRepository: IScoreRepository,
        fileService: FileService,
        userService: UserService
    ): ScoreService = ScoreService(scoreRepository, fileService, userService)

    @Bean
    @ConfigurationProperties("chorister.datasource.system")
    fun systemDataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    @Bean
    @LiquibaseDataSource
    @ConfigurationProperties("chorister.datasource.system.hikari")
    fun systemDataSource(): DataSource {
        val dataSource = systemDataSourceProperties()
            .initializeDataSourceBuilder()
            .type(HikariDataSource::class.java)
            .build()
        dataSource?.poolName = "systemDataSource"
        return dataSource
    }

    @Bean
    @Primary
    @ConfigurationProperties("chorister.datasource.user")
    fun userDataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    @Bean
    @Primary
    @ConfigurationProperties("chorister.datasource.user.hikari")
    fun userDataSource(): DataSource? {
        val dataSource = userDataSourceProperties()
            .initializeDataSourceBuilder()
            .type(HikariDataSource::class.java)
            .build()
        dataSource?.poolName = "userDataSource"
        return ChoirAwareDataSource(dataSource)
    }

}