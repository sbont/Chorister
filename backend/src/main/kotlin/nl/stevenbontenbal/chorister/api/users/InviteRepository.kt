package nl.stevenbontenbal.chorister.api.users

import nl.stevenbontenbal.chorister.domain.users.Invite
import org.springframework.context.annotation.Primary
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.security.access.prepost.PreAuthorize

@Primary
@RepositoryRestResource
interface InviteRepository : CrudRepository<Invite, Long> {
    @PreAuthorize("hasRole('MANAGER')")
    override fun <T : Invite> save(entity: T): T

    @PreAuthorize("hasRole('MANAGER')")
    override fun delete(entity: Invite)

    fun findByToken(token: String): Invite?
}