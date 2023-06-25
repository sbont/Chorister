package nl.stevenbontenbal.chorister.authorization

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.access.PermissionEvaluator
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity


@Configuration
@EnableMethodSecurity
internal class PermissionEvaluatorConfiguration {
    @Bean
    fun expressionHandler(evaluator: PermissionEvaluator): MethodSecurityExpressionHandler {
        val expressionHandler = DefaultMethodSecurityExpressionHandler()
        expressionHandler.setPermissionEvaluator(evaluator)
        return expressionHandler
    }
}