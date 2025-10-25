package nl.stevenbontenbal.chorister.api.rites

import nl.stevenbontenbal.chorister.api.ReadOnlyRepository
import nl.stevenbontenbal.chorister.domain.rites.Rite
import org.springframework.context.annotation.Primary
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@Primary
@RepositoryRestResource
interface RiteRepository : ReadOnlyRepository<Rite, Long> {}
