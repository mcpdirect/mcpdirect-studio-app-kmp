package ai.mcpdirect.studio.app.data.repository

import ai.mcpdirect.studio.MCPDirectStudio
import ai.mcpdirect.studio.app.data.model.User

class UserRepositoryImpl : UserRepository {
    override suspend fun login(email: String, password: String): Result<User> {
        // Simulate a successful login
        val success = MCPDirectStudio.login(email,password);
        if(success) {
            val userInfo = MCPDirectStudio.getUserInfo();
            var name = userInfo.name
            if(name==null) name = email;
            return Result.success(User(id = userInfo.id, name = name, email = email))
        }else{
            return Result.failure(Exception("Login failed."));
        }
    }

    override suspend fun sendOtpForRegistration(email: String): Result<Unit> {
        // Simulate sending OTP for registration
        return Result.success(Unit)
    }

    override suspend fun register(email: String, password: String): Result<User> {
        // Simulate a successful registration with password
        return Result.success(User(id = 1, name = "New User", email = email))
    }

    override suspend fun verifyOtpForRegistration(email: String, otp: String): Result<Unit> {
        // Simulate a successful OTP verification for registration
        return Result.success(Unit)
    }

    override suspend fun sendOtpForForgotPassword(email: String): Result<Unit> {
        // Simulate sending OTP for forgot password
        return Result.success(Unit)
    }

    override suspend fun verifyOtpForForgotPassword(email: String, otp: String): Result<Unit> {
        // Simulate a successful OTP verification for forgot password
        return Result.success(Unit)
    }

    override suspend fun setNewPassword(email: String, otp: String, newPassword: String): Result<Unit> {
        // Simulate setting a new password with OTP
        return Result.success(Unit)
    }

    override suspend fun loginWithGoogle(): Result<User> {
        // Simulate a successful Google login
        return Result.success(User(id = 2, name = "Google User", email = "google@example.com"))
    }

    override suspend fun loginWithGitHub(): Result<User> {
        // Simulate a successful GitHub login
        return Result.success(User(id = 3, name = "GitHub User", email = "github@example.com"))
    }

    override suspend fun loginWithApple(): Result<User> {
        // Simulate a successful Apple login
        return Result.success(User(id = 4, name = "Apple User", email = "apple@example.com"))
    }
}
