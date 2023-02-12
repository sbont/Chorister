package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.model.entities.Choir
import nl.stevenbontenbal.chorister.model.entities.Setlist
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.security.access.prepost.PostAuthorize
import org.springframework.security.access.prepost.PostFilter

@RepositoryRestResource
interface ChoirRepository : CrudRepository<Choir, Long> {
    @PostFilter("hasPermission(filterObject, 'read')")
    override fun findAll(): Iterable<Choir>

    @PostAuthorize("hasPermission(filterObject, 'read')")
    fun findByName(name: String): Choir?

    @PostAuthorize("hasPermission(filterObject, 'read')")
    fun findByInviteToken(inviteToken: String): Choir?
}