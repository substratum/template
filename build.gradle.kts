import Util.cleanEncryptedAssets

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
    cleanEncryptedAssets(projectDir)
}

