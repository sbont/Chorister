package nl.stevenbontenbal.chorister.controller

import nl.stevenbontenbal.chorister.model.entities.Score
import nl.stevenbontenbal.chorister.repository.FileRepository
import nl.stevenbontenbal.chorister.repository.ScoreRepository
import nl.stevenbontenbal.chorister.service.ScoreService
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.server.ExposesResourceFor
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody


@BasePathAwareController
@ExposesResourceFor(Score::class)
class ScoreController(
    private val scoreService: ScoreService
) {
    @PutMapping("/scores/{id}/file") //, consumes = [MediaType.TEXT_PLAIN_VALUE]
    fun putFile(@PathVariable id: Long?, @RequestBody fileId: Long?): ResponseEntity<Any> {
        if (id == null || fileId == null)
            return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)

        scoreService.saveFileToScore(id, fileId)
        return ResponseEntity.ok().build()
    }
}