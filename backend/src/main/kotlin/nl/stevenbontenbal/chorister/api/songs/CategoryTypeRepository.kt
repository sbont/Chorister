package nl.stevenbontenbal.chorister.api.songs
import nl.stevenbontenbal.chorister.domain.songs.CategoryType
import org.springframework.context.annotation.Primary
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.security.access.prepost.PreAuthorize

@Primary
@RepositoryRestResource
interface CategoryTypeRepository : CrudRepository<CategoryType, Long> {
    @PreAuthorize("hasRole('MANAGER')")
    override fun <T : CategoryType> save(entity: T): T

    @PreAuthorize("hasRole('MANAGER')")
    override fun delete(entity: CategoryType)

    fun findByName(name: String): CategoryType?
}