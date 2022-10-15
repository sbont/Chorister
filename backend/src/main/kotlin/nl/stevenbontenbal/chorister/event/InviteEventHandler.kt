package nl.stevenbontenbal.chorister.event

import nl.stevenbontenbal.chorister.model.Invite
import nl.stevenbontenbal.chorister.service.UserService
import org.springframework.data.rest.core.annotation.HandleBeforeCreate
import org.springframework.data.rest.core.annotation.RepositoryEventHandler
import org.springframework.stereotype.Component

@Component
@RepositoryEventHandler(Invite::class)
class InviteEventHandler(
    private val userService: UserService
) {

    @HandleBeforeCreate
    fun handleInviteCreate(invite: Invite) {
        invite.linkChoir(userService)
        invite.invitedBy = userService.getCurrentUser()
    }
}