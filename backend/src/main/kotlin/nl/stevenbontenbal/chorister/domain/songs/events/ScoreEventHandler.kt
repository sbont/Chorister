package nl.stevenbontenbal.chorister.domain.songs.events

import kotlinx.coroutines.runBlocking
import nl.stevenbontenbal.chorister.domain.songs.IFileService
import nl.stevenbontenbal.chorister.domain.songs.Score
import org.springframework.data.rest.core.annotation.HandleBeforeDelete
import org.springframework.data.rest.core.annotation.RepositoryEventHandler
import org.springframework.stereotype.Component

@Component
@RepositoryEventHandler(Score::class)
class ScoreEventHandler(
    private val fileService: IFileService
) {
    @HandleBeforeDelete
    fun handleScoreDelete(score: Score) {
        runBlocking {
            if (score.file != null)
                fileService.delete(score.file!!)
        }
    }

}