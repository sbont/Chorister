package nl.stevenbontenbal.chorister.service

import nl.stevenbontenbal.chorister.configuration.ChoristerProperties
import nl.stevenbontenbal.chorister.model.Category
import nl.stevenbontenbal.chorister.model.CategoryType
import nl.stevenbontenbal.chorister.model.Choir
import nl.stevenbontenbal.chorister.repository.CategoryRepository

class CategorisationService(
    private val properties: ChoristerProperties,
    private val categoryRepository: CategoryRepository
) {
    fun createDefaultCategories(choir: Choir) {
        val categories = properties.defaultCategories.toCategories()
        categories.forEach { it.choir = choir }
        categoryRepository.saveAll(categories)
    }

    private fun ChoristerProperties.DefaultCategories.toCategories(): Iterable<Category> {
        val categories = mutableListOf<Category>()
        categories.addAll(this.liturgicalMoment.map {
            Category(
                name = it,
                type = CategoryType.LITURGICAL_MOMENT
            )
        })
        categories.addAll(this.season.map {
            Category(
                name = it,
                type = CategoryType.SEASON
            )
        })
        return categories
    }


}