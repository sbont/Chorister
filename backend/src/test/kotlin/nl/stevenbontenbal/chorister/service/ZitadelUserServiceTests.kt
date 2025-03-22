package nl.stevenbontenbal.chorister.service

import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import nl.stevenbontenbal.chorister.configuration.ZitadelProperties
import nl.stevenbontenbal.chorister.model.dto.AcceptInviteRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono


@RunWith(SpringRunner::class)
class ZitadelUserServiceTests {
    val zitadelProperties: ZitadelProperties = mockk()
    val exchangeFunction: ExchangeFunction = mockk()
    val webclient = WebClient.builder().exchangeFunction(exchangeFunction).build()

    @BeforeEach
    fun init() {
        clearAllMocks()
        every { zitadelProperties.baseUrl } returns "https://myurl/"
        every { zitadelProperties.adminAccessToken } returns "123abc"
    }

    @Test
    fun `when postUser then webclient does request`() {
        // Arrange
        val request = AcceptInviteRequest(
            displayName = "Test",
            email = "test@example.com",
            password = "123456",
            token = "qwerty"
        )
        every { exchangeFunction.exchange(any()) } returns Mono.just(
            ClientResponse.create(HttpStatus.OK)
                .header("content-type", "application/json")
                .body("{ \"userId\" : \"789\"}")
                .build()
        )
        val target = ZitadelUserService(zitadelProperties, webclient)

        // Act
        val actual = target.postUser(request)

        // Assert
        verify(exactly = 1) { exchangeFunction.exchange(any()) }
        actual.isSuccess shouldBe true
        actual.getOrNull() shouldBe "789"
    }

    @Test
    fun `when setEmailAddress with different email then webclient puts email`() {
        // Arrange
        val firstResponse = Mono.just(
            ClientResponse.create(HttpStatus.OK)
                .header("content-type", "application/json")
                .body("{ \"details\" : {}, \"email\": {\"email\": \"oldEmail@example.com\"}}")
                .build()
        )
        val secondResponse = Mono.just(
            ClientResponse.create(HttpStatus.OK)
                .header("content-type", "application/json")
                .body("{ \"details\" : {}}")
                .build()
        )
        every { exchangeFunction.exchange(any()) } returnsMany listOf(firstResponse, secondResponse, secondResponse)
        val target = ZitadelUserService(zitadelProperties, webclient)

        // Act
        val actual = target.setEmailAddress("789", "newEmail@example.com")

        // Assert
        verify(exactly = 3) { exchangeFunction.exchange(any()) }
        actual.isSuccess shouldBe
        actual.getOrNull() shouldBe "newEmail@example.com"
    }

    @Test
    fun `when setEmailAddress with same email then no subsequent calls`() {
        // Arrange
        val firstResponse = Mono.just(
            ClientResponse.create(HttpStatus.OK)
                .header("content-type", "application/json")
                .body("{ \"details\" : {}, \"email\": {\"email\": \"oldEmail@example.com\"}}")
                .build()
        )
        every { exchangeFunction.exchange(any()) } returns firstResponse
        val target = ZitadelUserService(zitadelProperties, webclient)

        // Act
        val actual = target.setEmailAddress("789", "oldEmail@example.com")

        // Assert
        verify(exactly = 1) { exchangeFunction.exchange(any()) }
        actual.isSuccess shouldBe
        actual.getOrNull() shouldBe "oldEmail@example.com"
    }

}