package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.model.entities.Invite
import nl.stevenbontenbal.chorister.model.entities.Score
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource
import org.springframework.security.access.prepost.PostFilter

@RepositoryRestResource
interface ScoreRepository : CrudRepository<Score, Long> {
    @RestResource
    @PostFilter("hasPermission(filterObject, 'read')")
    override fun findAll(): Iterable<Score>
}