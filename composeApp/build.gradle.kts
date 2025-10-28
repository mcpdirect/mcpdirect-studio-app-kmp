import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinxSerialization)
}

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
        commonMain.dependencies {
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
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation("ai.mcpdirect:mcpdirect-studio-core:1.2.0-SNAPSHOT")
        }
        jsMain.dependencies {
            implementation(compose.material)
        }
        wasmJsMain.dependencies {
            implementation(compose.material)
        }
    }
}

compose.desktop {
    application {
        val version = System.getenv("AI_MCPDIRECT_STUDIO_APP_VERSION")
        mainClass = "ai.mcpdirect.mcpdirectstudioapp.MainKt"
        jvmArgs += listOf(
            "-Dai.mcpdirect.studio.app.version=${version}",
            "-Dai.mcpdirect.gateway.endpoint=${System.getenv("AI_MCPDIRECT_GATEWAY_ENDPOINT")}",
            "-Dai.mcpdirect.hstp.webport=${System.getenv("AI_MCPDIRECT_HSTP_WEBPORT")}",
            "-Dai.mcpdirect.hstp.service.gateway=${System.getenv("AI_MCPDIRECT_HSTP_SERVICE_GATEWAY")}",
        )
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ai.mcpdirect.mcpdirectstudioapp"
            packageVersion = version
        }
    }
}
