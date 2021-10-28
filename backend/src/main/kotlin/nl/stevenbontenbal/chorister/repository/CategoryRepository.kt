package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.model.Category
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.security.access.prepost.PostAuthorize
import org.springframework.security.access.prepost.PostFilter

@RepositoryRestResource
interface CategoryRepository: CrudRepository<Category, Long> {
    @PostAuthorize("hasPermission(filterObject, 'read')")
    fun findByName(name: String): Category

    @PostFilter("hasPermission(filterObject, 'read')")
    override fun findAll(): Iterable<Category>
}