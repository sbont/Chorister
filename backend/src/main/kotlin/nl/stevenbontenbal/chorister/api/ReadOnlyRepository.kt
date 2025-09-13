package nl.stevenbontenbal.chorister.api

import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.Repository
import java.util.Optional

@NoRepositoryBean
interface ReadOnlyRepository<T, ID> : Repository<T?, ID?> {
    fun findById(id: ID?): Optional<T>
    fun findAll(): Iterable<T>
}