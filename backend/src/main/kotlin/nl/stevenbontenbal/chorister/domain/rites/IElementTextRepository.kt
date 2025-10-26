package nl.stevenbontenbal.chorister.domain.rites

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface IElementTextRepository : CrudRepository<ElementText, Long> {
    @Query("select et from ElementText et join et.element e join e.orderOfService oos join et.translation t where t.id = :tId and oos.id = :oosId")
    fun findByOrderOfServiceAndTranslation(@Param("oosId") orderOfServiceId: Long, @Param("tId") translationId: Long): Iterable<ElementText>
}