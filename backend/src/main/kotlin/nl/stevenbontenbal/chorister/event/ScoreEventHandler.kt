package nl.stevenbontenbal.chorister.event

import kotlinx.coroutines.runBlocking
import nl.stevenbontenbal.chorister.model.entities.Score
import nl.stevenbontenbal.chorister.service.FileService
import org.springframework.data.rest.core.annotation.HandleBeforeDelete
import org.springframework.data.rest.core.annotation.RepositoryEventHandler
import org.springframework.stereotype.Component

@Component
@RepositoryEventHandler(Score::class)
class ScoreEventHandler(
    private val fileService: FileService
) {
    @HandleBeforeDelete
    fun handleScoreDelete(score: Score) {
        runBlocking {
            if (score.file != null)
                fileService.delete(score.file!!)
        }
    }

}