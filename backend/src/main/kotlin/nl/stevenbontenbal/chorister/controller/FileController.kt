package nl.stevenbontenbal.chorister.controller

import nl.stevenbontenbal.chorister.model.dto.FileReturnEnvelope
import nl.stevenbontenbal.chorister.model.entities.File
import nl.stevenbontenbal.chorister.service.FileService
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.server.ExposesResourceFor
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.net.URI

@BasePathAwareController
@ExposesResourceFor(File::class)
class FileController(
    private val fileService: FileService
) {
    @GetMapping("/files/new-upload")
    suspend fun getUploadUrl(): ResponseEntity<FileReturnEnvelope> {
        val file = fileService.createFile()
        return getUploadUrl(file)
    }

    @GetMapping("/files/{id}/upload")
    suspend fun getUploadUrl(@PathVariable id: Long?): ResponseEntity<FileReturnEnvelope> {
        val file = id?.let { fileService.getFile(it) } ?: return ResponseEntity.notFound().build()
        return getUploadUrl(file)
    }

    @GetMapping("/files/old/{id}")
    fun getFileOld(@PathVariable id: Long?): ResponseEntity<File?> {
        val file = id?.let { fileService.getFile(it) } ?: return ResponseEntity.notFound().build()
        val accelRedirectHost = fileService.s3RedirectHost()
        val accelRedirectPath = fileService.s3RedirectPath(file)
        return ResponseEntity
            .ok()
            .header(ACCEL_REDIRECT_HOST, accelRedirectHost)
            .header(ACCEL_REDIRECT_PATH, accelRedirectPath)
            .body(file)
    }

    @GetMapping("/files/{id}")
    suspend fun getFile(@PathVariable id: Long?): ResponseEntity<Any> {
        val file = id?.let { fileService.getFile(it) } ?: return ResponseEntity.notFound().build()
        val key = file.s3Key ?: return ResponseEntity.internalServerError().body("File key not found")
        val downloadUrl = fileService.getDownloadUrl(key)
        return ResponseEntity.status(HttpStatus.CREATED)
            .location(URI.create(downloadUrl))
            .build()
    }

    private suspend fun getUploadUrl(file: File): ResponseEntity<FileReturnEnvelope> {
        val uploadUrl = fileService.getUploadUrl(file)
        val response = FileReturnEnvelope(file.id!!, uploadUrl)
        return ResponseEntity
            .ok()
            .body(response)
    }

    companion object {
        const val ACCEL_REDIRECT_HOST = "X-Accel-Redirect-Host"
        const val ACCEL_REDIRECT_PATH = "X-Accel-Redirect-Path"
    }
}