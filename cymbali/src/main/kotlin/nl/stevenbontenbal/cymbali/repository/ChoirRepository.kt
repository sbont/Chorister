package nl.stevenbontenbal.cymbali.repository

import nl.stevenbontenbal.cymbali.model.Choir
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource
interface ChoirRepository : CrudRepository<Choir, Long> {
    fun findByName(name: String): Choir?
}