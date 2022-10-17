package nl.stevenbontenbal.chorister.event

import nl.stevenbontenbal.chorister.model.entities.Setlist
import nl.stevenbontenbal.chorister.service.UserService
import org.springframework.data.rest.core.annotation.HandleBeforeCreate
import org.springframework.data.rest.core.annotation.RepositoryEventHandler
import org.springframework.stereotype.Component

@Component
@RepositoryEventHandler(Setlist::class)
class SetlistEventHandler(
    private val userService: UserService
) {

    @HandleBeforeCreate
    fun handleSongCreate(setlist: Setlist) {
        setlist.linkChoir(userService)
    }
}