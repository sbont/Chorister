package nl.stevenbontenbal.chorister.api.songs

import io.kotest.matchers.collections.shouldHaveSize
import nl.stevenbontenbal.chorister.application.songs.create
import nl.stevenbontenbal.chorister.application.users.create
import nl.stevenbontenbal.chorister.domain.songs.Song
import nl.stevenbontenbal.chorister.domain.users.Choir
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class SongRepositoryTests @Autowired constructor(
    val entityManager: TestEntityManager,
    val songRepository: SongRepository,
) {
    @Test
    fun `When findAll then return all Songs`() {
        // Arrange
        val choir = Choir.create()
        entityManager.persist(choir)
        val song1 = Song.create(choir)
        val song2 = Song.create(choir)
        entityManager.persist(song1)
        entityManager.persist(song2)
        // Act
        val songs = songRepository.findAll()
        // Assert
        songs shouldHaveSize 2
    }

}