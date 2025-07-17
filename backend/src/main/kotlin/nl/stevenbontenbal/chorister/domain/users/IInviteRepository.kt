package nl.stevenbontenbal.chorister.domain.users

import org.springframework.data.repository.CrudRepository

interface IInviteRepository : CrudRepository<Invite, Long> {
    fun findByToken(token: String): Invite?
}