package nl.stevenbontenbal.chorister.controller

import nl.stevenbontenbal.chorister.repository.ScoreRepository
import nl.stevenbontenbal.chorister.service.ChordsService
import nl.stevenbontenbal.chorister.service.FileService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
class ChordsController(
    private val chordsService: ChordsService
) {
    @GetMapping("/api/scores/{id}/file/upload-url")
    suspend fun getUploadUrl(@PathVariable("id") id: Long) = chordsService.getFileUploadUrl(id)
}