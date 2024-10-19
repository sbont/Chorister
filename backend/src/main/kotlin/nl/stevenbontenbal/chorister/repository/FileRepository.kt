package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.model.entities.File
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FileRepository : CrudRepository<File, Long> {
}