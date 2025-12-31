package ai.mcpdirect.studio.app.model.repository

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAccessKey
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAccessKeyCredential
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
    private val _accessKeys = MutableStateFlow<Map<Long,AIPortToolAccessKey>>(emptyMap())
    val accessKeys: StateFlow<Map<Long, AIPortToolAccessKey>> = _accessKeys
//    val toolPermissionMakerSummary = mutableStateListOf<AIPortToolPermissionMakerSummary>()
    fun reset(){
        _accessKeyLastQuery = null
        _accessKeys.update { map ->
            map.toMutableMap().apply {
                clear()
            }
        }
    }
    suspend fun loadAccessKeys(
        force:Boolean=false,
        onResponse: ((resp: AIPortServiceResponse<List<AIPortToolAccessKey>>) -> Unit)?=null
    ) {
        loadMutex.withLock {
            val now = TimeSource.Monotonic.markNow()
            if (_accessKeyLastQuery == null || (force && _accessKeyLastQuery!!.elapsedNow() > _duration)) {
                generalViewModel.loading()
                getPlatform().queryToolAccessKeys {
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
                    onResponse?.let { response->
                        response(it)
                    }
                }
            }
        }
    }
    suspend fun generateAccessKey(keyName:String) {
        loadMutex.withLock {
            generalViewModel.loading()
            getPlatform().generateToolAccessKey(keyName){
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

    suspend fun modifyAccessKey(key: AIPortToolAccessKey, status:Int?=null, name:String?=null) {
        loadMutex.withLock {
            generalViewModel.loading()
            getPlatform().modifyToolAccessKey(key.id,status,name){
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
    suspend fun getAccessKeyCredential(key: AIPortToolAccessKey,
                                       onResponse: (resp: AIPortToolAccessKeyCredential?) -> Unit){
        loadMutex.withLock {
            generalViewModel.loading()
            getPlatform().getToolAccessKeyCredential(key.id){
                generalViewModel.loaded(
                    "Get MCPdirect Access Key Credential\"${key.name}\"",it.code,it.message
                )
                onResponse(it.data)
            }
        }

    }
}