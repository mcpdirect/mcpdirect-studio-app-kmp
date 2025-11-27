package ai.mcpdirect.studio.app.model.repository

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.account.AIPortAccessKey
import ai.mcpdirect.studio.app.model.account.AIPortAccessKeyCredential
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource

object AccessKeyRepository {
    private val loadMutex = Mutex()
    private val _duration = 5.seconds
    private var _accessKeyLastQuery:TimeMark? = null
    private val _accessKeys = MutableStateFlow<Map<Long,AIPortAccessKey>>(emptyMap())
    val accessKeys: StateFlow<Map<Long, AIPortAccessKey>> = _accessKeys
//    val toolPermissionMakerSummary = mutableStateListOf<AIPortToolPermissionMakerSummary>()
    fun reset(){
        _accessKeyLastQuery = null
        _accessKeys.update { map ->
            map.toMutableMap().apply {
                clear()
            }
        }
    }
    suspend fun loadAccessKeys(force:Boolean=false) {
        loadMutex.withLock {
            val now = TimeSource.Monotonic.markNow()
            if (_accessKeyLastQuery == null || (force && _accessKeyLastQuery!!.elapsedNow() > _duration)) {
                generalViewModel.loading()
                getPlatform().queryAccessKeys {
                    if(it.successful()){
                        it.data?.let { accessKeys ->
                            _accessKeys.update { map ->
                                map.toMutableMap().apply {
                                    accessKeys.forEach {
                                        put(it.id,it)
                                    }
                                }
                            }
                        }
                        _accessKeyLastQuery = now
                    }
                    generalViewModel.loaded(
                        "Load MCPdirect Access Keys",it.code,it.message
                    )
                }
            }
        }
    }
    suspend fun generateAccessKey(keyName:String) {
        loadMutex.withLock {
            generalViewModel.loading()
            getPlatform().generateAccessKey(keyName){
                if(it.successful()){
                    it.data?.let { accessKey ->
                        _accessKeys.update { map ->
                            map.toMutableMap().apply {
                                put(accessKey.id,accessKey)
                            }
                        }
                    }
                }
                generalViewModel.loaded(
                    "Generate MCPdirect Access Key\"$keyName\"",it.code,it.message
                )
            }
        }
    }

    suspend fun modifyAccessKey(key: AIPortAccessKey, status:Int?=null, name:String?=null) {
        loadMutex.withLock {
            generalViewModel.loading()
            getPlatform().modifyAccessKey(key.id,status,name){
                if(it.successful()){
                    it.data?.let { accessKey ->
                        _accessKeys.update { map ->
                            map.toMutableMap().apply {
                                put(accessKey.id,accessKey)
                            }
                        }
                    }
                }
                generalViewModel.loaded(
                    "Modify MCPdirect Access Key\"${key.name}\"",it.code,it.message
                )
            }
        }
    }
    suspend fun getAccessKeyCredential(key: AIPortAccessKey,
                                          onResponse: (resp: AIPortAccessKeyCredential?) -> Unit){
        loadMutex.withLock {
            generalViewModel.loading()
            getPlatform().getAccessKeyCredential(key.id){
                onResponse(it.data)
                generalViewModel.loaded(
                    "Get MCPdirect Access Key Credential\"${key.name}\"",it.code,it.message
                )
            }
        }

    }
}