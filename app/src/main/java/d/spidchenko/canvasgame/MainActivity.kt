package d.spidchenko.canvasgame

import android.graphics.Point
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), View.OnTouchListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val gameView = GameView(this)
        val gameLayout: LinearLayout = findViewById(R.id.gameLayout)
        gameLayout.addView(gameView)
        gameLayout.setOnTouchListener(this)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        tapPosition = Point(event.x.toInt(), event.y.toInt())
        return false
    }

    companion object {
        private const val TAG = "MainActivity.LOG_TAG"
        var tapPosition: Point? = null
    }
}