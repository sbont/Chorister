package nl.stevenbontenbal.chorister.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "keycloakservice")
class KeycloakConfiguration {
    var url: String = "http://localhost:9000/auth/admin/realms/Chorister"
}