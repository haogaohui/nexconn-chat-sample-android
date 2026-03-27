package ai.nexconn.chat.sample.channellist

import ai.nexconn.chat.NCEngine
import ai.nexconn.chat.channel.BaseChannel
import ai.nexconn.chat.channel.ChannelType
import ai.nexconn.chat.error.NCError
import ai.nexconn.chat.handler.OperationHandler
import ai.nexconn.chat.model.PageData
import ai.nexconn.chat.params.ChannelsQueryParams
import ai.nexconn.chat.params.ConnectParams
import ai.nexconn.chat.sample.module.LogOutputActivity
import ai.nexconn.chat.sample.module.SampleConfig
import android.os.Bundle

class MainActivity : LogOutputActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Channel List"
        addActionButton("1. Connect") { onSetup() }
        addActionButton("2. Fetch Direct + Group Channels") { onFetch() }
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

    private fun onFetch() {
        val params = ChannelsQueryParams(listOf(ChannelType.DIRECT, ChannelType.GROUP))
        val query = BaseChannel.createChannelsQuery(params)
        query.loadNextPage(
            object : OperationHandler<PageData<BaseChannel>> {
                override fun onResult(
                    result: PageData<BaseChannel>?,
                    error: NCError?,
                ) {
                    if (error != null) {
                        appendLog("Query failed: ${error.message} (${error.code})")
                        return
                    }
                    val list = result?.data ?: emptyList()
                    appendLog("Query succeeded, count=${list.size}")
                    list.forEach { channel ->
                        appendLog("  - ${channelTypeText(channel.channelType)} / ${channel.channelId}")
                    }
                }
            },
        )
    }

    private fun channelTypeText(type: ChannelType): String {
        return when (type) {
            ChannelType.DIRECT -> "direct"
            ChannelType.GROUP -> "group"
            else -> type.name.lowercase()
        }
    }
}
