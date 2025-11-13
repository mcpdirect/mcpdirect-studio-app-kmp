package ai.mcpdirect.studio.app.model.repository

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.account.AIPortUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class UserRepository {
    private val loadMutex = Mutex()
    val _users = MutableStateFlow<Map<Long,AIPortUser>>(emptyMap())
    val users: StateFlow<Map<Long, AIPortUser>> = _users
    suspend fun getUser(userId:Long) {
        val user = _users.value[userId];
        if(user==null)loadMutex.withLock {
            // 此块内代码会排队执行
            getPlatform().getUser(userId){
                if(it.code== AIPortServiceResponse.SERVICE_SUCCESSFUL)
                    it.data?.let {
                        user ->
                        _users.update { map ->
                            map.toMutableMap().apply {
                                put(user.id, user)
                            }
                        }
                    }
            }
        }else{

        }
    }
}