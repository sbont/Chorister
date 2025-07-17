package nl.stevenbontenbal.chorister.service

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import nl.stevenbontenbal.chorister.model.entities.Choir
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ChoirService(private val zitadelService: ZitadelService) {
    @PersistenceContext
    private val entityManager: EntityManager? = null

    @Transactional
    fun insertWithTransaction(choir: Choir): Choir {
        return persistAndSetCurrentChoir(choir)
    }

    fun insertWithoutTransaction(choir: Choir): Choir {
        return persistAndSetCurrentChoir(choir)
    }

    private fun persistAndSetCurrentChoir(choir: Choir): Choir {
        entityManager!!.persist(choir)
        setCurrentChoir(choir)
        zitadelService.createRolesForTenant(choir.id!!)
        return choir
    }

    fun setCurrentChoir(choir: Choir) {
        entityManager!!.createNativeQuery("SET app.current_choir_id TO '${choir.id}'").executeUpdate()
        ChoirContext.setCurrentChoirId(choir.id!!)
    }
}