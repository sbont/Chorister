package nl.stevenbontenbal.cymbali.repository

import nl.stevenbontenbal.cymbali.model.User
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource
interface UserRepository : CrudRepository<User, Long> {
    fun findByEmail(login: String): User?
}