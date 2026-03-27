package ai.nexconn.chat.sample.directtext

import ai.nexconn.chat.NCEngine
import ai.nexconn.chat.channel.DirectChannel
import ai.nexconn.chat.error.NCError
import ai.nexconn.chat.handler.SendMessageHandler
import ai.nexconn.chat.message.Message
import ai.nexconn.chat.message.TextMessage
import ai.nexconn.chat.params.ConnectParams
import ai.nexconn.chat.params.SendMessageParams
import ai.nexconn.chat.sample.module.LogOutputActivity
import ai.nexconn.chat.sample.module.SampleConfig
import android.os.Bundle

class MainActivity : LogOutputActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Send Text Message"
        addActionButton("1. Connect") { onSetup() }
        addActionButton("2. Send Text Message") { onSendText() }
        appendLog("Please configure SampleConfig in CommonModule first.")
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

    private fun onSendText() {
        val channel = DirectChannel(SampleConfig.directTargetId)
        val params = SendMessageParams(TextMessage("Hello from SendTextMessage sample."))
        channel.sendMessage(
            params,
            object : SendMessageHandler {
                override fun onAttached(message: Message) {
                    appendLog("Message attached, messageId=${message.messageId}")
                }

                override fun onResult(
                    message: Message?,
                    error: NCError?,
                ) {
                    if (error != null) {
                        appendLog("Send failed: ${error.message} (${error.code})")
                    } else {
                        appendLog("Sent successfully, messageId=${message?.messageId}")
                    }
                }
            },
        )
    }
}
