package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.create
import nl.stevenbontenbal.chorister.model.Choir
import nl.stevenbontenbal.chorister.model.Song
import org.assertj.core.api.Assertions
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
        Assertions.assertThat(songs.count()).isEqualTo(2)
    }

}