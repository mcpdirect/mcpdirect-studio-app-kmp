package ai.mcpdirect.studio.app.auth
import ai.mcpdirect.backend.dao.entity.account.AIPortUser
import ai.mcpdirect.studio.handler.UserInfoNotificationHandler

class UserInfoNotificationHandlerImplement(val authViewModel: AuthViewModel):UserInfoNotificationHandler {
    override fun onUserInfoNotification(userInfo: AIPortUser?) {
        authViewModel.userInfo.value = userInfo
    }

}