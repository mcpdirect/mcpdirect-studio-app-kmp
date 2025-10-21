package ai.mcpdirect.studio.app.model.aitool

import kotlinx.serialization.Serializable

@Serializable
class AIPortTeamToolMaker {
    var toolMakerId: Long = 0
    var teamId: Long = 0
    var status: Int? = null
    var created: Long = 0
    var lastUpdated: Long = 0

    fun toolMakerId(toolMakerId: Long): AIPortTeamToolMaker {
        this.toolMakerId = toolMakerId
        return this
    }

    fun teamId(teamId: Long): AIPortTeamToolMaker {
        this.teamId = teamId
        return this
    }

    fun status(status: Int?): AIPortTeamToolMaker {
        this.status = status
        return this
    }

    fun created(created: Long): AIPortTeamToolMaker {
        this.created = created
        return this
    }

    fun lastUpdated(lastUpdated: Long): AIPortTeamToolMaker {
        this.lastUpdated = lastUpdated
        return this
    }
    
    fun copy(): AIPortTeamToolMaker {
        return _root_ide_package_.ai.mcpdirect.studio.app.model.aitool.AIPortTeamToolMaker.Companion.build()
            .toolMakerId(toolMakerId)
            .teamId(teamId)
            .status(status)
            .created(created)
            .lastUpdated(lastUpdated)
    }

    companion object {
        fun build(): AIPortTeamToolMaker {
            return AIPortTeamToolMaker()
        }
    }
}