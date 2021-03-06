package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.create
import nl.stevenbontenbal.chorister.model.Category
import nl.stevenbontenbal.chorister.model.CategoryType
import nl.stevenbontenbal.chorister.model.Choir
import nl.stevenbontenbal.chorister.model.User
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class ChoirRepositoryTests @Autowired constructor(
    val entityManager: TestEntityManager,
    val choirRepository: ChoirRepository,
) {
    @Test
    fun `When findByName and exists then return Choir`() {
        // Arrange
        val manager = User.create()
        val myChoir = Choir.create(manager)
        val otherChoir = Choir.create(null)
        entityManager.persist(myChoir)
        entityManager.persist(otherChoir)
        // Act
        val actual = choirRepository.findByName(myChoir.name)
        // Assert
        Assertions.assertThat(actual!!.id).isEqualTo(myChoir.id)
    }

    @Test
    fun `When findByName and not exists then return null`() {
        // Arrange
        val otherChoir = Choir.create(null)
        entityManager.persist(otherChoir)
        // Act
        val actual = choirRepository.findByName("Non-Existent Choir")
        // Assert
        Assertions.assertThat(actual).isNull()
    }
}