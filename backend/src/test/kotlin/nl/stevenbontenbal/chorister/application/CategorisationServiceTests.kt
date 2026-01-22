package nl.stevenbontenbal.chorister.application

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import nl.stevenbontenbal.chorister.application.config.ChoristerConfiguration
import nl.stevenbontenbal.chorister.application.config.ChoristerProperties
import nl.stevenbontenbal.chorister.create
import nl.stevenbontenbal.chorister.domain.songs.Category
import nl.stevenbontenbal.chorister.domain.users.Choir
import nl.stevenbontenbal.chorister.api.songs.CategoryRepository
import nl.stevenbontenbal.chorister.application.songs.CategorisationService
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@ContextConfiguration(classes = [ChoristerConfiguration::class])
class CategorisationServiceTests {
    @Test
    fun `when createDefaultCategories then exist categories`() {
        // Arrange
        val properties = ChoristerProperties(
            "",
            "",
            "",
            ChoristerProperties.DefaultCategories(listOf("Test1", "Test2"), listOf("Test3")),
            ChoristerProperties.DataSource(DataSourceProperties())
        )
        val choir = Choir.create(null)
        val slot = slot<Iterable<Category>>()
        val categoryRepository: CategoryRepository = mockk(relaxed = true)
        every { categoryRepository.saveAll(capture(slot)) }.answers { slot.captured }
        val target = CategorisationService(properties, categoryRepository)
        // Act
        target.createDefaultCategories(choir)
        // Assert
        slot.captured.iterator().hasNext() shouldBe true
    }

}