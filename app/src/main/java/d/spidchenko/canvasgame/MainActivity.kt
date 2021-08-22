package d.spidchenko.canvasgame

import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), View.OnTouchListener {
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val gameView = GameView(this) // создаём gameView
        val gameLayout: LinearLayout = findViewById<LinearLayout>(R.id.gameLayout) // находим gameLayout
        gameLayout.addView(gameView) // и добавляем в него gameView
        gameLayout.setOnTouchListener(this)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        Log.d(TAG, "onTouch: " + event.x.toInt() + " " + event.y.toInt())
        tapPosition = Point(event.x.toInt(), event.y.toInt())
        return false
    }

    companion object {
        private const val TAG = "MainActivity.LOG_TAG"
        var tapPosition: Point? = null
    }
}