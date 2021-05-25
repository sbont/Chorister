package nl.stevenbontenbal.cymbali

import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.web.bind.annotation.CrossOrigin

@CrossOrigin("http://localhost:8081", maxAge = 3600)
@RepositoryRestResource(excerptProjection = SongProjection::class)
interface SongRepository : CrudRepository<Song, Long> {
    fun findByTitle(title: String): Song?
    fun findBySlug(slug: String): Song?
    fun findAllByOrderByAddedAtDesc(): Iterable<Song>
}

@RepositoryRestResource
interface SongbookRepository : CrudRepository<Songbook, Long> {
    fun findByTitle(title: String): Songbook?
}

@RepositoryRestResource()
interface ScoreRepository : CrudRepository<Score, Long> {}

@CrossOrigin("http://localhost:8081", maxAge = 3600)
@RepositoryRestResource
interface UserRepository : CrudRepository<User, Long> {
    fun findByEmail(login: String): User?
}