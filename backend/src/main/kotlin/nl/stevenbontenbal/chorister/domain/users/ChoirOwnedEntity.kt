package nl.stevenbontenbal.chorister.domain.users

interface ChoirOwnedEntity {
    var choir: Choir?
    fun linkChoir(userService: UserService) {
        val currentUser = userService.getCurrentUser()
        if(currentUser.choir != null) {
            choir = currentUser.choir!!
        }
    }
}