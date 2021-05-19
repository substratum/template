import java.io.FileInputStream
import java.io.FileOutputStream

buildscript {
    extra["kotlin_version"] = Constants.kotlinVersion
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.2.1")
        classpath(kotlin("gradle-plugin", version = Constants.kotlinVersion))
    }
}

tasks {
    wrapper {
        gradleVersion = "7.0.2"
        distributionType = Wrapper.DistributionType.ALL
    }
}

allprojects {
    repositories {
        google()
        maven("https://jitpack.io")
        mavenCentral()
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
    val tempAssets = File(projectDir, "/src/main/assets-temp")
    if (tempAssets.exists()) {
        println("cleaning encrypted assets...")
        val encryptedAssets = File(projectDir, "src/main/assets")
        encryptedAssets.delete()

        tempAssets.listFiles()?.filter { it.isFile }?.forEach { file ->
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

