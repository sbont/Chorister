package nl.stevenbontenbal.chorister.service

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import nl.stevenbontenbal.chorister.configuration.ChoristerConfiguration
import nl.stevenbontenbal.chorister.create
import nl.stevenbontenbal.chorister.model.entities.Category
import nl.stevenbontenbal.chorister.model.entities.Choir
import nl.stevenbontenbal.chorister.repository.CategoryRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner


@RunWith(SpringRunner::class)
@ContextConfiguration(classes = [ChoristerConfiguration::class])
class CategorisationServiceTests @Autowired constructor(
    val categorisationService: CategorisationService,
) {

    @MockK
    lateinit var categoryRepository: CategoryRepository

    @Test
    fun `when createDefaultCategories then exist categories`() {
        // Arrange
        val choir = Choir.create(null)
        val slot = slot<Iterable<Category>>()
        every { categoryRepository.saveAll(capture(slot)) }
        // Act
        categorisationService.createDefaultCategories(choir)
        // Assert
        assertThat(slot.captured.iterator().hasNext()).isTrue
    }


}