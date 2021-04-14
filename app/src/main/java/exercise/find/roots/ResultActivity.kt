package exercise.find.roots

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val originalNumber = intent.getLongExtra("original_number", 0)
        val root1 = intent.getLongExtra("root1", 0)
        val root2 = intent.getLongExtra("root2", 0)
        val calcTime = intent.getLongExtra("calculation_time", 0)

        val resultMsg = "$originalNumber=$root1*$root2 \n Calculation time: $calcTime seconds."
        findViewById<TextView>(R.id.resultTextView).apply { text=resultMsg }
    }
}