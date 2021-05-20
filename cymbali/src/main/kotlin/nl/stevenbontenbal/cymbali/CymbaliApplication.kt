package nl.stevenbontenbal.cymbali

import nl.stevenbontenbal.cymbali.configuration.CymbaliProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(CymbaliProperties::class)
class CymbaliApplication

fun main(args: Array<String>) {
	runApplication<CymbaliApplication>(*args)
}
