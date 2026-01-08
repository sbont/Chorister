package nl.stevenbontenbal.chorister.api.songs

import nl.stevenbontenbal.chorister.domain.songs.Category
import org.springframework.context.annotation.Primary
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.security.access.prepost.PreAuthorize

@Primary
@RepositoryRestResource
interface CategoryRepository: CrudRepository<Category, Long> {
    @PreAuthorize("hasRole('MANAGER')")
    override fun <T : Category> save(entity: T): T

    @PreAuthorize("hasRole('MANAGER')")
    override fun delete(entity: Category)

    fun findByName(name: String): Category?
}