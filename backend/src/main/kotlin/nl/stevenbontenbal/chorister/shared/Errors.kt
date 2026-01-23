package nl.stevenbontenbal.chorister.shared

interface ChoristerError

interface Validation : ChoristerError {
    object NotFound : Validation
}

interface Failure : ChoristerError {
    data class InvalidOperation(
        val message: String
    ) : Failure
}
