package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.model.entities.Chords
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource(collectionResourceRel = "chords", path = "chords")
interface ChordsRepository : CrudRepository<Chords, Long>