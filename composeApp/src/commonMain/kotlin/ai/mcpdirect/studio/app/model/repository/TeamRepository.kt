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

object TeamRepository {
    private val loadMutex = Mutex()
    private val duration = 10000
    private var _teamLastUpdated = 0L
    private val _teams = MutableStateFlow<Map<Long, AIPortTeam>>(emptyMap())
    val teams: StateFlow<Map<Long, AIPortTeam>> = _teams

    suspend fun loadTeams(lastQuery:Long=currentMilliseconds()){
        if(_teamLastUpdated==0L|| lastQuery-_teamLastUpdated>duration) loadMutex.withLock {
            generalViewModel.loading()
            getPlatform().queryTeams{
                if(it.successful()) it.data?.let { list ->
                    _teams.update { map ->
                        map.toMutableMap().apply {
                            for (team in list){
                                put(team.id,team)
                                if(team.lastUpdated>_teamLastUpdated)
                                    _teamLastUpdated = team.lastUpdated
                            }
                        }
                    }
                }
                generalViewModel.loading(it.code)
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