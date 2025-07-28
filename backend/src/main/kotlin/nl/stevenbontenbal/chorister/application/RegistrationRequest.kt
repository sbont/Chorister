package nl.stevenbontenbal.chorister.application

interface RegistrationRequest {
    var firstName: String
    var lastName: String?
    var email: String
    var password: String
}