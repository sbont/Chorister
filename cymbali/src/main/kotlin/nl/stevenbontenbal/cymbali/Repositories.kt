package nl.stevenbontenbal.cymbali

import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource
interface SongRepository : CrudRepository<Song, Long> {
//    fun findById(id: Long): Song?
    fun findByTitle(title: String): Song?
    fun findBySlug(slug: String): Song?
    fun findAllByOrderByAddedAtDesc(): Iterable<Song>
}

@RepositoryRestResource
interface UserRepository : CrudRepository<User, Long> {
    fun findByEmail(login: String): User?
}