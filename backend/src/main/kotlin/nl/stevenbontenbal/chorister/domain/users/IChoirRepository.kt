package nl.stevenbontenbal.chorister.domain.users

import org.springframework.data.repository.CrudRepository

interface IChoirRepository : CrudRepository<Choir, Long> {
    fun findByInviteToken(inviteToken: String): Choir?
}