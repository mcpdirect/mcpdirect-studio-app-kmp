package ai.mcpdirect.studio.app

class Greeting {
    private val platform = _root_ide_package_.ai.mcpdirect.studio.app.getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}