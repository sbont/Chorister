package nl.stevenbontenbal.chorister.application

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.DeleteObjectRequest
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.sdk.kotlin.services.s3.presigners.presignGetObject
import aws.sdk.kotlin.services.s3.presigners.presignPutObject
import aws.smithy.kotlin.runtime.net.url.Url
import nl.stevenbontenbal.chorister.application.config.S3Configuration
import nl.stevenbontenbal.chorister.domain.InvalidIdentifierException
import nl.stevenbontenbal.chorister.domain.songs.File
import nl.stevenbontenbal.chorister.domain.songs.IFileRepository
import nl.stevenbontenbal.chorister.domain.songs.IFileService
import nl.stevenbontenbal.chorister.domain.users.UserService
import java.net.URI
import java.util.*
import kotlin.jvm.optionals.getOrNull
import kotlin.time.Duration.Companion.minutes

class FileService(
    private val fileRepository: IFileRepository,
    private val s3Configuration: S3Configuration,
    private val userService: UserService,
) : IFileService {
    override fun createFile(): File {
        val choir = userService.currentUser.choir
            ?: throw InvalidIdentifierException("Choir ID is unknown for current user")
        val uuid = UUID.randomUUID()
        val key = "${choir.id}/$uuid"
        val file = File(choir = choir, s3Key = key)
        fileRepository.save(file)
        return file
    }

    override suspend fun getUploadUrl(file: File): String {
        val fileKey = file.s3Key ?: throw InvalidIdentifierException("Key is unknown for file with id ${file.id}")
        val client = getClient()
        val request = PutObjectRequest {
            bucket = s3Configuration.bucketName
            key = fileKey
        }
        val presigned = client.presignPutObject(request, 15.minutes)
        return presigned.url.toString()
    }

    override fun getFile(id: Long): File? {
        return fileRepository
            .findById(id)
            .filter{ userService.hasAccess(it) }
            .getOrNull()
    }

    override fun s3RedirectHost(): String = URI(s3Configuration.endpointUrl).toURL().host

    override fun s3RedirectPath(file: File): String = "${s3Configuration.bucketName}/${file.s3Key}"

    override fun s3RedirectUrlPathSafe(file: File): String {
        val baseUrl = URI(s3Configuration.endpointUrl).toURL()
        val root = baseUrl.host +
                (if (baseUrl.port > -1) ":${baseUrl.port}" else "") +
                (if (baseUrl.path.isNotEmpty()) "/${baseUrl.path}" else "")

        return "/s3-download/${baseUrl.protocol}/$root/${s3Configuration.bucketName}/${file.s3Key}"
    }

    override suspend fun getDownloadUrl(fileKey: String): String {
        val client = getClient()
        val request = GetObjectRequest {
            bucket = s3Configuration.bucketName
            key = fileKey
            responseContentDisposition = "inline"
        }
        val presigned = client.presignGetObject(request, 2.minutes)
        return presigned.url.toString()
    }

    override suspend fun delete(file: File) {
        val client = getClient()
        val request = DeleteObjectRequest {
            bucket = s3Configuration.bucketName
            key = file.s3Key
        }
        client.deleteObject(request)
    }

    private suspend fun getClient() = S3Client.fromEnvironment {
        endpointUrl = clientUrl()
        region = s3Configuration.region
        credentialsProvider = StaticCredentialsProvider {
            accessKeyId = s3Configuration.accessKey
            secretAccessKey = s3Configuration.secretKey
        }
        forcePathStyle = true
    }

    private fun clientUrl() = Url.parse(s3Configuration.endpointUrl)

}