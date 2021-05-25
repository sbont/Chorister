package nl.stevenbontenbal.cymbali.repository

import nl.stevenbontenbal.cymbali.model.Score
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource()
interface ScoreRepository : CrudRepository<Score, Long> {}