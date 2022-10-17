package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.model.entities.Invite
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.security.access.prepost.PostFilter

@RepositoryRestResource
interface InviteRepository : CrudRepository<Invite, Long> {

    @PostFilter("hasPermission(filterObject, 'read')")
    override fun findAll(): Iterable<Invite>

    fun findByToken(token: String): Invite?
}