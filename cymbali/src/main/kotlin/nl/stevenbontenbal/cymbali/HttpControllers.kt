package nl.stevenbontenbal.cymbali

import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/songs")
class SongController(private val songRepository: SongRepository, private val userRepository: UserRepository) {

    @GetMapping("/")
    fun findAll() = songRepository.findAllByOrderByAddedAtDesc()

    @GetMapping("/{id}")
    fun findOne(@PathVariable id: Long) =
        songRepository.findByIdOrNull(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "This song does not exist")

    @PostMapping("/")
    fun add(@RequestBody song: Song): Song {
        /*val addedById = song.addedBy.id ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "No user id provided")
        val addedBy = userRepository.findByIdOrNull(addedById) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "This song does not exist")
        song.addedBy = addedBy*/
        songRepository.save(song)
        return song
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody song: Song) {
        song.id = id
        songRepository.save(song)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) =
        songRepository.deleteById(id)

}

@RestController
@RequestMapping("/api/users")
class UserController(private val repository: UserRepository) {

    @GetMapping("/")
    fun findAll() = repository.findAll()

    @GetMapping("/{id}")
    fun findOne(@PathVariable id: Long) =
        repository.findByIdOrNull(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "This user does not exist")
}