package ai.mcpdirect.studio.app.model.aitool

import kotlinx.serialization.Serializable

@Serializable
class AIPortTeamToolMakerTemplate {
    var toolMakerTemplateId: Long = 0
    var teamId: Long = 0
    var status: Int = -1
    var created: Long = 0
    var lastUpdated: Long = 0

    fun templateId(templateId: Long): AIPortTeamToolMakerTemplate {
        this.toolMakerTemplateId = templateId
        return this
    }

    fun teamId(teamId: Long): AIPortTeamToolMakerTemplate {
        this.teamId = teamId
        return this
    }

    fun status(status: Int): AIPortTeamToolMakerTemplate {
        this.status = status
        return this
    }

    fun created(created: Long): AIPortTeamToolMakerTemplate {
        this.created = created
        return this
    }

    fun lastUpdated(lastUpdated: Long): AIPortTeamToolMakerTemplate {
        this.lastUpdated = lastUpdated
        return this
    }

    fun copy(): AIPortTeamToolMakerTemplate {
        return build()
            .templateId(toolMakerTemplateId)
            .teamId(teamId)
            .status(status)
            .created(created)
            .lastUpdated(lastUpdated)
    }
    companion object {
        data class Key(val teamId: Long,val templateId: Long)
        fun key(teamId: Long,templateId: Long):Key{
            return Key(teamId,templateId)
        }
        fun build(): AIPortTeamToolMakerTemplate {
            return AIPortTeamToolMakerTemplate()
        }
    }
}