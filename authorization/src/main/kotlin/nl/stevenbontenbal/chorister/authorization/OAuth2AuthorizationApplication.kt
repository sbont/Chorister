package nl.stevenbontenbal.chorister.authorization

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OAuth2AuthorizationApplication

fun main(args: Array<String>) {
	runApplication<OAuth2AuthorizationApplication>(*args)
}
