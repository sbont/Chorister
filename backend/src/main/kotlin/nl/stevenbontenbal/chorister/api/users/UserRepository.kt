package nl.stevenbontenbal.chorister.api.users

import nl.stevenbontenbal.chorister.domain.users.User
import org.springframework.context.annotation.Primary
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource
import org.springframework.security.access.prepost.PreAuthorize
import java.util.Optional

@Primary
@RepositoryRestResource
interface UserRepository : CrudRepository<User, Long> {
    @RestResource(exported = false)
    override fun findById(id: Long): Optional<User?>

    @RestResource(exported = false)
    override fun findAll(): Iterable<User?>

    @PreAuthorize("hasRole('VIEWER')")
    override fun <T : User> save(entity: T): T

    @PreAuthorize("hasRole('MANAGER')")
    override fun delete(entity: User)
}