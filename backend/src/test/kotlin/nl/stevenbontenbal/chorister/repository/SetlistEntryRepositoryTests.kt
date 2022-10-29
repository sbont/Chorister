package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.create
import nl.stevenbontenbal.chorister.model.entities.Choir
import nl.stevenbontenbal.chorister.model.entities.Setlist
import nl.stevenbontenbal.chorister.model.entities.SetlistEntry
import nl.stevenbontenbal.chorister.model.entities.Song
import nl.stevenbontenbal.chorister.persist
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class SetlistEntryRepositoryTests @Autowired constructor(
    val entityManager: TestEntityManager,
    val setlistEntryRepository: SetlistEntryRepository,
) {
    @Test
    fun `When findBySetlistId then return all setlist's entries`() {
        // Arrange
        val choir = Choir.create().persist(entityManager)
        val setlist = Setlist.create(choir).persist(entityManager)
        val song1 = Song.create(choir).persist(entityManager)
        val song2 = Song.create(choir).persist(entityManager)
        val song3 = Song.create(choir).persist(entityManager)
        val entry1 = SetlistEntry.create(setlist, song1).persist(entityManager)
        val entry2 = SetlistEntry.create(setlist, song2).persist(entityManager)
        // Act
        val setlists = setlistEntryRepository.findBySetlistId(setlistId = setlist.id!!)
        // Assert
        Assertions.assertThat(setlists.count()).isEqualTo(2)
    }

}