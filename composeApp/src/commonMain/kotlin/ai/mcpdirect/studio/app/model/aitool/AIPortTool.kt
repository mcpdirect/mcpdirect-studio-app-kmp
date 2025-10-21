package ai.mcpdirect.studio.app.model.aitool

import kotlinx.serialization.Serializable

@Serializable
open class AIPortTool {
    var id: Long = 0
    var makerId: Long = 0
    var status: Int = 0
    var lastUpdated: Long = 0
    var name: String = ""
    var hash: Int = 0
    var metaData: String = ""
    var tags: String = ""
    var agentId: Long = 0

    constructor()

    constructor(id: Long, makerId: Long, status: Int, lastUpdated: Long, name: String, hash: Int, metaData: String, tags: String) {
        this.id = id
        this.makerId = makerId
        this.status = status
        this.lastUpdated = lastUpdated
        this.name = name
        this.hash = hash
        this.metaData = metaData
        this.tags = tags
    }
    
    fun duplicate(): AIPortTool {
        return AIPortTool(id, makerId, status, lastUpdated, name, hash, metaData, tags)
    }
}