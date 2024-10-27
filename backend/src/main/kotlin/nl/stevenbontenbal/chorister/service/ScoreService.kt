package nl.stevenbontenbal.chorister.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nl.stevenbontenbal.chorister.repository.ScoreRepository
import org.springframework.data.rest.webmvc.ResourceNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import kotlin.jvm.optionals.getOrElse

class ScoreService(
    private val scoreRepository: ScoreRepository,
    private val fileService: FileService,
    private val userService: UserService,
) {
    /*suspend fun getFileUploadUrl(id: Long): String {
        return withContext(Dispatchers.IO) {
            val score = scoreRepository.findById(id)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }

            if (score.file == null)
            {
                score.file = fileService.createFile(score)
                scoreRepository.save(score)
            }

            val file = score.file!!
            return@withContext fileService.getUploadUrl(file)
        }
    }*/
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