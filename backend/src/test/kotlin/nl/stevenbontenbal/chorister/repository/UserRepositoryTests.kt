package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.model.entities.User
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class UserRepositoryTests @Autowired constructor(
    val entityManager: TestEntityManager,
    val userRepository: UserRepository,
) {
    @Test
    fun `When findByUsername then return User`() {
        // Arrange
        val myUser = User(email = "johnny@email.com", username = "johnnyJ", displayName = "JohnnyJ")
        entityManager.persist(myUser)
        entityManager.flush()
        // Act
        val user = userRepository.findByUsername(myUser.username)
        // Assert
        assertThat(user).isEqualTo(myUser)
    }

    @Test
    fun `When findByEmail then return User`() {
        // Arrange
        val myUser = User(email = "johnny@email.com", username = "johnnyJ", displayName = "JohnnyJ")
        entityManager.persist(myUser)
        entityManager.flush()
        // Act
        val user = myUser.email?.let { userRepository.findByEmail(it) }
        // Assert
        assertThat(user).isEqualTo(myUser)
    }

}