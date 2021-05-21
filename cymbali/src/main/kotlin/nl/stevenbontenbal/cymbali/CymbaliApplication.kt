package nl.stevenbontenbal.cymbali

import nl.stevenbontenbal.cymbali.configuration.CymbaliProperties
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties

@SpringBootApplication
@EnableConfigurationProperties(CymbaliProperties::class)
class CymbaliApplication

fun main(args: Array<String>) {
	SpringApplication.run(CymbaliApplication::class.java, *args)
}
