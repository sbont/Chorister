package nl.stevenbontenbal.chorister.api.liturgies

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import nl.stevenbontenbal.chorister.domain.rites.MassPart
import nl.stevenbontenbal.chorister.domain.rites.Proper
import nl.stevenbontenbal.chorister.domain.rites.ProperType
import nl.stevenbontenbal.chorister.domain.rites.Reading
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.TestPropertySource

/**
 * The other @DataJpaTest slices fail on this machine because
 * spring.jpa.hibernate.ddl-auto=validate meets an empty embedded database,
 * and chorister.base-url/chorister.datasource are only defined in the
 * development profile. This test creates the schema from the entities and
 * supplies the missing properties (including the H2 dialect, since the
 * global CockroachDialect produces DDL that H2 cannot run) so the mappings
 * and finders are actually verified.
 */
@DataJpaTest
@TestPropertySource(
    properties = [
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "chorister.base-url=http://localhost:8090",
        "chorister.datasource.system.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "chorister.datasource.user.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
    ]
)
class LiturgicalContentRepositoryTests @Autowired constructor(
    val entityManager: TestEntityManager,
    val readingRepository: ReadingRepository,
    val properRepository: ProperRepository,
) {
    private val dayReference = "england-wales:day:all-saints"

    @Test
    fun `finds all readings of a day`() {
        entityManager.persist(Reading(dayReference = dayReference, part = MassPart.GOSPEL, reference = "Matthew 5:1-12", translation = "ESV-CE", text = "Blessed are the poor in spirit..."))
        entityManager.persist(Reading(dayReference = dayReference, part = MassPart.READING_1, reference = "Revelation 7:2-4", translation = "ESV-CE", text = "I, John, saw *another angel*..."))
        entityManager.persist(Reading(dayReference = dayReference, part = MassPart.PSALM_1, reference = "Psalm 23", text = "The Lord is my shepherd."))

        val readings = readingRepository.findByDayReference(dayReference).toList()

        readings shouldHaveSize 3
        readings.map { it.part }.sortedBy { it.order } shouldContainExactly listOf(
            MassPart.READING_1, MassPart.PSALM_1, MassPart.GOSPEL
        )
    }

    @Test
    fun `finds a reading by day reference and part`() {
        entityManager.persist(Reading(dayReference = dayReference, part = MassPart.READING_1, reference = "Revelation 7:2-4", text = "I, John, saw another angel..."))

        val reading = readingRepository.findByDayReferenceAndPart(dayReference, MassPart.READING_1)

        reading.isPresent shouldBe true
        reading.get().reference shouldBe "Revelation 7:2-4"
    }

    @Test
    fun `finds all propers of a day`() {
        entityManager.persist(Proper(dayReference = dayReference, type = ProperType.COLLECT, text = "Almighty ever-living God..."))
        entityManager.persist(Proper(dayReference = dayReference, type = ProperType.ENTRANCE_ANTIPHON, text = "Let us all rejoice in the Lord..."))

        val propers = properRepository.findByDayReference(dayReference).toList()

        propers shouldHaveSize 2
        propers.map { it.type }.sorted() shouldContainExactly listOf(
            ProperType.ENTRANCE_ANTIPHON, ProperType.COLLECT
        )
    }

    @Test
    fun `rejects a reading whose part is not a reading part`() {
        val invalid = Reading(dayReference = dayReference, part = MassPart.GLORIA, text = "Gloria in excelsis Deo")

        shouldThrow<IllegalArgumentException> { entityManager.persist(invalid) }
    }
}
