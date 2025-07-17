package nl.stevenbontenbal.chorister.domain.events.events

import nl.stevenbontenbal.chorister.domain.events.Event
import nl.stevenbontenbal.chorister.domain.users.UserService
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