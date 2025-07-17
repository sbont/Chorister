package nl.stevenbontenbal.chorister.api.songs.models

data class FileReturnEnvelope(
    var fileId: Long,
    var uploadUrl: String
)