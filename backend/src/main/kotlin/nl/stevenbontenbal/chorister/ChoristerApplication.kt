package nl.stevenbontenbal.chorister

import nl.stevenbontenbal.chorister.configuration.ChoristerProperties
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties

@SpringBootApplication
@EnableConfigurationProperties(ChoristerProperties::class)
class ChoristerApplication

fun main(args: Array<String>) {
	SpringApplication.run(ChoristerApplication::class.java, *args)
}