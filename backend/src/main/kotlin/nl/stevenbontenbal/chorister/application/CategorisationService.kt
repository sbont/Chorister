package nl.stevenbontenbal.chorister.application

import nl.stevenbontenbal.chorister.application.config.ChoristerProperties
import nl.stevenbontenbal.chorister.domain.songs.Category
import nl.stevenbontenbal.chorister.domain.songs.CategoryType
import nl.stevenbontenbal.chorister.domain.songs.ICategoryRepository
import nl.stevenbontenbal.chorister.domain.users.Choir

class CategorisationService(
    private val properties: ChoristerProperties,
    private val categoryRepository: ICategoryRepository
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