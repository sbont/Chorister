package nl.stevenbontenbal.chorister.repository

import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.shouldNot
import nl.stevenbontenbal.chorister.model.entities.Songbook
import nl.stevenbontenbal.chorister.persist
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class SongbookRepositoryTests @Autowired constructor(
    val entityManager: TestEntityManager,
    val songbookRepository: SongbookRepository
) {
    @Test
    fun `When findByTitle then return songbook`() {
        // Arrange
        val songbook = Songbook(title = "my hymnal").persist(entityManager)
        // Act
        val result = songbookRepository.findByTitle("my hymnal")
        // Assert
        result shouldNot beNull()
    }
}