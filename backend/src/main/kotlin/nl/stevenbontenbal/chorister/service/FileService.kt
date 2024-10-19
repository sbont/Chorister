package nl.stevenbontenbal.chorister.service

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.sdk.kotlin.services.s3.presigners.presignPutObject
import aws.smithy.kotlin.runtime.net.url.Url
import nl.stevenbontenbal.chorister.configuration.S3Configuration
import nl.stevenbontenbal.chorister.exceptions.InvalidIdentifierException
import nl.stevenbontenbal.chorister.interfaces.ChoirOwnedEntity
import nl.stevenbontenbal.chorister.model.entities.File
import nl.stevenbontenbal.chorister.repository.FileRepository
import java.util.*
import kotlin.time.Duration.Companion.minutes

class FileService(
    private val fileRepository: FileRepository,
    private val s3Configuration: S3Configuration,
) {
    fun createFile(choirOwnedEntity: ChoirOwnedEntity): File {
        val uuid = UUID.randomUUID()
        val choirId = choirOwnedEntity.choir?.id
            ?: throw InvalidIdentifierException("Choir ID is unknown for entity $choirOwnedEntity")
        val key = "$choirId-$uuid"
        val file = File(choir = choirOwnedEntity.choir, s3Key = key)
        fileRepository.save(file)
        return file
    }

    suspend fun getUploadUrl(file: File): String {
        val fileKey = file.s3Key ?: throw InvalidIdentifierException("Key is unknown for file with id ${file.id}")
        val client = getClient()
        val request = PutObjectRequest {
            bucket = s3Configuration.bucketName
            key = fileKey
        }

        val presigned = client.presignPutObject(request, 15.minutes)
        return presigned.url.toString()
    }
//
//    suspend fun getDownloadUrl(fileKey: String) {
//        val client = getClient()
//        val request = GetObjectRequest {
//            bucket = s3Configuration.bucketName
//            key = fileKey
//        }
//        val url = request.
//        presigned.url.toString()
//    }

    private suspend fun getClient() = S3Client.fromEnvironment { endpointUrl = clientUrl() }

    private fun clientUrl() = Url.parse(s3Configuration.endpointUrl)
}