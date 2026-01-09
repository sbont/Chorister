package nl.stevenbontenbal.chorister.api.users.views

/**
 * JsonView interfaces for role-based user data filtering
 */
sealed interface UserViews {
    /**
     * Basic view - includes only firstName and lastName
     * For EDITOR and VIEWER roles
     */
    interface Basic
    /**
     * Manager view - includes all user details
     * For MANAGER role
     */
    object Manager : Basic
}
