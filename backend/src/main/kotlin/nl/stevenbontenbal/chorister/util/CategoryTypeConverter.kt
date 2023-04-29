package nl.stevenbontenbal.chorister.util

import nl.stevenbontenbal.chorister.model.entities.CategoryType
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter


@Converter(autoApply = true)
class CategoryTypeConverter : AttributeConverter<CategoryType?, Char?> {
    override fun convertToDatabaseColumn(categoryType: CategoryType?): Char? {
        return categoryType?.literal
    }

    override fun convertToEntityAttribute(literal: Char?): CategoryType? {
        return if (literal == null) {
            null
        } else CategoryType.values().first { c -> c.literal == literal }
    }
}