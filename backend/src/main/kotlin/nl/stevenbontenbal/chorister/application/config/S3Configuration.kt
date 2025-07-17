package nl.stevenbontenbal.chorister.application.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("s3")
data class S3Configuration(
    var endpointUrl: String = "",
    var bucketName: String = "",
    var region: String = "",
    var accessKey: String = "",
    var secretKey: String = "",
)
