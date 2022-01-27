import java.io.InputStream
import java.io.OutputStream
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

}