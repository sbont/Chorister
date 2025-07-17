package nl.stevenbontenbal.chorister.application

interface RegistrationRequest {
    var displayName: String
    var email: String
    var password: String
}