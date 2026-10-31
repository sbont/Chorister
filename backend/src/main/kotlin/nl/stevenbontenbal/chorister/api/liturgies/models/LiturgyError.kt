package nl.stevenbontenbal.chorister.api.liturgies.models

data class LiturgyError(
    val code: String,
    val message: String
) {
    companion object {
        const val CODE_DATE_NOT_IN_NEAR_FUTURE = "DATE_NOT_IN_NEAR_FUTURE"
        const val CODE_INVALID_DATE = "INVALID_DATE"
        const val CODE_UNEXPECTED = "UNEXPECTED"
    }
}
