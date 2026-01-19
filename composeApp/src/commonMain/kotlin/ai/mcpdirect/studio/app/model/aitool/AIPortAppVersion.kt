package ai.mcpdirect.studio.app.model.aitool

class AIPortAppVersion {
    var appId: Int = 0
    var platform: Int = 0
    var architecture: Int = 0
    var version: String? = null
    var versionCode: Int = 0
    var status: Int = 0
    var mandatory: Boolean = false
    var releaseNotes: String? = null
    var url: String? = null
    var created: Long = 0
    companion object{
        const val PLATFORM_MACOS: Int = 100

        const val PLATFORM_IOS: Int = 101

        const val PLATFORM_IPADOS: Int = 102

        const val PLATFORM_WINDOWS: Int = 200

        const val PLATFORM_LINUX: Int = 300

        const val PLATFORM_ANDROID: Int = 400

        const val ARCH_X86: Int = 100

        const val ARCH_X86_64: Int = 101

        const val ARCH_ARM: Int = 200

        const val ARCH_ARM64: Int = 201

        const val STATUS_DEPRECATED: Short = -1

        const val STATUS_PREVIEW: Short = 0

        const val STATUS_RELEASE: Short = 1
    }
}