package nl.stevenbontenbal.cymbali

import nl.stevenbontenbal.cymbali.configuration.CymbaliProperties
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer
import org.springframework.stereotype.Component
import org.springframework.web.servlet.config.annotation.CorsRegistry

@SpringBootApplication
@EnableConfigurationProperties(CymbaliProperties::class)
class CymbaliApplication

fun main(args: Array<String>) {
	SpringApplication.run(CymbaliApplication::class.java, *args)
}

@Component
class RestConfig : RepositoryRestConfigurer {
	override fun configureRepositoryRestConfiguration(config: RepositoryRestConfiguration, cors: CorsRegistry) {
		println("RestConfig configured.")
		config.exposeIdsFor(Song::class.java)
		cors.addMapping("/api/**")
			.allowedMethods("*")
			.allowedOrigins("http://localhost:8081/")
	}
}
