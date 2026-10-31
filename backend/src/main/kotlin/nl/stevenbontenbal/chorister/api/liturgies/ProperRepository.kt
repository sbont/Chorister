package nl.stevenbontenbal.chorister.api.liturgies

import nl.stevenbontenbal.chorister.domain.rites.Proper
import nl.stevenbontenbal.chorister.domain.rites.ProperType
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource
import java.util.Optional

/**
 * Read-only: propers are reference data, entered by an importer or the
 * backend, not through the API.
 */
@RepositoryRestResource
interface ProperRepository : CrudRepository<Proper, Long> {
    fun findByDayReference(dayReference: String): Iterable<Proper>

    fun findByDayReferenceAndType(dayReference: String, type: ProperType): Optional<Proper>

    @RestResource(exported = false)
    override fun <T : Proper> save(entity: T): T

    @RestResource(exported = false)
    override fun <T : Proper> saveAll(entities: MutableIterable<T>): MutableIterable<T>

    @RestResource(exported = false)
    override fun delete(entity: Proper)

    @RestResource(exported = false)
    override fun deleteById(id: Long)

    @RestResource(exported = false)
    override fun deleteAll(entities: MutableIterable<Proper>)

    @RestResource(exported = false)
    override fun deleteAll()
}
