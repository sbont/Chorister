package nl.stevenbontenbal.chorister.domain.users

import org.springframework.data.repository.CrudRepository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

interface IUserRepository : CrudRepository<User, Long> {
    fun findByEmail(email: String): User?

    fun findByUsername(username: String): User?

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun findByZitadelId(zitadelId: String): User?

    fun findByChoirId(choirId: Long): List<User>
}