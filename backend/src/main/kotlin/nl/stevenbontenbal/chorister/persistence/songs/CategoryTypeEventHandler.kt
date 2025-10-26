package nl.stevenbontenbal.chorister.persistence.songs

import nl.stevenbontenbal.chorister.domain.songs.CategoryType
import nl.stevenbontenbal.chorister.domain.users.UserService
import org.springframework.data.rest.core.annotation.HandleBeforeCreate
import org.springframework.data.rest.core.annotation.RepositoryEventHandler
import org.springframework.stereotype.Component

@Component
@RepositoryEventHandler(CategoryType::class)
class CategoryTypeEventHandler(private val userService: UserService) {
    @HandleBeforeCreate
    fun handleCategoryTypeCreate(categoryType: CategoryType) {
        categoryType.linkChoir(userService)
    }
}
