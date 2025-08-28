package ai.mcpdirect.studio.app.data.repository

import ai.mcpdirect.studio.app.data.model.User

interface UserRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun sendOtpForRegistration(email: String): Result<Unit>
    suspend fun register(email: String, password: String): Result<User>
    suspend fun verifyOtpForRegistration(email: String, otp: String): Result<Unit>
    suspend fun sendOtpForForgotPassword(email: String): Result<Unit>
    suspend fun verifyOtpForForgotPassword(email: String, otp: String): Result<Unit>
    suspend fun setNewPassword(email: String, otp: String, newPassword: String): Result<Unit>
    suspend fun loginWithGoogle(): Result<User>
    suspend fun loginWithGitHub(): Result<User>
    suspend fun loginWithApple(): Result<User>
}
