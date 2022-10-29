package nl.stevenbontenbal.chorister.model.entities

import nl.stevenbontenbal.chorister.exceptions.InvalidInputException
import org.springframework.core.convert.converter.Converter

class SetlistEntryIdConverter: Converter<String, SetlistEntryId> {

    override fun convert(source: String): SetlistEntryId {
        val parts = source.split(":")
        if (parts.size != 2)
            throw InvalidInputException("Incorrect identifier provided: $source")

        return SetlistEntryId(parts[0].toLong(), parts[1].toLong())
    }

}
