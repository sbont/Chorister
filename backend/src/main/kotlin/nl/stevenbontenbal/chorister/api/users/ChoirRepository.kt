package nl.stevenbontenbal.chorister.api.users

import nl.stevenbontenbal.chorister.domain.users.Choir
import org.springframework.context.annotation.Primary
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.security.access.prepost.PreAuthorize

@Primary
@RepositoryRestResource
interface ChoirRepository : CrudRepository<Choir, Long> {
    @PreAuthorize("hasRole('MANAGER')")
    override fun <T : Choir> save(entity: T): T

    @PreAuthorize("hasRole('MANAGER')")
    override fun delete(entity: Choir)
}