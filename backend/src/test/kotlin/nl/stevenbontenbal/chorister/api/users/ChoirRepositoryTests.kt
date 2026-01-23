package nl.stevenbontenbal.chorister.api.users

import io.kotest.matchers.shouldBe
import nl.stevenbontenbal.chorister.application.users.create
import nl.stevenbontenbal.chorister.domain.users.Choir
import nl.stevenbontenbal.chorister.domain.users.IChoirRepository
import nl.stevenbontenbal.chorister.domain.users.User
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.TestPropertySource

@DataJpaTest
@TestPropertySource(properties = ["spring.jpa.hibernate.ddl-auto=create-drop"])
class ChoirRepositoryTests @Autowired constructor(
    val entityManager: TestEntityManager,
    val choirRepository: IChoirRepository,
) {
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