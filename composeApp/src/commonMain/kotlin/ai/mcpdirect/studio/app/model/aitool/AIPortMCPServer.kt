package ai.mcpdirect.studio.app.model.aitool

import kotlinx.serialization.Serializable

@Serializable
class AIPortMCPServer {
    var id: Long = 0
    var name: String = ""
    var url: String? = null
    var transport: Int = 0
    var created: Long = 0
    var command: String? = null
    var args: List<String>? = null
    var env: Map<String,String>? = null
    var inputs:Map<String,String>? = null
    var inputArgs:List<String>? = null
    var inputEnv: Map<String,String>? = null
    constructor()
    constructor(id: Long){
        this.id = id
    }
    constructor(id: Long, name: String, command: String,
                args: List<String>? = null,
                env: Map<String,String>? = null,
                inputs: Map<String,String>? = null,
                inputArgs: List<String>? = null,
        ) {
        this.id = id
        this.name = name
        this.command = command
        this.args = args
        this.env = env
        this.inputs = inputs
        this.inputArgs = inputArgs
    }
    constructor(id: Long, name: String, transport: Int, url: String,
                env: Map<String,String>? = null,
                inputs: Map<String,String>? = null,
                inputEnv: Map<String,String>? = null,
        ) {
        this.id = id
        this.name = name
        this.url = url
        this.transport = transport
        this.env = env
        this.inputs = inputs
        this.inputEnv = inputEnv
    }
}