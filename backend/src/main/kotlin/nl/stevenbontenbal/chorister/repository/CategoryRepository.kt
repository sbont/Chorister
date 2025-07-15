package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.model.entities.Category
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource


@RepositoryRestResource
interface CategoryRepository: CrudRepository<Category, Long> {
    fun findByName(name: String): Category?
}