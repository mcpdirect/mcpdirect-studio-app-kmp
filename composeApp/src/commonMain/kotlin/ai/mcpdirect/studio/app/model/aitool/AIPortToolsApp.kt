package ai.mcpdirect.studio.app.model.aitool

import kotlinx.serialization.Serializable

/**
 * Represents a tool application in the AI Port system.
 */
@Serializable
class AIPortToolsApp {
    var id: Long = 0
    var name: String = ""
    var description: String = ""
    var summary: String = ""
    var developer: String = ""
    var version: String = ""
    var rating: Int = 0

    constructor()

    constructor(name: String, description: String, summary: String, developer: String, version: String) {
        this.name = name
        this.description = description
        this.summary = summary
        this.developer = developer
        this.version = version
        this.rating = 0
    }
}