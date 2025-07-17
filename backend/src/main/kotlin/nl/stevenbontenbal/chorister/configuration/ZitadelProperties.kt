package nl.stevenbontenbal.chorister.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("zitadel")
data class ZitadelProperties(
    var baseUrl: String = "http://localhost:9000/management/v1",
    var adminAccessToken: String = "",
    var projectId: String = "",
)