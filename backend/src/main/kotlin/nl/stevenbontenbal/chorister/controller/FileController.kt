package nl.stevenbontenbal.chorister.controller

import nl.stevenbontenbal.chorister.model.dto.FileReturnEnvelope
import nl.stevenbontenbal.chorister.model.entities.File
import nl.stevenbontenbal.chorister.service.FileService
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.hateoas.server.ExposesResourceFor
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@RepositoryRestController()
@ExposesResourceFor(File::class)
class FileController(
    private val fileService: FileService
) {
    @GetMapping("/files/new-upload")
    suspend fun getUploadUrl(): FileReturnEnvelope {
        val file = fileService.createFile()
        val uploadUrl = fileService.getUploadUrl(file)
        return FileReturnEnvelope(file.id!!, uploadUrl)
    }

    @GetMapping("/files/{id}")
    fun getFile(@PathVariable id: Long?): ResponseEntity<File?> {
        val file = id?.let { fileService.getFile(it) } ?: return ResponseEntity.notFound().build()
        val accelRedirectHost = fileService.s3RedirectHost()
        val accelRedirectPath = fileService.s3RedirectPath(file)
        return ResponseEntity
            .ok()
            .header(ACCEL_REDIRECT_HOST, accelRedirectHost)
            .header(ACCEL_REDIRECT_PATH, accelRedirectPath)
            .body(file)

    }

    companion object {
        const val ACCEL_REDIRECT_HOST = "X-Accel-Redirect-Host"
        const val ACCEL_REDIRECT_PATH = "X-Accel-Redirect-Path"
    }
}