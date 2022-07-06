package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.create
import nl.stevenbontenbal.chorister.model.Category
import nl.stevenbontenbal.chorister.model.CategoryType
import nl.stevenbontenbal.chorister.model.Choir
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class CategoryRepositoryTests @Autowired constructor(
    val entityManager: TestEntityManager,
    val categoryRepository: CategoryRepository,
) {
    @Test
    fun `When findAll then return all Categories`() {
        // Arrange
        val choir = Choir.create()
        entityManager.persist(choir)
        val categoryLiturgical = Category.create(choir, CategoryType.LITURGICAL_MOMENT)
        val categorySeasonal = Category.create(choir, CategoryType.SEASON)
        entityManager.persist(categoryLiturgical)
        entityManager.persist(categorySeasonal)
        // Act
        val categories = categoryRepository.findAll()
        // Assert
        Assertions.assertThat(categories.count()).isEqualTo(2)
    }

    @Test
    fun `When findByName and Category exists then return Category`() {
        // Arrange
        val choir = Choir.create()
        entityManager.persist(choir)
        val category1 = Category(name = "Advent", type = CategoryType.SEASON, choir = choir)
        val category2 = Category.create(choir, CategoryType.SEASON)
        entityManager.persist(category1)
        entityManager.persist(category2)
        // Act
        val actual = categoryRepository.findByName("Advent")
        // Assert
        Assertions.assertThat(actual!!.id).isEqualTo(category1.id)
    }

    @Test
    fun `When findByName and Category not exists then return nothing`() {
        // Arrange
        val choir = Choir.create()
        entityManager.persist(choir)
        val category1 = Category.create(choir, CategoryType.SEASON)
        entityManager.persist(category1)
        // Act
        val actual = categoryRepository.findByName("Advent")
        // Assert
        Assertions.assertThat(actual).isNull()
    }
}