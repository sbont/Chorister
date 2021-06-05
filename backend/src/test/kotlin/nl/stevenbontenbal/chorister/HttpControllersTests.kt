package nl.stevenbontenbal.chorister

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
@WebMvcTest
class HttpControllersTests(@Autowired val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var userRepository: UserRepository

    @MockkBean
    private lateinit var songRepository: SongRepository

    @Test
    fun `List articles`() {
        val juergen = User("springjuergen@gmail.com", "Juergen")
        val song1 = Song("You'll never walk alone", "Richard Rodgers", "https://www.youtube.com/watch?v=OV5_LQArLa0", "Richard Rodgers", juergen)
        val song2 = Song("You might ever walk alone", "Richard Rodgers", "https://www.youtube.com/watch?v=OV5_LQArLa0", "Richard Rodgers", juergen)
        every { songRepository.findAllByOrderByAddedAtDesc() } returns listOf(song1, song2)
        mockMvc.perform(get("/api/song/").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$.[0].addedBy.email").value(juergen.email))
            .andExpect(jsonPath("\$.[0].title").value(song1.title))
            .andExpect(jsonPath("\$.[1].addedBy.email").value(juergen.email))
            .andExpect(jsonPath("\$.[1].title").value(song2.title))
    }

    @Test
    fun `List users`() {
        val juergen = User("springjuergen@gmail.com", "Juergen")
        val smaldini = User("smaldini@gmail.com", "Stéphane")
        every { userRepository.findAll() } returns listOf(juergen, smaldini)
        mockMvc.perform(get("/api/user/").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$.[0].email").value(juergen.email))
            .andExpect(jsonPath("\$.[1].email").value(smaldini.email))
    }
}