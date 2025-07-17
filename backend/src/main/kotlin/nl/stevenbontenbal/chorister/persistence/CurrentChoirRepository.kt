package nl.stevenbontenbal.chorister.persistence

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import nl.stevenbontenbal.chorister.application.ChoirContext
import nl.stevenbontenbal.chorister.application.ICurrentChoirRepository
import nl.stevenbontenbal.chorister.domain.users.Choir
import nl.stevenbontenbal.chorister.domain.users.IUserAuthorizationService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CurrentChoirRepository(private val authService: IUserAuthorizationService): ICurrentChoirRepository {
    @PersistenceContext
    private val entityManager: EntityManager? = null

    @Transactional
    override fun insertAsCurrentChoir(choir: Choir): Choir {
        entityManager!!.persist(choir)
        setCurrentChoir(choir)
        authService.createRolesForTenant(choir.id!!)
        return choir
    }

    override fun setCurrentChoir(choir: Choir) {
        entityManager!!.createNativeQuery("SET app.current_choir_id TO '${choir.id}'").executeUpdate()
        ChoirContext.setCurrentChoirId(choir.id!!)
    }
}