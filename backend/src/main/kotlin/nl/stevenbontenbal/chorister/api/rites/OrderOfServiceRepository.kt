package nl.stevenbontenbal.chorister.api.rites

import nl.stevenbontenbal.chorister.api.ReadOnlyRepository
import nl.stevenbontenbal.chorister.domain.rites.OrderOfService
import org.springframework.context.annotation.Primary
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@Primary
@RepositoryRestResource
interface OrderOfServiceRepository : ReadOnlyRepository<OrderOfService, Long>
