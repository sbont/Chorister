package nl.stevenbontenbal.chorister.domain.songs.events

import nl.stevenbontenbal.chorister.api.songs.SongRepository
import nl.stevenbontenbal.chorister.domain.songs.Category
import nl.stevenbontenbal.chorister.domain.users.UserService
import org.springframework.data.rest.core.annotation.HandleBeforeCreate
import org.springframework.data.rest.core.annotation.HandleBeforeDelete
import org.springframework.data.rest.core.annotation.RepositoryEventHandler
import org.springframework.stereotype.Component

@Component
@RepositoryEventHandler(Category::class)
class CategoryEventHandler(
    private val userService: UserService,
    private val songRepository: SongRepository) {
    @HandleBeforeCreate
    fun handleCategoryCreate(category: Category) {
        category.linkChoir(userService)
    }

    @HandleBeforeDelete
    fun handleCategoryDelete(category: Category) {
        category.id
            ?.let { songRepository.findAllByCategories_id(it) }
            ?.forEach { song -> song.categories?.remove(category) }
    }

}
