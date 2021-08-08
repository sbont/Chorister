package nl.stevenbontenbal.chorister.authorization.configuration

import org.springframework.context.annotation.Bean
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain


@EnableWebSecurity
class DefaultSecurityConfig {
    @Bean
    @Throws(Exception::class)
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf().disable()
            .cors().disable()
            .authorizeRequests { authorizeRequests -> authorizeRequests.anyRequest().authenticated() }
            .formLogin( withDefaults() )
        return http.build()
    }

    @Bean
    fun users(): UserDetailsService? {
        val user: UserDetails = User.withDefaultPasswordEncoder()
            .username("a")
            .password("s")
            .roles("USER")
            .build()
        return InMemoryUserDetailsManager(user)
    }
}