package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.create
import nl.stevenbontenbal.chorister.model.Choir
import nl.stevenbontenbal.chorister.model.Invite
import org.assertj.core.api.Assertions.assertThat
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
        assertThat(actual.count()).isEqualTo(2)
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
        assertThat(actual).isNotNull
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
        assertThat(actual).isNull()
    }
}
