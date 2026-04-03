package nl.stevenbontenbal.chorister.application.readings.universalis

import kotlinx.serialization.Serializable

@Serializable
data class UniversalisReadings(
    val number: Long,
    val date: String,
    val day: String,
    val Mass_R1: Reading,
    val Mass_Ps: Reading,
    val Mass_GA: Reading? = null,
    val Mass_G: Reading,
    val copyright: Copyright,
    val Mass_R2: Reading? = null,
    val Mass_Ps2: Reading? = null,
    val Mass_R3: Reading? = null,
    val Mass_Ps3: Reading? = null,
    val Mass_R4: Reading? = null,
    val Mass_Ps4: Reading? = null,
    val Mass_R5: Reading? = null,
    val Mass_Ps5: Reading? = null,
    val Mass_R6: Reading? = null,
    val Mass_Ps6: Reading? = null,
    val Mass_R7: Reading? = null,
    val Mass_Ps7: Reading? = null,
    val Mass_R8: Reading? = null,
    val Mass_Ps8: Reading? = null,
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
