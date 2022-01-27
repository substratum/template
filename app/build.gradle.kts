import ThemerConstants.ALLOW_THIRD_PARTY_SUBSTRATUM_BUILDS
import ThemerConstants.APK_SIGNATURE_PRODUCTION
import ThemerConstants.BASE_64_LICENSE_KEY
import ThemerConstants.ENABLE_APP_BLACKLIST_CHECK
import ThemerConstants.ENFORCE_GOOGLE_PLAY_INSTALL
import ThemerConstants.SHOULD_ENCRYPT_ASSETS
import ThemerConstants.SUPPORTS_THIRD_PARTY_SYSTEMS

import java.util.Random
import java.io.FileInputStream
import java.io.FileOutputStream

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

plugins {
    id("com.android.application")
    id("kotlin-android")
}

val key = ByteArray(16).apply {
    Random().nextBytes(this)
}

val ivKey = ByteArray(16).apply {
    Random().nextBytes(this)
}

android {
    compileSdk = 30

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
    implementation(kotlin("stdlib-jdk8"))
    implementation("androidx.appcompat:appcompat:1.4.1")
}

// Themers: DO NOT MODIFY ANYTHING BELOW
tasks.register("encryptAssets") {
    if (!shouldEncrypt()) {
        println("Skipping assets encryption...")
        return@register
    }

    val tempAssets = File(projectDir, "/src/main/assets-temp")
    if (!tempAssets.exists()) {
        println("Encrypting duplicated assets, don't worry, your original assets are safe...")
        val list = mutableListOf<File>()
        val dir = File(projectDir, "/src/main/assets")
        dir.listFiles()?.filter { it.isFile }?.forEach { file ->
            list.add(file)

            val fis = FileInputStream(file)
            val fo = File(file.absolutePath.replace("assets", "assets-temp"))
            fo.parentFile.mkdirs()
            val fos = FileOutputStream(fo)
            val buffer = ByteArray(4096)
            var n: Int
            while (fis.read(buffer).also { n = it } != -1) {
                fos.write(buffer, 0, n)
            }
            fis.close()
            fos.close()
        }

        list.forEach { file ->
            if (file.absolutePath.contains("overlays")) {
                val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
                val secret = SecretKeySpec(key, "AES")
                val iv = IvParameterSpec(ivKey)

                cipher.init(Cipher.ENCRYPT_MODE, secret, iv)
                val fis = FileInputStream(file)
                val fos =  FileOutputStream(file.absolutePath + ".enc")

                val input = ByteArray(64)
                var bytesRead: Int
                while (fis.read(input).also {bytesRead = it } != -1) {
                    val output = cipher.update(input, 0, bytesRead)
                    if (output != null) {
                        fos.write(output)
                    }
                }
                val output = cipher.doFinal()
                if (output != null) {
                    fos.write(output)
                }
                fis.close()
                fos.flush()
                fos.close()

                file.delete()
            }
        }
    } else {
        throw RuntimeException("Old temporary assets found! Try and do a clean project.")
    }
}

project.afterEvaluate {
    tasks.named("preBuild"){
        dependsOn("encryptAssets")
    }
}

gradle.buildFinished {
    val tempAssets = File(projectDir, "/src/main/assets-temp")
    if (tempAssets.exists()) {
        println("Cleaning duplicated encrypted assets, not your decrypted assets...")
        val encryptedAssets = File(projectDir, "src/main/assets")
        encryptedAssets.delete()

        tempAssets.listFiles()?.filter{ it.isFile }?.forEach { file ->
            val fis = FileInputStream(file)
            val fo = File(file.absolutePath.replace("assets-temp", "assets"))
            fo.parentFile.mkdirs()
            val fos = FileOutputStream(fo)
            val buffer = ByteArray(4096)
            var n: Int
            while (fis.read(buffer).also { n = it } != -1) {
                fos.write(buffer, 0, n)
            }
            fis.close()
            fos.close()
        }
        tempAssets.delete()
    }
}

fun shouldEncrypt(): Boolean {
    val tasks = project.gradle.startParameter.taskNames
    return SHOULD_ENCRYPT_ASSETS && tasks.joinToString { it.toLowerCase() }.contains("release")
}
