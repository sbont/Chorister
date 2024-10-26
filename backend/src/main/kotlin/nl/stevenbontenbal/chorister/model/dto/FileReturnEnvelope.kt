package nl.stevenbontenbal.chorister.model.dto

data class FileReturnEnvelope(
    var fileId: Long,
    var uploadUrl: String
)
