package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.model.entities.Invite
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource
import org.springframework.security.access.prepost.PostFilter

@RepositoryRestResource
interface InviteRepository : CrudRepository<Invite, Long> {
    @RestResource
    @PostFilter("hasPermission(filterObject, 'read')")
    override fun findAll(): Iterable<Invite>

    @RestResource
    fun findByToken(token: String): Invite?
}