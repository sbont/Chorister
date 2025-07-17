package nl.stevenbontenbal.chorister.domain.songs.events

import nl.stevenbontenbal.chorister.domain.songs.Notation
import nl.stevenbontenbal.chorister.domain.users.UserService
import org.springframework.data.rest.core.annotation.HandleBeforeCreate
import org.springframework.data.rest.core.annotation.RepositoryEventHandler
import org.springframework.stereotype.Component

@Component
@RepositoryEventHandler(Notation::class)
class NotationEventHandler(
    private val userService: UserService
) {
    @HandleBeforeCreate
    fun handleNotationCreate(notation: Notation) {
        notation.linkChoir(userService)
    }
}