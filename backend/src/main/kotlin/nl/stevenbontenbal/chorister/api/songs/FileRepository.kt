package nl.stevenbontenbal.chorister.api.songs

import nl.stevenbontenbal.chorister.domain.songs.File
import org.springframework.context.annotation.Primary
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@Primary
@RepositoryRestResource(exported = false) // We need this repo in order to expose the reference from Score to File
interface FileRepository : CrudRepository<File, Long>