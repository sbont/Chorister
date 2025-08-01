package nl.stevenbontenbal.chorister.api.users

import nl.stevenbontenbal.chorister.domain.users.Invite
import org.springframework.context.annotation.Primary
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@Primary
@RepositoryRestResource
interface InviteRepository : CrudRepository<Invite, Long> {
    fun findByToken(token: String): Invite?
}