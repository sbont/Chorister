package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.model.User
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource
interface UserRepository : CrudRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun findByUsername(username: String): User?
}