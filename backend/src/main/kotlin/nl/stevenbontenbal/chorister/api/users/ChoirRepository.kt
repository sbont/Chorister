package nl.stevenbontenbal.chorister.api.users

import nl.stevenbontenbal.chorister.domain.users.Choir
import org.springframework.context.annotation.Primary
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@Primary
@RepositoryRestResource
interface ChoirRepository : CrudRepository<Choir, Long> {
    fun findByName(name: String): Choir?
}