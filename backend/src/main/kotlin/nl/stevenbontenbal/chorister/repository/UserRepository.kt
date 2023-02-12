package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.model.entities.User
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.security.access.prepost.PostAuthorize
import org.springframework.security.access.prepost.PostFilter

@RepositoryRestResource
interface UserRepository : CrudRepository<User, Long> {
    @PostFilter("hasPermission(filterObject, 'read')")
    override fun findAll(): Iterable<User>

    @PostAuthorize("hasPermission(filterObject, 'read')")
    fun findByEmail(email: String): User?

    @PostAuthorize("hasPermission(filterObject, 'read')")
    fun findByUsername(username: String): User?
}