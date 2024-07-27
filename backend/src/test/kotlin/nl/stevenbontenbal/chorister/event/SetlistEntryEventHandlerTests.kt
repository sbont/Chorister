package nl.stevenbontenbal.chorister.event

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import nl.stevenbontenbal.chorister.configuration.ChoristerConfiguration
import nl.stevenbontenbal.chorister.create
import nl.stevenbontenbal.chorister.model.entities.Choir
import nl.stevenbontenbal.chorister.model.entities.Setlist
import nl.stevenbontenbal.chorister.model.entities.SetlistEntry
import nl.stevenbontenbal.chorister.model.entities.Song
import nl.stevenbontenbal.chorister.repository.SetlistEntryRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@ContextConfiguration(classes = [ChoristerConfiguration::class])
class SetlistEntryEventHandlerTests {
    @Test
    fun `when setlist empty then first entry is 1`() {
        // Arrange
        val choir = Choir.create()
        val setlist = Setlist.create(choir, 1)
        val song1 = Song.create(choir)
        val entry = SetlistEntry.create(setlist, song1)
        val repository: SetlistEntryRepository = mockk(relaxed = true)
        val target = SetlistEntryEventHandler(repository)

        // Act
        target.handleSetlistEntryCreate(entry)

        // Assert
        Assertions.assertThat(entry.number).isEqualTo(1)
    }

    @Test
    fun `when setlist contains 2 then next entry is 3`() {
        // Arrange
        val choir = Choir.create()
        val setlist = Setlist.create(choir, 1)
        val song1 = Song.create(choir)
        val song2 = Song.create(choir)
        val song3 = Song.create(choir)
        val entries = SetlistEntry.create(setlist, listOf(song1, song2, song3))
        setlist.entries.addAll(entries.take(2))
        val repository: SetlistEntryRepository = mockk(relaxed = true)
        val target = SetlistEntryEventHandler(repository)

        // Act
        target.handleSetlistEntryDelete(entries[2])

        // Assert
        Assertions.assertThat(entries[2].number).isEqualTo(3)
    }

    @Test
    fun `when entry deleted then following entries are reordered`() {
        // Arrange
        val choir = Choir.create()
        val setlist = Setlist.create(choir, 1)
        val song1 = Song.create(choir)
        val song2 = Song.create(choir)
        val song3 = Song.create(choir)
        val entries = SetlistEntry.create(setlist, listOf(song1, song2, song3))
        val repository: SetlistEntryRepository = mockk(relaxed = true)
        every { repository.findBySetlistIdAndNumberGreaterThan(setlist.id!!, 1) }.returns(listOf(entries[1], entries[2]))
        val target = SetlistEntryEventHandler(repository)

        // Act
        target.handleSetlistEntryDelete(entries[0])

        // Assert
        verify(exactly = 1) { repository.saveAll(listOf(entries[1], entries[2])) }
        Assertions.assertThat(entries[1].number).isEqualTo(1)
    }
}