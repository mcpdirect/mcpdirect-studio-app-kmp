package ai.mcpdirect.studio.app.model.account

import kotlinx.serialization.Serializable

@Serializable
class AIPortTeam {
    var id: Long = 0
    var name: String = ""
    var created: Long = 0
    var ownerId: Long = 0
    var status: Int? = null
    var lastUpdated: Long = 0
    var ownerName: String = ""
    var ownerAccount: String = ""

    fun id(id: Long): AIPortTeam {
        this.id = id
        return this
    }

    fun name(name: String): AIPortTeam {
        this.name = name
        return this
    }

    fun created(created: Long): AIPortTeam {
        this.created = created
        return this
    }

    fun ownerId(ownerId: Long): AIPortTeam {
        this.ownerId = ownerId
        return this
    }

    fun status(status: Int?): AIPortTeam {
        this.status = status
        return this
    }

    fun lastUpdated(lastUpdated: Long): AIPortTeam {
        this.lastUpdated = lastUpdated
        return this
    }

    companion object {
        fun build(): AIPortTeam {
            return AIPortTeam()
        }
    }
}