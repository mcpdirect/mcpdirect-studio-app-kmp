package ai.mcpdirect.studio.app.model.aitool

class AIPortTeamToolMakerTemplate {
    var templateId: Long = 0
    var teamId: Long = 0
    var status: Int? = null
    var created: Long = 0
    var lastUpdated: Long = 0

    fun templateId(templateId: Long): AIPortTeamToolMakerTemplate {
        this.templateId = templateId
        return this
    }

    fun teamId(teamId: Long): AIPortTeamToolMakerTemplate {
        this.teamId = teamId
        return this
    }

    fun status(status: Int?): AIPortTeamToolMakerTemplate {
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

    companion object {
        fun build(): AIPortTeamToolMakerTemplate {
            return AIPortTeamToolMakerTemplate()
        }
    }
}