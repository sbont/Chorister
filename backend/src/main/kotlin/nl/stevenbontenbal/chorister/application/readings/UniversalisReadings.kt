package nl.stevenbontenbal.chorister.application.readings

import kotlinx.serialization.Serializable

@Serializable
data class UniversalisReadings(
    val number: Long,
    val date: String,
    val day: String,
    val Mass_R1: Reading,
    val Mass_Ps: Reading,
    val Mass_R2: Reading?,
    val Mass_GA: Reading,
    val Mass_G: Reading,
    val copyright: Copyright
)

@Serializable
data class Reading(
    val heading: String?,
    val source: String,
    val text: String
)

@Serializable
data class Copyright(
    val text: String
)
