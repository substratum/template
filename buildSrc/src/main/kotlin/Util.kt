import java.io.*
import javax.crypto.Cipher

object Util {

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

    fun cleanEncryptedAssets(projectDir: File) {
        val tempAssets = File(projectDir, "/src/main/assets-temp")
        if (tempAssets.exists()) {
            println("Cleaning duplicated encrypted assets, not your decrypted assets...")
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

}