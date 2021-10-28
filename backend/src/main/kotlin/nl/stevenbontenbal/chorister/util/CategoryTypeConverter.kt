package nl.stevenbontenbal.chorister.util

import nl.stevenbontenbal.chorister.model.CategoryType
import javax.persistence.AttributeConverter
import javax.persistence.Converter


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