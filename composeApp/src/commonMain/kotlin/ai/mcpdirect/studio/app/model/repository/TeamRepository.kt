package ai.mcpdirect.studio.app.model.repository

import ai.mcpdirect.mcpdirectstudioapp.currentMilliseconds
import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.account.AIPortTeam
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource

object TeamRepository {
    private val loadMutex = Mutex()
    private val _duration = 5.seconds
    private var _teamLastQuery: TimeMark? = null
    private val _teams = MutableStateFlow<Map<Long, AIPortTeam>>(emptyMap())
    val teams: StateFlow<Map<Long, AIPortTeam>> = _teams

    suspend fun loadTeams(force: Boolean=false){
        loadMutex.withLock {
            val now = TimeSource.Monotonic.markNow()
            if(_teamLastQuery==null|| (force&&_teamLastQuery!!.elapsedNow()> _duration)) {
                generalViewModel.loading()
                println(currentMilliseconds())
                getPlatform().queryTeams(
                    lastUpdated = if (_teamLastQuery == null) 0L else currentMilliseconds()
                ) {
                    if (it.successful()) it.data?.let { list ->
                        _teams.update { map ->
                            map.toMutableMap().apply {
                                for (team in list) {
                                    put(team.id, team)
                                }
                            }
                        }
                        _teamLastQuery = now
                    }
                    generalViewModel.loading(it.code)
                }
            }
        }
    }
    fun loadTeam(teamId:Long,
                 onResponse:(code:Int,message:String?,data: AIPortTeam?)->Unit) {
        val team = _teams.value[teamId];
        if(team!=null) onResponse(0,null,team)
        else onResponse(255,null,null)
    }
}