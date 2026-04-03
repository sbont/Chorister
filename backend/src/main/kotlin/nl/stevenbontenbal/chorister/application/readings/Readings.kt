package nl.stevenbontenbal.chorister.application.readings

import kotlinx.serialization.Serializable

@Serializable
data class Readings(
    val date: String,
    val day: String,
    val reading1: Reading,
    val psalm: Reading,
    val reading2: Reading?,
    val psalm2: Reading?,
    val reading3: Reading?,
    val psalm3: Reading?,
    val reading4: Reading?,
    val psalm4: Reading?,
    val reading5: Reading?,
    val psalm5: Reading?,
    val reading6: Reading?,
    val psalm6: Reading?,
    val reading7: Reading?,
    val psalm7: Reading?,
    val reading8: Reading?,
    val psalm8: Reading?,
    val gospelAcclamation: Reading?,
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
