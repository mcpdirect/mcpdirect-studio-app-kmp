package ai.mcpdirect.studio.app.model.repository

import ai.mcpdirect.mcpdirectstudioapp.currentMilliseconds
import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.account.AIPortTeam
import ai.mcpdirect.studio.app.model.aitool.AIPortTeamToolMaker
import ai.mcpdirect.studio.app.model.aitool.AIPortTeamToolMakerTemplate
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource

object TeamRepository {
    private val loadMutex = Mutex()
    private val _duration = 5.seconds
    private var _teamLastQuery: TimeMark? = null
    private val _teams = MutableStateFlow<Map<Long, AIPortTeam>>(emptyMap())
    val teams: StateFlow<Map<Long, AIPortTeam>> = _teams

    data class TeamKey(val teamId: Long, val keyId: Long)
    private var _teamToolMakerTemplateLastQuery: TimeMark? = null
    private val _teamToolMakerTemplates = MutableStateFlow<Map<TeamKey, AIPortTeamToolMakerTemplate>>(emptyMap())
    val teamToolMakerTemplates: StateFlow<Map<TeamKey, AIPortTeamToolMakerTemplate>> = _teamToolMakerTemplates

    private var _teamToolMakerLastQuery: TimeMark? = null
    private val _teamToolMakers = MutableStateFlow<Map<TeamKey, AIPortTeamToolMaker>>(emptyMap())
    val teamToolMakers: StateFlow<Map<TeamKey, AIPortTeamToolMaker>> = _teamToolMakers


    fun reset(){
        _teamLastQuery = null
        _teams.update { map ->
            map.toMutableMap().apply {
                clear()
            }
        }
    }
    suspend fun loadTeams(force: Boolean=false){
        loadMutex.withLock {
            val now = TimeSource.Monotonic.markNow()
            if(_teamLastQuery==null|| (force&&_teamLastQuery!!.elapsedNow()> _duration)) {
                generalViewModel.loading()
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
                    generalViewModel.loaded("Load Teams",it.code,it.message)
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

    suspend fun loadTeamToolMakerTemplates(force: Boolean=false){
        loadMutex.withLock {
            val now = TimeSource.Monotonic.markNow()
            if (_teamToolMakerTemplateLastQuery == null || (force && _teamToolMakerTemplateLastQuery!!.elapsedNow() > _duration)) {
                generalViewModel.loading()
                getPlatform().queryTeamToolMakerTemplates(
                    lastUpdated = if (_teamToolMakerTemplateLastQuery == null) 0L else currentMilliseconds()
                ){
                    if(it.successful())it.data?.let { templates ->
                        _teamToolMakerTemplates.update { map ->
                            map.toMutableMap().apply {
                                for (template in templates) {
                                    put(TeamKey(template.teamId,template.toolMakerTemplateId),template)
                                }
                            }
                        }
                        _teamToolMakerTemplateLastQuery = now
                    }
                    generalViewModel.loaded("Load Team Tool Maker Templates",it.code,it.message)
                }
            }
        }
    }

    suspend fun loadTeamToolMakers(force: Boolean=false){
        loadMutex.withLock {
            val now = TimeSource.Monotonic.markNow()
            if (_teamToolMakerLastQuery == null || (force && _teamToolMakerLastQuery!!.elapsedNow() > _duration)) {
                generalViewModel.loading()
                getPlatform().queryTeamToolMakers(
                    lastUpdated = if (_teamToolMakerLastQuery == null) 0L else currentMilliseconds()
                ){
                    if(it.successful())it.data?.let { makers ->
                        _teamToolMakers.update { map ->
                            map.toMutableMap().apply {
                                for (maker in makers) {
                                    put(TeamKey(maker.teamId,maker.toolMakerId),maker)
                                }
                            }
                        }
                        _teamToolMakerLastQuery = now
                    }
                    generalViewModel.loaded("Load Team Tool Makers",it.code,it.message)
                }
            }
        }
    }
    fun toolMakers(team: AIPortTeam): List<AIPortToolMaker>{
        val ttmts =_teamToolMakerTemplates.value.values.filter { it.teamId==team.id }.map { it.toolMakerTemplateId }.toList()
        val ttms =_teamToolMakers.value.values.filter { it.teamId==team.id }.map { it.toolMakerId }.toList()
        return ToolRepository.toolMakers.value.values.filter { it.id in ttms||it.templateId in ttmts}
    }
}