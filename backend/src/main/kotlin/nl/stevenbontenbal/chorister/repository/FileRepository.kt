package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.model.entities.File
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource(exported = false)
interface FileRepository : CrudRepository<File, Long>