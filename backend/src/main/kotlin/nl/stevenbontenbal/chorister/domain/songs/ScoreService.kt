package nl.stevenbontenbal.chorister.domain.songs

import nl.stevenbontenbal.chorister.domain.users.UserService
import org.springframework.data.rest.webmvc.ResourceNotFoundException
import kotlin.jvm.optionals.getOrElse

class ScoreService(
    private val scoreRepository: IScoreRepository,
    private val fileService: IFileService,
    private val userService: UserService,
) {
    fun saveFileToScore(id: Long, fileId: Long) {
        val score = scoreRepository.findById(id)
            .filter { userService.hasAccess(it) }
            .getOrElse {
                throw ResourceNotFoundException("Score with ID $id not found in database.")
            }
        val file = fileService.getFile(fileId)
            ?: throw ResourceNotFoundException("File with ID $fileId not found in database.")
        score.file = file
        scoreRepository.save(score)
    }
}