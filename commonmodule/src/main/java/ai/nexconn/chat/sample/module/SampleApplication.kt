package ai.nexconn.chat.sample.module

import ai.nexconn.chat.NCEngine
import ai.nexconn.chat.params.InitParams
import android.app.Application

open class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initNexconnChat()
    }

    private fun initNexconnChat() {
        val params = InitParams(applicationContext, SampleConfig.appKey)
        if (SampleConfig.naviServer.isNotBlank()) {
            params.naviServer = SampleConfig.naviServer
        }
        NCEngine.initialize(params)
    }
}
