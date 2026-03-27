package ai.nexconn.chat.sample.members

import ai.nexconn.chat.NCEngine
import ai.nexconn.chat.channel.GroupChannel
import ai.nexconn.chat.params.ConnectParams
import ai.nexconn.chat.params.GroupMembersByRoleQueryParams
import ai.nexconn.chat.sample.module.LogOutputActivity
import ai.nexconn.chat.sample.module.SampleConfig
import android.os.Bundle

class MainActivity : LogOutputActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Group Members"
        addActionButton("1. Connect") { onSetup() }
        addActionButton("2. Fetch Group Members") { onFetchMembers() }
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

    private fun onFetchMembers() {
        val params = GroupMembersByRoleQueryParams(groupId = SampleConfig.groupId)
        val query = GroupChannel.createGroupMembersByRoleQuery(params)
        query.loadNextPage { result, error ->
            if (error != null) {
                appendLog("Fetch failed: ${error.message} (${error.code})")
                return@loadNextPage
            }
            val list = result?.data ?: emptyList()
            appendLog("Fetch succeeded, count=${list.size}")
            list.forEach { member ->
                appendLog("  - userId=${member.userId}, role=${member.role}")
            }
        }
    }
}
