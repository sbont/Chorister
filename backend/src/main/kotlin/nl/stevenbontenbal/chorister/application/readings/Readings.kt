package nl.stevenbontenbal.chorister.application.readings

import kotlinx.serialization.Serializable

@Serializable
data class Readings(
    val date: String,
    val day: String,
    val reading1: Reading,
    val psalm: Reading,
    val reading2: Reading?,
    val gospelAcclamation: Reading,
    val gospelReading: Reading,
    val copyright: Copyright
)

@Serializable
data class Reading(
    val source: String?,
    val text: String
)

@Serializable
data class Copyright(
    val text: String
)
