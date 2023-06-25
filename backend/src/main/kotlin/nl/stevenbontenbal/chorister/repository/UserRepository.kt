package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.model.entities.User
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource
import org.springframework.security.access.prepost.PostAuthorize
import org.springframework.security.access.prepost.PostFilter
import java.util.*

@RepositoryRestResource
interface UserRepository : CrudRepository<User, Long> {
    @PostFilter("hasPermission(filterObject, 'read')")
    override fun findAll(): Iterable<User>

    @PostAuthorize("hasPermission(returnObject, 'read')")
    override fun findById(id: Long): Optional<User>

    @RestResource(exported = false)
    fun findByEmail(email: String): User?

    @RestResource(exported = false)
    fun findByUsername(username: String): User?

    @RestResource(exported = false)
    fun findByZitadelId(zitadelId: String): User?
}