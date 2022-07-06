package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.create
import nl.stevenbontenbal.chorister.model.Choir
import nl.stevenbontenbal.chorister.model.Setlist
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class SetlistRepositoryTests @Autowired constructor(
    val entityManager: TestEntityManager,
    val setlistRepository: SetlistRepository,
) {
    @Test
    fun `When findAll then return all Setlists`() {
        // Arrange
        val choir = Choir.create()
        entityManager.persist(choir)
        val setlist1 = Setlist.create(choir)
        val setlist2 = Setlist.create(choir)
        entityManager.persist(setlist1)
        entityManager.persist(setlist2)
        // Act
        val setlists = setlistRepository.findAll()
        // Assert
        Assertions.assertThat(setlists.count()).isEqualTo(2)
    }
}