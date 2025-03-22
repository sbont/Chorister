package nl.stevenbontenbal.chorister.repository

import io.kotest.matchers.shouldBe
import nl.stevenbontenbal.chorister.create
import nl.stevenbontenbal.chorister.model.entities.Choir
import nl.stevenbontenbal.chorister.model.entities.User
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.TestPropertySource

@DataJpaTest
@TestPropertySource(properties = ["spring.jpa.hibernate.ddl-auto=create-drop"])
class ChoirRepositoryTests @Autowired constructor(
    val entityManager: TestEntityManager,
    val choirRepository: ChoirRepository,
) {
    @Test
    fun `When findByName and exists then return Choir`() {
        // Arrange
        val manager = User.create()
        entityManager.persist(manager)
        val myChoir = Choir(
            name = "MyChoir",
            type = "Band",
            manager = manager)
        val otherChoir = Choir(
            name = "OtherChoir",
            type = "Band",
            manager = null)
        entityManager.persist(myChoir)
        entityManager.persist(otherChoir)
        // Act
        val actual = choirRepository.findByName(myChoir.name)
        // Assert
        actual!!.id shouldBe myChoir.id
    }

    @Test
    fun `When findByName and not exists then return null`() {
        // Arrange
        val otherChoir = Choir.create(null)
        entityManager.persist(otherChoir)
        // Act
        val actual = choirRepository.findByName("Non-Existent Choir")
        // Assert
        actual shouldBe null
    }
    @Test
    fun `When findByInviteToken and exists then return Choir`() {
        // Arrange
        val manager = User.create()
        entityManager.persist(manager)
        val myChoir = Choir(
            name = "MyChoir",
            type = "Band",
            manager = manager,
            inviteToken = "1234")
        val otherChoir = Choir(
            name = "OtherChoir",
            type = "Band",
            manager = null,
            inviteToken = "12345")
        entityManager.persist(myChoir)
        entityManager.persist(otherChoir)
        // Act
        val actual = choirRepository.findByInviteToken("1234")
        // Assert
        actual!!.id shouldBe myChoir.id
    }

    @Test
    fun `When findByInviteToken and not exists then return null`() {
        // Arrange
        val otherChoir = Choir.create(null)
        entityManager.persist(otherChoir)
        // Act
        val actual = choirRepository.findByInviteToken("no-real-token")
        // Assert
        actual shouldBe null
    }
}