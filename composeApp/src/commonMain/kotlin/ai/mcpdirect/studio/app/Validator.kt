package ai.mcpdirect.studio.app

fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    return email.matches(emailRegex.toRegex())
}