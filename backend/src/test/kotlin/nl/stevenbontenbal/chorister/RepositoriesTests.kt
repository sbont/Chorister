package nl.stevenbontenbal.chorister

import nl.stevenbontenbal.chorister.repository.SongRepository
import nl.stevenbontenbal.chorister.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class RepositoriesTests @Autowired constructor(
    val entityManager: TestEntityManager,
    val userRepository: UserRepository,
    val songRepository: SongRepository
) {

    /*@Test
    fun `When findByIdOrNull then return Article`() {
        val juergen = User("springjuergen@gmail.com", "Juergen")
        entityManager.persist(juergen)
        val song = Song("You'll never walk alone", "Richard Rodgers", "https://www.youtube.com/watch?v=OV5_LQArLa0", "Richard Rodgers", juergen)
        entityManager.persist(song)
        entityManager.flush()
        val found = songRepository.findByIdOrNull(song.id!!)
        assertThat(found).isEqualTo(song)
    }

    @Test
    fun `When findByLogin then return User`() {
        val juergen = User("springjuergen@gmail.com", "Juergen")
        entityManager.persist(juergen)
        entityManager.flush()
        val user = userRepository.findByEmail(juergen.email)
        assertThat(user).isEqualTo(juergen)
    }*/
}