import java.io.FileInputStream
import java.io.FileOutputStream

buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.1.0")
        classpath(kotlin("gradle-plugin", version = Constants.kotlinVersion))
    }
}

allprojects {
    repositories {
        google()
        maven(url = "https://jitpack.io")
        mavenCentral()
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)

    val tempAssets = File(projectDir, "/src/main/assets-temp")
    if (tempAssets.exists()) {
        println("cleaning encrypted assets...")

        File(projectDir, "src/main/assets").deleteRecursively()

        tempAssets.walkTopDown().filter { it.isFile }.forEach { file ->
            val fo = File(file.absolutePath.replace("assets-temp", "assets"))
                .apply {
                    parentFile.mkdirs()
                }

            FileInputStream(file).use { fis ->
                FileOutputStream(fo).use { fos ->
                    fis.copyTo(fos, bufferSize = 4096)
                }
            }
        }
        tempAssets.deleteRecursively()
    }
}

