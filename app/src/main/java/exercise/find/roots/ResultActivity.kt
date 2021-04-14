package exercise.find.roots

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val originalNumber = intent.getLongExtra(MainActivity.EXTRA_NUM_ORIG, 0)
        val root1 = intent.getLongExtra(MainActivity.EXTRA_ROOT1, 0)
        val root2 = intent.getLongExtra(MainActivity.EXTRA_ROOT2, 0)
        val calcTime = intent.getLongExtra(MainActivity.EXTRA_CALC_TIME, 0)

        val resultMsg = "$originalNumber=$root1*$root2 \n Calculation time: $calcTime seconds."
        findViewById<TextView>(R.id.resultTextView).apply { text=resultMsg }
    }
}