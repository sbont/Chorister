package nl.stevenbontenbal.chorister.api.songs

import io.kotest.matchers.shouldBe
import nl.stevenbontenbal.chorister.create
import nl.stevenbontenbal.chorister.domain.songs.Category
import nl.stevenbontenbal.chorister.domain.songs.CategoryType
import nl.stevenbontenbal.chorister.domain.users.Choir
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
        categories.count() shouldBe 2
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
        actual!!.id shouldBe category1.id
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
        actual shouldBe null
    }
}