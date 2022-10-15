package nl.stevenbontenbal.chorister.event

import nl.stevenbontenbal.chorister.model.Setlist
import nl.stevenbontenbal.chorister.model.Song
import nl.stevenbontenbal.chorister.repository.SongbookRepository
import nl.stevenbontenbal.chorister.service.UserService
import org.springframework.data.rest.core.annotation.HandleBeforeCreate
import org.springframework.data.rest.core.annotation.HandleBeforeSave
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