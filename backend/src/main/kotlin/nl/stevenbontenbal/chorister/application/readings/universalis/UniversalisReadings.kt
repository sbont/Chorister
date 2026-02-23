package nl.stevenbontenbal.chorister.application.readings.universalis

import kotlinx.serialization.Serializable

@Serializable
data class UniversalisReadings(
    val number: Long,
    val date: String,
    val day: String,
    val Mass_R1: Reading,
    val Mass_Ps: Reading,
    val Mass_GA: Reading,
    val Mass_G: Reading,
    val copyright: Copyright,
    val Mass_R2: Reading? = null,
)

@Serializable
data class Reading(
    val source: String? = null,
    val text: String,
    val heading: String? = null,
)

@Serializable
data class Copyright(
    val text: String
)
