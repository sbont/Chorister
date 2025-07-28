package nl.stevenbontenbal.chorister.api.users

import nl.stevenbontenbal.chorister.domain.users.User
import org.springframework.context.annotation.Primary
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource
import org.springframework.security.access.prepost.PostAuthorize
import org.springframework.security.access.prepost.PostFilter
import java.util.*

@Primary
@RepositoryRestResource
interface UserRepository : CrudRepository<User, Long> {
    @PostFilter("hasPermission(filterObject, 'read')")
    override fun findAll(): Iterable<User>

    @PostAuthorize("hasPermission(returnObject, 'read')")
    override fun findById(id: Long): Optional<User>

    @RestResource(exported = false)
    fun findByEmail(email: String): User?

}