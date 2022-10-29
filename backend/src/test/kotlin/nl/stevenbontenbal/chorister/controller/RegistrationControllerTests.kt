package nl.stevenbontenbal.chorister.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import nl.stevenbontenbal.chorister.configuration.ChoristerConfiguration
import nl.stevenbontenbal.chorister.model.entities.User
import nl.stevenbontenbal.chorister.model.dto.NewChoirRegistrationRequest
import nl.stevenbontenbal.chorister.service.RegistrationService
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@RunWith(SpringRunner::class)
@WebMvcTest(RegistrationController::class)
@ContextConfiguration(classes = [ChoristerConfiguration::class])
class RegistrationControllerTests {
    @Autowired
    private val mvc: MockMvc? = null

    @MockK
    lateinit var registrationService: RegistrationService

    @Test
    fun `When register then service layer is called`() {
        // Arrange
        val request = NewChoirRegistrationRequest(
            "Me", "me@chorister.io", "1234", "MyChoir", choirType = "Band"
        )
        every { registrationService.register(any()) } returns User(email = "me@chorister.io", username = "me@chorister.io", displayName = "Me")

        // Act / Assert
        mvc!!.perform(
            post("/api/registration")
            .content(jacksonObjectMapper().writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
        verify { registrationService.register(any()) }
    }
}