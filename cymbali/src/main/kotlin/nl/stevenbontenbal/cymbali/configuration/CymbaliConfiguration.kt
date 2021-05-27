package nl.stevenbontenbal.cymbali.configuration

import nl.stevenbontenbal.cymbali.repository.SongRepository
import nl.stevenbontenbal.cymbali.repository.UserRepository
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class CymbaliConfiguration {

    @Bean
    fun corsConfigurer(): WebMvcConfigurer? {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/api/**").allowedOrigins("http://localhost:8081/")
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
                corsRegistry.addMapping("/api/**")
                    .allowedMethods("*")
                    .allowedOrigins("http://localhost:8081/")
            }
        }
    }

    @Bean
    fun corsFilter(): FilterRegistrationBean<*> {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.addAllowedOrigin("http://localhost:8081/")
        config.addAllowedHeader("*")
        config.addAllowedMethod("*")
        source.registerCorsConfiguration("/**", config)
        val bean: FilterRegistrationBean<*> = FilterRegistrationBean(CorsFilter(source))
        bean.order = Ordered.HIGHEST_PRECEDENCE
        return bean
    }

    @Bean
    fun databaseInitializer(userRepository: UserRepository,
                            songRepository: SongRepository
    ) = ApplicationRunner {

    }

}