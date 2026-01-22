package nl.stevenbontenbal.chorister.application.songs

import nl.stevenbontenbal.chorister.domain.songs.Category
import nl.stevenbontenbal.chorister.domain.songs.CategoryType
import nl.stevenbontenbal.chorister.domain.songs.ICategoryRepository
import nl.stevenbontenbal.chorister.domain.songs.ICategoryTypeRepository
import nl.stevenbontenbal.chorister.domain.users.Choir

class CategorisationService(
    private val categoryRepository: ICategoryRepository,
    private val categoryTypeRepository: ICategoryTypeRepository
) {
    fun createDefaultCategories(choir: Choir) {
        val liturgicalType = CategoryType(
            choir = choir,
            name = "Liturgical part"
        )
        categoryTypeRepository.save(liturgicalType)
        val liturgicalParts = LITURGICAL_PARTS.map { Category(choir = choir, name = it, categoryType = liturgicalType) }

        val seasonType = CategoryType(
            choir = choir,
            name = "Season"
        )
        categoryTypeRepository.save(seasonType)
        val seasons = SEASONS.map { Category(choir = choir, name = it, categoryType = seasonType) }

        categoryRepository.saveAll(liturgicalParts + seasons)
    }

    companion object {
        val LITURGICAL_PARTS = arrayOf(
            "Entrance",
            "Kyrie",
            "Gloria",
            "Gospel acclamation",
            "Offertory",
            "Sanctus",
            "Lord's Prayer",
            "Agnus Dei",
            "Communion",
            "Recessional",
        )
        val SEASONS = arrayOf(
            "Ordinary Time",
            "Advent",
            "Christmas",
            "Lent",
            "Easter",
            "Pentecost",
        )
    }
}