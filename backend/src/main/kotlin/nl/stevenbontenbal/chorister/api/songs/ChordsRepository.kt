package nl.stevenbontenbal.chorister.api.songs

import nl.stevenbontenbal.chorister.domain.songs.Chords
import org.springframework.context.annotation.Primary
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@Primary
@RepositoryRestResource(collectionResourceRel = "chords", path = "chords")
interface ChordsRepository : CrudRepository<Chords, Long>