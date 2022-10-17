package nl.stevenbontenbal.chorister.interfaces

import nl.stevenbontenbal.chorister.model.entities.Choir
import nl.stevenbontenbal.chorister.service.UserService

interface ChoirOwnedEntity {
    var choir: Choir?
    fun linkChoir(userService: UserService) {
        val currentUser = userService.getCurrentUser()
        if(currentUser.choir != null) {
            choir = currentUser.choir!!
        }
    }
}