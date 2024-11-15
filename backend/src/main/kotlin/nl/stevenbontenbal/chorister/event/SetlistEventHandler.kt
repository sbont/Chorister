package nl.stevenbontenbal.chorister.event

import nl.stevenbontenbal.chorister.model.entities.Event
import nl.stevenbontenbal.chorister.service.UserService
import org.springframework.data.rest.core.annotation.HandleBeforeCreate
import org.springframework.data.rest.core.annotation.RepositoryEventHandler
import org.springframework.stereotype.Component

@Component
@RepositoryEventHandler(Event::class)
class EventEventHandler(
    private val userService: UserService
) {
    @HandleBeforeCreate
    fun handleEventCreate(event: Event) {
        event.linkChoir(userService)
    }
}