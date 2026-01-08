package nl.stevenbontenbal.chorister.api.users

import nl.stevenbontenbal.chorister.domain.users.User
import org.springframework.context.annotation.Primary
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.security.access.prepost.PreAuthorize

@Primary
@RepositoryRestResource
interface UserRepository : CrudRepository<User, Long> {
    @PreAuthorize("hasRole('VIEWER')")
    override fun <T : User> save(entity: T): T

    @PreAuthorize("hasRole('MANAGER')")
    override fun delete(entity: User)
}