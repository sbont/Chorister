package nl.stevenbontenbal.chorister.api.songs

import nl.stevenbontenbal.chorister.domain.songs.Category
import org.springframework.context.annotation.Primary
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@Primary
@RepositoryRestResource
interface CategoryRepository: CrudRepository<Category, Long> {
    fun findByName(name: String): Category?
}