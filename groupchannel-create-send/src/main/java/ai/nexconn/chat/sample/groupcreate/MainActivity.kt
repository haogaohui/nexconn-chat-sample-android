package ai.nexconn.chat.sample.groupcreate

import ai.nexconn.chat.NCEngine
import ai.nexconn.chat.channel.GroupChannel
import ai.nexconn.chat.error.NCError
import ai.nexconn.chat.handler.SendMessageHandler
import ai.nexconn.chat.message.Message
import ai.nexconn.chat.message.TextMessage
import ai.nexconn.chat.params.ConnectParams
import ai.nexconn.chat.params.CreateGroupParams
import ai.nexconn.chat.params.SendMessageParams
import ai.nexconn.chat.sample.module.LogOutputActivity
import ai.nexconn.chat.sample.module.SampleConfig
import android.os.Bundle

class MainActivity : LogOutputActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Create Group & Send"
        addActionButton("1. Connect") { onSetup() }
        addActionButton("2. Create Group Channel") { onCreateGroup() }
        addActionButton("3. Send Group Message") { onSendGroupMessage() }
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

    private fun onCreateGroup() {
        val params =
            CreateGroupParams(
                groupId = SampleConfig.groupId,
                groupName = SampleConfig.groupName,
                inviteeUserIds = SampleConfig.memberUserIds,
            )
        GroupChannel.createGroup(params) { resultCode, errorKeys, error ->
            if (error != null) {
                appendLog("Create group failed: ${error.message} (${error.code}), errorKeys=$errorKeys")
                return@createGroup
            }
            appendLog("Group created successfully, resultCode=$resultCode")
        }
    }

    private fun onSendGroupMessage() {
        val channel = GroupChannel(SampleConfig.groupId)
        val params = SendMessageParams(TextMessage("Hello from CreateGroupAndSend sample."))
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
                        appendLog("Group message sent, messageId=${message?.messageId}")
                    }
                }
            },
        )
    }
}
