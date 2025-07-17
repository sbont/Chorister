package nl.stevenbontenbal.chorister.domain.songs

interface IFileService {
    fun createFile(): File

    suspend fun getUploadUrl(file: File): String

    fun getFile(id: Long): File?

    fun s3RedirectHost(): String

    fun s3RedirectPath(file: File): String

    fun s3RedirectUrlPathSafe(file: File): String

    suspend fun getDownloadUrl(fileKey: String): String

    suspend fun delete(file: File)
}