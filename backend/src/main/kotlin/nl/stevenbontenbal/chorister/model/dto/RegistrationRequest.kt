package nl.stevenbontenbal.chorister.model.dto

import org.jetbrains.annotations.NotNull

interface RegistrationRequest {
    var displayName: String
    var email: String
    var password: String
}