package nl.stevenbontenbal.chorister.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("s3")
data class S3Configuration(
    val endpointUrl: String = "",
    val bucketName: String = ""
)