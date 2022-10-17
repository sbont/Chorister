package nl.stevenbontenbal.chorister.event

import nl.stevenbontenbal.chorister.model.entities.User
import nl.stevenbontenbal.chorister.repository.ChoirRepository
import org.springframework.data.rest.core.annotation.HandleBeforeCreate
import org.springframework.data.rest.core.annotation.RepositoryEventHandler
import org.springframework.stereotype.Component

@Component
@RepositoryEventHandler(User::class)
class UserEventHandler(private val choirRepository: ChoirRepository) {

    @HandleBeforeCreate
    fun handleUserCreate(u: User) {
        saveChoir(u)
    }

    fun saveChoir(u: User) {
        choirRepository.save(u.choir ?: return)
    }
}