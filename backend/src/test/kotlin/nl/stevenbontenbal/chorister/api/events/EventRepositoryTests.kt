package nl.stevenbontenbal.chorister.api.events

import io.kotest.matchers.collections.shouldHaveSize
import nl.stevenbontenbal.chorister.application.events.create
import nl.stevenbontenbal.chorister.application.users.create
import nl.stevenbontenbal.chorister.domain.events.Event
import nl.stevenbontenbal.chorister.domain.users.Choir
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
        events shouldHaveSize 2
    }
}