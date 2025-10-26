package nl.stevenbontenbal.chorister.persistence.songs

import nl.stevenbontenbal.chorister.domain.songs.Category
import nl.stevenbontenbal.chorister.domain.users.UserService
import org.springframework.data.rest.core.annotation.HandleBeforeCreate
import org.springframework.data.rest.core.annotation.RepositoryEventHandler
import org.springframework.stereotype.Component

@Component
@RepositoryEventHandler(Category::class)
class CategoryEventHandler(private val userService: UserService) {
    @HandleBeforeCreate
    fun handleCategoryCreate(category: Category) {
        category.linkChoir(userService)
    }
}
