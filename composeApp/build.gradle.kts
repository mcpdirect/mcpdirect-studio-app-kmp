import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
//    alias(libs.plugins.composeHotReload)
}

kotlin {
    jvm("desktop")

    sourceSets {
        val desktopMain by getting
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.kotlinx.serialization.json)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation("ai.mcpdirect:mcpdirect-studio-core:1.1.2-SNAPSHOT")
        }
    }
}

compose.desktop {

    application {
        val version = "1.0.2"
        mainClass = "ai.mcpdirect.studio.app.MainKt"
        jvmArgs += listOf(
            "-Dai.mcpdirect.studio.app.version=${version}",
            //"-Dai.mcpdirect.hstp.webport=https://your_mcpdirect_gateway_host/hstp/",
            //"-Dai.mcpdirect.hstp.service.gateway=ssl://your_mcpdirect_gateway_host:53100",
        )
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            modules("java.naming")
            packageName = "MCPdirect Studio"
            packageVersion = version
            windows {
                iconFile.set(project.file("icons/icon.ico")) // For Windows
                menu = true
            }
            macOS {
                iconFile.set(project.file("icons/icon.icns")) // For macOS
                dockName = "MCPdirect Studio"
            }
            linux {
                iconFile.set(project.file("icons/icon.png")) // For Linux
            }
        }
    }
}
