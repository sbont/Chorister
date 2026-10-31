package nl.stevenbontenbal.chorister.api.liturgies

import nl.stevenbontenbal.chorister.domain.rites.MassPart
import nl.stevenbontenbal.chorister.domain.rites.Reading
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource
import java.util.Optional

@RepositoryRestResource(path = "massReadings")
interface ReadingRepository : CrudRepository<Reading, Long> {
    fun findByDayReference(dayReference: String): Iterable<Reading>

    fun findByDayReferenceAndPart(dayReference: String, part: MassPart): Optional<Reading>

    @RestResource(exported = false)
    override fun <T : Reading> save(entity: T): T

    @RestResource(exported = false)
    override fun <T : Reading> saveAll(entities: MutableIterable<T>): MutableIterable<T>

    @RestResource(exported = false)
    override fun delete(entity: Reading)

    @RestResource(exported = false)
    override fun deleteById(id: Long)

    @RestResource(exported = false)
    override fun deleteAll(entities: MutableIterable<Reading>)

    @RestResource(exported = false)
    override fun deleteAll()
}
