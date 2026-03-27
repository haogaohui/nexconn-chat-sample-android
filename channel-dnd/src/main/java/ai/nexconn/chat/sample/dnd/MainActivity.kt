package ai.nexconn.chat.sample.dnd

import ai.nexconn.chat.NCEngine
import ai.nexconn.chat.channel.BaseChannel
import ai.nexconn.chat.channel.ChannelType
import ai.nexconn.chat.channel.DirectChannel
import ai.nexconn.chat.channel.GroupChannel
import ai.nexconn.chat.channel.model.ChannelNoDisturbLevel
import ai.nexconn.chat.params.ChannelTypeNoDisturbLevelParams
import ai.nexconn.chat.params.ConnectParams
import ai.nexconn.chat.sample.module.LogOutputActivity
import ai.nexconn.chat.sample.module.SampleConfig
import android.os.Bundle

class MainActivity : LogOutputActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "No Disturb Setting"
        addActionButton("1. Connect") { onSetup() }
        addActionButton("2. Set Direct DND") { onSetDirectDnd() }
        addActionButton("3. Set Group DND") { onSetGroupDnd() }
        addActionButton("4. Set DND by Channel Type") { onSetTypeLevel() }
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

    private fun onSetDirectDnd() {
        val channel = DirectChannel(SampleConfig.directTargetId)
        channel.setNoDisturbLevel(ChannelNoDisturbLevel.MUTED) { error ->
            if (error != null) {
                appendLog("Set direct DND failed: ${error.message} (${error.code})")
                return@setNoDisturbLevel
            }
            appendLog("Direct channel DND set to MUTED.")
        }
    }

    private fun onSetGroupDnd() {
        val channel = GroupChannel(SampleConfig.groupId)
        channel.setNoDisturbLevel(ChannelNoDisturbLevel.MENTION) { error ->
            if (error != null) {
                appendLog("Set group DND failed: ${error.message} (${error.code})")
                return@setNoDisturbLevel
            }
            appendLog("Group channel DND set to MENTION.")
        }
    }

    private fun onSetTypeLevel() {
        val params =
            ChannelTypeNoDisturbLevelParams(
                channelType = ChannelType.GROUP,
                level = ChannelNoDisturbLevel.MENTION_ALL,
            )
        BaseChannel.setChannelTypeNoDisturbLevel(params) { error ->
            if (error != null) {
                appendLog("Set DND by type failed: ${error.message} (${error.code})")
                return@setChannelTypeNoDisturbLevel
            }
            appendLog("Group channel type DND set to MENTION_ALL.")
        }
    }
}
