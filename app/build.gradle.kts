import ThemerConstants.ALLOW_THIRD_PARTY_SUBSTRATUM_BUILDS
import ThemerConstants.APK_SIGNATURE_PRODUCTION
import ThemerConstants.BASE_64_LICENSE_KEY
import ThemerConstants.ENABLE_APP_BLACKLIST_CHECK
import ThemerConstants.ENFORCE_GOOGLE_PLAY_INSTALL
import ThemerConstants.SHOULD_ENCRYPT_ASSETS
import ThemerConstants.SUPPORTS_THIRD_PARTY_SYSTEMS
import Util.copyEncryptedTo
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

plugins {
    id("com.android.application")
    kotlin("android")
}

// Themers: DO NOT MODIFY
val key = ByteArray(16).apply {
    Random().nextBytes(this)
}

// Themers: DO NOT MODIFY
val ivKey = ByteArray(16).apply {
    Random().nextBytes(this)
}

android {
    compileSdk = 31

    defaultConfig {
        // If you're planning to change up the package name, ensure you have read the readme
        // thoroughly!
        applicationId = "substratum.theme.template"
        // We are only supporting Nougat and above, all new changes will incorporate Nougat changes
        // to the substratum repo rather than anything lower. Keep targetSdkVersion the same.
        minSdk = 24
        // Both versions must be changed to increment on Play Store/user's devices
        versionCode = 2
        versionName = "2.0"

        // Themers: DO NOT MODIFY
        buildConfigField("boolean", "SUPPORTS_THIRD_PARTY_SYSTEMS", "$SUPPORTS_THIRD_PARTY_SYSTEMS")
        buildConfigField("boolean", "ENABLE_APP_BLACKLIST_CHECK", "$ENABLE_APP_BLACKLIST_CHECK")
        buildConfigField("boolean", "ALLOW_THIRD_PARTY_SUBSTRATUM_BUILDS", "$ALLOW_THIRD_PARTY_SUBSTRATUM_BUILDS")
        buildConfigField("String", "IV_KEY", "\"$ivKey\"")
        buildConfigField("byte[]", "DECRYPTION_KEY", key.joinToString(prefix = "{", postfix = "}"))
        buildConfigField("byte[]", "IV_KEY", ivKey.joinToString(prefix = "{", postfix = "}"))
        resValue("string", "encryption_status", if (shouldEncrypt()) "onCompileVerify" else "false")
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")

            // Themers: DO NOT MODIFY
            buildConfigField("boolean", "ENFORCE_GOOGLE_PLAY_INSTALL", "false")
            buildConfigField("String", "BASE_64_LICENSE_KEY", "\"\"")
            buildConfigField("String", "APK_SIGNATURE_PRODUCTION", "\"\"")
        }
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")

            // Themers: DO NOT MODIFY
            buildConfigField("boolean", "ENFORCE_GOOGLE_PLAY_INSTALL", "$ENFORCE_GOOGLE_PLAY_INSTALL")
            buildConfigField("String", "BASE_64_LICENSE_KEY", "\"$BASE_64_LICENSE_KEY\"")
            buildConfigField("String", "APK_SIGNATURE_PRODUCTION", "\"$APK_SIGNATURE_PRODUCTION\"")
        }
    }
    sourceSets {
        named("main") {
            java.srcDir("src/main/kotlin")
        }
    }
}

dependencies {
    //implementation(fileTree(include = ["*.jar"], dir = "libs"))
    implementation("com.github.javiersantos:PiracyChecker:1.2.5")
    implementation(kotlin("stdlib-jdk8", version = Constants.kotlinVersion))
    implementation("androidx.appcompat:appcompat:1.4.1")
}

// Themers: DO NOT MODIFY ANYTHING BELOW
tasks.register("encryptAssets") {
    if (!shouldEncrypt()) {
        println("Skipping assets encryption...")
        return@register
    }

    // Check if temp assets exist
    if (!File(projectDir, "/src/main/assets-temp").exists()) {
        println("Encrypting duplicated assets, don't worry, your original assets are safe...")

        val list = mutableListOf<File>()

        // Encrypt every single file in the assets dir recursively
        File(projectDir, "/src/main/assets").walkTopDown().filter { it.isFile }.forEach { file ->
            list.add(file)

            val fo = File(file.absolutePath.replace("assets", "assets-temp"))
                .apply {
                    parentFile.mkdirs()
                }

            FileInputStream(file).use { fis ->
                FileOutputStream(fo).use { fos ->
                    fis.copyTo(fos, bufferSize = 4096)
                }
            }
        }

        list.forEach { file ->
            if (file.absolutePath.contains("overlays")) {
                val secret = SecretKeySpec(key, "AES")
                val iv = IvParameterSpec(ivKey)

                val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
                    .apply {
                        init(Cipher.ENCRYPT_MODE, secret, iv)
                    }

                FileInputStream(file).use { fis ->
                    FileOutputStream(file.absolutePath + ".enc").use { fos ->
                        fis.copyEncryptedTo(fos, cipher, bufferSize = 64)
                    }
                }

                file.delete()
            }
        }
    } else {
        throw RuntimeException("Old temporary assets found! Try and do a clean project.")
    }
}

project.afterEvaluate {
    tasks.named("preBuild") {
        dependsOn("encryptAssets")
    }
}

gradle.buildFinished {
    val tempAssets = File(projectDir, "/src/main/assets-temp")
    if (tempAssets.exists()) {
        println("Cleaning duplicated encrypted assets, not your decrypted assets...")

        // Delete encrypted assets
        File(projectDir, "src/main/assets").delete()

        tempAssets.walkTopDown().filter { it.isFile }.forEach { file ->
            val fo = File(file.absolutePath.replace("assets-temp", "assets"))
            fo.parentFile.mkdirs()

            FileInputStream(file).use { fis ->
                FileOutputStream(fo).use { fos ->
                    fis.copyTo(fos, bufferSize = 4096)
                }
            }
        }
        tempAssets.delete()
    }
}

fun shouldEncrypt(): Boolean {
    val tasks = project.gradle.startParameter.taskNames
    return SHOULD_ENCRYPT_ASSETS && tasks.joinToString().contains("release", ignoreCase = true)
}
