package nl.stevenbontenbal.chorister.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nl.stevenbontenbal.chorister.repository.ScoreRepository
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class ScoreService(
    private val scoreRepository: ScoreRepository,
    private val fileService: FileService
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
}