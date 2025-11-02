package ai.mcpdirect.studio.app.model.account

import kotlinx.serialization.Serializable

@Serializable
class AIPortTeamMember {
    var teamId: Long = 0
    var memberId: Long = 0
    var status: Int = 0
    var created: Long = 0
    var expirationDate: Long = 0
    var lastUpdated: Long = 0
    var name: String = ""
    var account: String = ""

    fun teamId(teamId: Long): AIPortTeamMember {
        this.teamId = teamId
        return this
    }

    fun memberId(memberId: Long): AIPortTeamMember {
        this.memberId = memberId
        return this
    }

    fun status(status: Int): AIPortTeamMember {
        this.status = status
        return this
    }

    fun created(created: Long): AIPortTeamMember {
        this.created = created
        return this
    }

    fun expirationDate(expirationDate: Long): AIPortTeamMember {
        this.expirationDate = expirationDate
        return this
    }

    fun lastUpdated(lastUpdated: Long): AIPortTeamMember {
        this.lastUpdated = lastUpdated
        return this
    }

    companion object {
        data class Key(val teamId: Long,val memberId: Long)
        fun key(teamId: Long,memberId: Long):Key{
            return Key(teamId,memberId)
        }
        fun build(): AIPortTeamMember {
            return AIPortTeamMember()
        }
    }
}