package ifsp.project.welnessmind.ui.login

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUser(
    val userId: Long,
    val displayName: String
)