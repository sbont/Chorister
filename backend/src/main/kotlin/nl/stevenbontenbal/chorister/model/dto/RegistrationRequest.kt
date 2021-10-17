package nl.stevenbontenbal.chorister.model.dto

import org.jetbrains.annotations.NotNull

data class RegistrationRequest (
    var displayName: String,
    var email: String,
    var password: String,
    var choirName: String,
    var choirType: String = "Choir")