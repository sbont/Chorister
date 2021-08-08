package nl.stevenbontenbal.chorister.configuration

import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain


@EnableWebSecurity
class ResourceServerConfig {
    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.mvcMatcher("/api/**")
            .authorizeRequests()
            .mvcMatchers("/api/**").access("hasAuthority('SCOPE_cms')")
            .and()
            .oauth2ResourceServer()
            .jwt()
        return http.build()
    }
}