package nl.stevenbontenbal.chorister.configuration

import nl.stevenbontenbal.chorister.model.Score
import nl.stevenbontenbal.chorister.model.Song
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
            .allowedOrigins("http://localhost:8080/")
    }
}
