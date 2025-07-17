package nl.stevenbontenbal.chorister.domain.songs

import org.springframework.data.repository.CrudRepository

interface ICategoryRepository : CrudRepository<Category, Long>