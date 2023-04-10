package nl.stevenbontenbal.chorister.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("zitadel")
data class ZitadelConfiguration(
    var baseUrl: String = "@ConfigurationProperties(\"chorister\")"
)