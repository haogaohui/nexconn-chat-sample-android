package ai.nexconn.chat.sample.directmedia

import ai.nexconn.chat.NCEngine
import ai.nexconn.chat.channel.DirectChannel
import ai.nexconn.chat.error.NCError
import ai.nexconn.chat.handler.SendMediaMessageHandler
import ai.nexconn.chat.message.ImageMessage
import ai.nexconn.chat.message.Message
import ai.nexconn.chat.params.ConnectParams
import ai.nexconn.chat.params.SendMediaMessageParams
import ai.nexconn.chat.sample.module.LogOutputActivity
import ai.nexconn.chat.sample.module.SampleConfig
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import java.io.File

class MainActivity : LogOutputActivity() {
    private var selectedImagePath: String? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                val path = copyUriToCache(uri)
                if (path != null) {
                    selectedImagePath = path
                    appendLog("Image selected: ${File(path).name}")
                } else {
                    appendLog("Failed to read selected image.")
                }
            } else {
                appendLog("No image selected.")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Send Image Message"
        addActionButton("1. Connect") { onSetup() }
        addActionButton("2. Pick Image") { onPickImage() }
        addActionButton("3. Send Image Message") { onSendImage() }
    }

    private fun onSetup() {
        NCEngine.connect(ConnectParams(SampleConfig.token)) { userId, error ->
            if (error != null) {
                appendLog("Connection failed: ${error.message} (${error.code})")
                return@connect
            }
            appendLog("Connected successfully, userId=$userId")
        }
    }

    private fun onPickImage() {
        pickImageLauncher.launch("image/*")
    }

    private fun onSendImage() {
        val path = selectedImagePath
        if (path == null) {
            appendLog("Please pick an image first.")
            return
        }

        val imageMessage =
            ImageMessage().apply {
                localPath = path
            }
        val channel = DirectChannel(SampleConfig.directTargetId)
        channel.sendMediaMessage(
            SendMediaMessageParams(imageMessage),
            object : SendMediaMessageHandler {
                override fun onAttached(message: Message) {
                    appendLog("Message attached, messageId=${message.messageId}")
                }

                override fun onProgress(
                    message: Message,
                    progress: Int,
                ) {
                    appendLog("Upload progress: $progress%")
                }

                override fun onResult(
                    message: Message?,
                    error: NCError?,
                ) {
                    if (error != null) {
                        appendLog("Send failed: ${error.message} (${error.code})")
                    } else {
                        appendLog("Image sent successfully, messageId=${message?.messageId}")
                    }
                }

                override fun onCanceled(message: Message) {
                    appendLog("Send cancelled.")
                }
            },
        )
    }

    private fun copyUriToCache(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val file = File(cacheDir, "picked_image_${System.currentTimeMillis()}.jpg")
            file.outputStream().use { output -> inputStream.copyTo(output) }
            file.absolutePath
        } catch (e: Exception) {
            appendLog("Error copying image: ${e.message}")
            null
        }
    }
}
