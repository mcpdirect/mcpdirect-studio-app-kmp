package ai.mcpdirect.studio.app.model.repository

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.aitool.AIPortAppVersion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object AppVersionRepository {
    private val loadMutex = Mutex()
    private val _version = MutableStateFlow<AIPortAppVersion>(AIPortAppVersion())
    val version: StateFlow<AIPortAppVersion> = _version
    fun reset(){
        _version.value = AIPortAppVersion()
    }
    fun updateAppVersion(version: AIPortAppVersion) {
        _version.value = version
    }
    suspend fun checkAppVersion(
        onResponse: (resp: AIPortServiceResponse<AIPortAppVersion>) -> Unit
    ){
        loadMutex.withLock {
            getPlatform().checkAppVersion{
                if(it.successful()) it.data?.let { data->
                    _version.value = data
                }
                onResponse(it)
            }
        }
    }
}