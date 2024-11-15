package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.create
import nl.stevenbontenbal.chorister.model.entities.Choir
import nl.stevenbontenbal.chorister.model.entities.Event
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class EventRepositoryTests @Autowired constructor(
    val entityManager: TestEntityManager,
    val eventRepository: EventRepository,
) {
    @Test
    fun `When findAll then return all Events`() {
        // Arrange
        val choir = Choir.create()
        entityManager.persist(choir)
        val event1 = Event.create(choir)
        val event2 = Event.create(choir)
        entityManager.persist(event1)
        entityManager.persist(event2)
        // Act
        val events = eventRepository.findAll()
        // Assert
        Assertions.assertThat(events.count()).isEqualTo(2)
    }
}