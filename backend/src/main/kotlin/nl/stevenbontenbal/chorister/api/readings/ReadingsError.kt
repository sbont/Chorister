package nl.stevenbontenbal.chorister.api.readings

data class ReadingsError(
    val code: String,
    val message: String
) {
    companion object {
        const val CODE_DATE_NOT_IN_NEAR_FUTURE = "DATE_NOT_IN_NEAR_FUTURE"
        const val CODE_UNEXPECTED = "UNEXPECTED"
        const val CODE_INVALID_DATE = "INVALID_DATE"
    }
}
