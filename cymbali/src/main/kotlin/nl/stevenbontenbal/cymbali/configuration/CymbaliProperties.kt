package nl.stevenbontenbal.cymbali.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("cymbali")
data class CymbaliProperties(var title: String, val banner: Banner) {
    data class Banner(val title: String? = null, val content: String)
}