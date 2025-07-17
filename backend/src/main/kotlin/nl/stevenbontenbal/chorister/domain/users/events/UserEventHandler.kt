package nl.stevenbontenbal.chorister.domain.users.events

import nl.stevenbontenbal.chorister.domain.users.IChoirRepository
import nl.stevenbontenbal.chorister.domain.users.User
import nl.stevenbontenbal.chorister.domain.users.UserService
import org.springframework.data.rest.core.annotation.HandleBeforeCreate
import org.springframework.data.rest.core.annotation.HandleBeforeSave
import org.springframework.data.rest.core.annotation.RepositoryEventHandler
import org.springframework.stereotype.Component

@Component
@RepositoryEventHandler(User::class)
class UserEventHandler(
    private val choirRepository: IChoirRepository,
    private val userService: UserService
    ) {

    @HandleBeforeCreate
    fun handleUserCreate(u: User) {
        saveChoir(u)
    }

    @HandleBeforeSave
    fun handleUserSave(u: User) {
        userService.setUserEmail(u)
    }

    fun saveChoir(u: User) {
        choirRepository.save(u.choir ?: return)
    }
}