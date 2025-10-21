package ai.mcpdirect.mcpdirectstudioapp

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "字体, ${platform.name}!"
    }
}