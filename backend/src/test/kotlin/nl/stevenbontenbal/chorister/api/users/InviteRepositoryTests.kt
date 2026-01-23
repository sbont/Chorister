package nl.stevenbontenbal.chorister.api.users

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import nl.stevenbontenbal.chorister.application.users.create
import nl.stevenbontenbal.chorister.domain.users.Choir
import nl.stevenbontenbal.chorister.domain.users.Invite
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager


@DataJpaTest
class InviteRepositoryTests @Autowired constructor(
    val entityManager: TestEntityManager,
    val inviteRepository: InviteRepository,
) {
    @Test
    fun `When findAll and exists then return Invites`() {
        // Arrange
        val myChoir = Choir.create()
        entityManager.persist(myChoir)
        val invite1 = Invite.create(myChoir)
        val invite2 = Invite.create(myChoir)

        entityManager.persist(invite1.invitedBy)
        entityManager.persist(invite1)
        entityManager.persist(invite2.invitedBy)
        entityManager.persist(invite2)
        // Act
        val actual = inviteRepository.findAll()

        // Assert
        actual shouldHaveSize 2
    }

    @Test
    fun `When findByToken and exists then return Invite`() {
        // Arrange
        val myChoir = Choir.create()
        entityManager.persist(myChoir)
        val invite = Invite.create(myChoir)
        invite.token = "1234"

        entityManager.persist(invite.invitedBy)
        entityManager.persist(invite)

        // Act
        val actual = inviteRepository.findByToken("1234")

        // Assert
        actual shouldNot beNull()
    }

    @Test
    fun `When findByToken and not exists then return Invite`() {
        // Arrange
        val myChoir = Choir.create()
        entityManager.persist(myChoir)
        val invite = Invite.create(myChoir)
        invite.token = "123"

        entityManager.persist(invite.invitedBy)
        entityManager.persist(invite)

        // Act
        val actual = inviteRepository.findByToken("456")

        // Assert
        actual should beNull()
    }
}
