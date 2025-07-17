package nl.stevenbontenbal.chorister.api.users

import io.kotest.matchers.equals.shouldBeEqual
import nl.stevenbontenbal.chorister.domain.users.IUserRepository
import nl.stevenbontenbal.chorister.domain.users.User
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class UserRepositoryTests @Autowired constructor(
    val entityManager: TestEntityManager,
    val userRepository: IUserRepository,
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
        user?.shouldBeEqual(myUser)
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
        user?.shouldBeEqual(myUser)
    }

}