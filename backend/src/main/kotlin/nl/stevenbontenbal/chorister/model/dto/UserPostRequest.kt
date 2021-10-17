package nl.stevenbontenbal.chorister.model.dto

class UserPostRequest (
    var credentials: MutableList<Credential>?,
    var email: String?,
    var enabled: Boolean? = true,
    var firstName: String?,
    var lastName: String?,
    var username: String) {

    class Credential(
        var type: String,
        var value: String,
        var temporary: Boolean? = false
    )
}