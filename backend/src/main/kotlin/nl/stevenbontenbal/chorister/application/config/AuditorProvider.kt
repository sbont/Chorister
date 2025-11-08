package nl.stevenbontenbal.chorister.application.config

import nl.stevenbontenbal.chorister.domain.users.UserService
import nl.stevenbontenbal.chorister.shared.toOptional
import org.springframework.data.domain.AuditorAware
import java.util.*


class AuditorProvider(val userService: UserService) : AuditorAware<Long> {
    override fun getCurrentAuditor(): Optional<Long> {
        return userService.currentUser.id.toOptional()
    }
}