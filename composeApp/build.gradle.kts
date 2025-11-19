import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinxSerialization)
}
//val properties = Properties().apply {
//    rootProject.file("version.properties").inputStream().use { load(it) }
//}
//val appVersion = properties.getProperty("version", "dev")
val appVersion = "2.2.0"
val generatedSrcDirPath = "generated/compose/srcGenerator"
val mcpdirectGatewayEndpoint = System.getenv("AI_MCPDIRECT_GATEWAY_ENDPOINT")?:"http://localhost:8088/"
println("AI_MCPDIRECT_GATEWAY_ENDPOINT=$mcpdirectGatewayEndpoint")
val mcpdirectHSTPWebport = System.getenv("AI_MCPDIRECT_HSTP_WEBPORT")?:"http://localhost:8088/hstp/"
println("AI_MCPDIRECT_HSTP_WEBPORT=$mcpdirectHSTPWebport")
val mcpdirectHSTPServiceGateway = System.getenv("AI_MCPDIRECT_HSTP_SERVICE_GATEWAY")?:"ssl://localhost:53100"
println("AI_MCPDIRECT_HSTP_SERVICE_GATEWAY=$mcpdirectHSTPServiceGateway")

//tasks{
//    register("generateAppInfo") {
//        println("tasks.register(\"generateAppInfo\")")
//        val outputDir = generatedSrcDir.get().asFile
//        val outputFile = project.layout.buildDirectory.file("ai/mcpdirect/mcpdirectstudioapp/AppInfo")
//        outputs.file(outputFile)
//        doLast {
//            outputDir.mkdirs()
//            outputFile.get().asFile.writeText("""
//            package ai.mcpdirect.mcpdirectstudioapp
//
//            actual object AppInfo {
//                val version: String = "$version"
//                val mcpdirectGatewayEndpoint: String? = "${System.getenv("AI_MCPDIRECT_GATEWAY_ENDPOINT")}"
//            }
//            """.trimIndent()
//            )
//            println("✅ Generated AppInfo.kt: version=$version")
//        }
//    }
//}

kotlin {
    jvm()

    js {
        browser()
        binaries.executable()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        commonMain {
            val generatedSrcCommonMainDirPath = "${generatedSrcDirPath}/commonMain"
            val generatedSrcDir = layout.buildDirectory.dir(generatedSrcCommonMainDirPath)
            kotlin{
//                val outputDir = generatedSrcDir.get().asFile

                val outputFile = project.layout.buildDirectory.file("${generatedSrcCommonMainDirPath}/ai/mcpdirect/mcpdirectstudioapp/AppInfo.kt").get().asFile
                outputFile.parentFile.mkdirs()
//                outputDir.mkdirs()
                outputFile.writeText("""
                    package ai.mcpdirect.mcpdirectstudioapp
                    object AppInfo {
                        const val APP_VERSION = "$appVersion"
                        const val MCPDIRECT_GATEWAY_ENDPOINT = "$mcpdirectGatewayEndpoint"
                        const val MCPDIRECT_HSTP_WEBPORT = "$mcpdirectHSTPWebport"
                    }""".trimIndent()
                )
                srcDir(generatedSrcDir)
            }
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)
                implementation(libs.kotlinx.serialization.json)
            }
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
//            implementation(compose.material3)
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation("ai.mcpdirect:mcpdirect-studio-core:2.2.0-SNAPSHOT")
        }
        jsMain.dependencies {
//            implementation(compose.material3)
        }
        wasmJsMain.dependencies {
//            implementation(compose.material3)
        }
    }
}

compose.desktop {
    application {
        mainClass = "ai.mcpdirect.mcpdirectstudioapp.MainKt"
        jvmArgs += listOf(
            "-Dai.mcpdirect.gateway.endpoint=$mcpdirectGatewayEndpoint",
            "-Dai.mcpdirect.hstp.webport=$mcpdirectHSTPWebport",
            "-Dai.mcpdirect.hstp.service.gateway=$mcpdirectHSTPServiceGateway",
        )
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            modules("java.naming")
            packageName = "MCPdirect Studio"
            packageVersion = appVersion
            windows {
                iconFile.set(project.file("icons/icon.ico")) // For Windows
                menu = true
            }
            macOS {
                entitlementsFile.set(project.file("resources/entitlements.plist"))

                iconFile.set(project.file("icons/icon.icns")) // For macOS
                dockName = "MCPdirect Studio"

            }
            linux {
                iconFile.set(project.file("icons/icon.png")) // For Linux
            }
        }
    }
}
