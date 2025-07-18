package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.model.entities.Invite
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource
interface InviteRepository : CrudRepository<Invite, Long> {
    fun findByToken(token: String): Invite?
}