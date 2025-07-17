package nl.stevenbontenbal.chorister.domain.users

import org.springframework.data.repository.CrudRepository

interface IUserRepository : CrudRepository<User, Long> {
    fun findByEmail(email: String): User?

    fun findByUsername(username: String): User?

    fun findByZitadelId(zitadelId: String): User?
}