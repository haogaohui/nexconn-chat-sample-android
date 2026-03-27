package ai.nexconn.chat.sample.module

import android.os.Bundle
import android.util.TypedValue
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

open class LogOutputActivity : AppCompatActivity() {
    private lateinit var buttonContainer: LinearLayout
    private lateinit var logTextView: TextView
    private lateinit var logScrollView: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_output)
        buttonContainer = findViewById(R.id.buttonContainer)
        logTextView = findViewById(R.id.logOutput)
        logScrollView = findViewById(R.id.logScrollView)
    }

    protected fun addActionButton(
        title: String,
        onClick: () -> Unit,
    ) {
        val button =
            Button(this).apply {
                text = title
                isAllCaps = false
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                setOnClickListener { onClick() }
            }
        val params =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                resources.getDimensionPixelSize(R.dimen.action_button_height),
            ).apply {
                bottomMargin = resources.getDimensionPixelSize(R.dimen.action_button_margin)
            }
        buttonContainer.addView(button, params)
    }

    protected fun appendLog(message: String) {
        val fmt = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val line = "[${fmt.format(Date())}] $message\n"
        runOnUiThread {
            logTextView.append(line)
            logScrollView.post { logScrollView.fullScroll(ScrollView.FOCUS_DOWN) }
        }
    }
}
