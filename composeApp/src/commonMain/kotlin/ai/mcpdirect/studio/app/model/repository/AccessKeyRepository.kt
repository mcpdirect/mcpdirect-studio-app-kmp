package ai.mcpdirect.studio.app.model.repository

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.account.AIPortAccessKey
import ai.mcpdirect.studio.app.model.aitool.AIPortToolPermissionMakerSummary
import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.collections.set
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource

object AccessKeyRepository {
    private val loadMutex = Mutex()
    private val _duration = 5.seconds
    private var _accessKeyLastQuery:TimeMark? = null
    val _accessKeys = MutableStateFlow<Map<Long,AIPortAccessKey>>(emptyMap())
    val accessKeys: StateFlow<Map<Long, AIPortAccessKey>> = _accessKeys
    val toolPermissionMakerSummary = mutableStateListOf<AIPortToolPermissionMakerSummary>()
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
}