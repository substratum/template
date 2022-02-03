import java.io.*
import java.util.*
import javax.crypto.Cipher

object Util {

    /**
     * @receiver Gradle project directory.
     * @return Assets directory.
     */
    val File.assets get() = File(this, "/src/main/assets")

    /**
     * @receiver Gradle project directory.
     * @return Assets directory.
     */
    val File.tempAssets get() = File(this, "/src/main/assets-temp")

    /**
     * Copies this [InputStream] to the given [OutputStream] and encrypts the content of it.
     */
    fun InputStream.copyEncryptedTo(
        out: OutputStream,
        cipher: Cipher,
        bufferSize: Int = DEFAULT_BUFFER_SIZE
    ) {
        val buffer = ByteArray(bufferSize)
        var bytesRead = read(buffer)
        while (bytesRead != -1) {
            val output = cipher.update(buffer, 0, bytesRead)
            if (output != null) {
                out.write(output)
            }
            bytesRead = read(buffer)
        }
        val output = cipher.doFinal()
        if (output != null) {
            out.write(output)
        }
    }

    /**
     * Cleans the encrypted assets in the project.
     * @receiver Gradle project directory.
     */
    fun File.cleanEncryptedAssets() {
        val tempAssets = tempAssets
        if (tempAssets.exists()) {
            println("Cleaning duplicated encrypted assets, not your decrypted assets...")
            assets.deleteRecursively()

            tempAssets.walkTopDown().filter { it.isFile }.forEach { file ->
                file.twistAsset("assets-temp", "assets")
            }
            tempAssets.deleteRecursively()
        }
    }

    /**
     * Copies the asset to a new destination by changing its [oldName] in the path with [newName].
     */
    fun File.twistAsset(oldName: String, newName: String) {
        val fo = File(absolutePath.replace(oldName, newName))
            .apply {
                parentFile.mkdirs()
            }

        FileInputStream(this).use { fis ->
            FileOutputStream(fo).use { fos ->
                fis.copyTo(fos, bufferSize = 4096)
            }
        }
    }

    fun generateRandomByteArray() =
        ByteArray(16).apply {
            Random().nextBytes(this)
        }
}