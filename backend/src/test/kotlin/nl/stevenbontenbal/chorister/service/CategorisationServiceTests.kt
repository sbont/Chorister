package nl.stevenbontenbal.chorister.service

import io.mockk.Answer
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import nl.stevenbontenbal.chorister.configuration.ChoristerConfiguration
import nl.stevenbontenbal.chorister.configuration.ChoristerProperties
import nl.stevenbontenbal.chorister.create
import nl.stevenbontenbal.chorister.model.entities.Category
import nl.stevenbontenbal.chorister.model.entities.Choir
import nl.stevenbontenbal.chorister.repository.CategoryRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner


@RunWith(SpringRunner::class)
@ContextConfiguration(classes = [ChoristerConfiguration::class])
class CategorisationServiceTests @Autowired constructor(
) {
    @Test
    fun `when createDefaultCategories then exist categories`() {
        // Arrange
        val properties = ChoristerProperties("", "", "", ChoristerProperties.DefaultCategories(listOf("Test1", "Test2"), listOf("Test3")))
        val choir = Choir.create(null)
        val slot = slot<Iterable<Category>>()
        val categoryRepository: CategoryRepository = mockk(relaxed = true)
        every { categoryRepository.saveAll(capture(slot)) }.answers { slot.captured }
        val target = CategorisationService(properties, categoryRepository)
        // Act
        target.createDefaultCategories(choir)
        // Assert
        assertThat(slot.captured.iterator().hasNext()).isTrue
    }


}