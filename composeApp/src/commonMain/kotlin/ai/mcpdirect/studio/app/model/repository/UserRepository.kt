package ai.mcpdirect.studio.app.model.repository

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.UIState
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.account.AIPortUser
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAgent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object UserRepository {
    private val loadMutex = Mutex()
    private val _me = MutableStateFlow(AIPortUser())
    val me: StateFlow<AIPortUser> = _me
    private val _users = MutableStateFlow<Map<Long,AIPortUser>>(emptyMap())
    val users: StateFlow<Map<Long, AIPortUser>> = _users
    fun reset(){
        _me.value = AIPortUser()
        _users.update { map ->
            map.toMutableMap().apply {
                clear()
            }
        }
    }
    fun me(user: AIPortUser):Boolean{
        return user.id>Int.MAX_VALUE&&user.id==_me.value.id
    }
    fun me(userId: Long):Boolean{
        return userId>Int.MAX_VALUE&&userId==_me.value.id
    }
    suspend fun user(userId:Long,
                     onResponse: (code: Int, message: String?, user: AIPortUser?) -> Unit) {
        val user = _users.value[userId];
        if(user==null)loadMutex.withLock {
            // 此块内代码会排队执行
            getPlatform().getUser(userId){
                if(it.successful()) it.data?.let {
                        user ->
                    _users.update { map ->
                        map.toMutableMap().apply {
                            put(user.id, user)
                        }
                    }
                }
                onResponse(it.code,it.message,it.data)
            }
        }else{
            onResponse(0,null,user)
        }
    }
    suspend fun login(
        email:String,password:String,
        onResponse:(code:Int, message:String?, user:AIPortUser?)->Unit
    ){
        loadMutex.withLock {
            getPlatform().login(email,password){
                if(it.successful()) {
                    it.data?.let {
                        _me.value = it
                    }
                }
                onResponse(it.code,it.message,it.data)
            }
        }
    }
}