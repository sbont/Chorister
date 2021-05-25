package nl.stevenbontenbal.cymbali.configuration

import nl.stevenbontenbal.cymbali.model.Score
import nl.stevenbontenbal.cymbali.model.Song
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer
import org.springframework.stereotype.Component
import org.springframework.web.servlet.config.annotation.CorsRegistry

@Component
class RestConfig : RepositoryRestConfigurer {
    override fun configureRepositoryRestConfiguration(config: RepositoryRestConfiguration, cors: CorsRegistry) {
        println("RestConfig configured.")
        config.exposeIdsFor(Song::class.java)
        config.exposeIdsFor(Score::class.java)
        cors.addMapping("/api/**")
            .allowedMethods("*")
            .allowedOrigins("http://localhost:8081/")
    }
}
