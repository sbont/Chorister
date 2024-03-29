package nl.stevenbontenbal.chorister.service

import nl.stevenbontenbal.chorister.configuration.ChoristerProperties
import nl.stevenbontenbal.chorister.model.entities.Category
import nl.stevenbontenbal.chorister.model.entities.CategoryType
import nl.stevenbontenbal.chorister.model.entities.Choir
import nl.stevenbontenbal.chorister.repository.CategoryRepository

class CategorisationService(
    private val properties: ChoristerProperties,
    private val categoryRepository: CategoryRepository
) {
    fun createDefaultCategories(choir: Choir) {
        val categories = properties.defaultCategories.toCategories(choir)
        categoryRepository.saveAll(categories)
    }

    private fun ChoristerProperties.DefaultCategories.toCategories(choir: Choir): Iterable<Category> {
        val categories = mutableListOf<Category>()
        categories.addAll(this.liturgicalMoment.map {
            Category(
                choir = choir,
                name = it,
                type = CategoryType.LITURGICAL_MOMENT
            )
        })
        categories.addAll(this.season.map {
            Category(
                choir = choir,
                name = it,
                type = CategoryType.SEASON
            )
        })
        return categories
    }


}