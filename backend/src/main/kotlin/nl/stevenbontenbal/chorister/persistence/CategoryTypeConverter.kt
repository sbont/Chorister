package nl.stevenbontenbal.chorister.persistence

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import nl.stevenbontenbal.chorister.domain.songs.CategoryType

@Converter(autoApply = true)
class CategoryTypeConverter : AttributeConverter<CategoryType?, Char?> {
    override fun convertToDatabaseColumn(categoryType: CategoryType?): Char? {
        return categoryType?.literal
    }

    override fun convertToEntityAttribute(literal: Char?): CategoryType? {
        return if (literal == null) {
            null
        } else CategoryType.entries.first { c -> c.literal == literal }
    }
}