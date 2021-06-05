package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.model.Choir
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource
interface ChoirRepository : CrudRepository<Choir, Long> {
    fun findByName(name: String): Choir?
}