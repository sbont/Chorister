package nl.stevenbontenbal.chorister.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("chorister")
data class ChoristerProperties(var title: String, val banner: Banner) {
    data class Banner(val title: String? = null, val content: String)
}