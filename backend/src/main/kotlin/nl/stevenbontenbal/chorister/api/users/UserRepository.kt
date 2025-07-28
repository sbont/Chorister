package nl.stevenbontenbal.chorister.api.users

import nl.stevenbontenbal.chorister.domain.users.User
import org.springframework.context.annotation.Primary
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@Primary
@RepositoryRestResource
interface UserRepository : CrudRepository<User, Long>