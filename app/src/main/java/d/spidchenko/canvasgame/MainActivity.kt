package d.spidchenko.canvasgame

import android.graphics.Point
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), View.OnTouchListener, View.OnClickListener,
    View.OnLongClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val gameView = GameView(this)
        val gameLayout: LinearLayout = findViewById(R.id.gameLayout)
        gameLayout.addView(gameView)
        gameLayout.setOnTouchListener(this)
        gameLayout.setOnClickListener(this)
        gameLayout.setOnLongClickListener(this)
    }

    override fun onLongClick(p0: View?): Boolean {
        clickDuration = ClickDuration.LONG
        return true
    }

    override fun onClick(p0: View?) {
        clickDuration = ClickDuration.SHORT
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            lastClickCoordinates = Point(event.x.toInt(), event.y.toInt())
        }
        return false
    }

    companion object {
        private const val TAG = "MainActivity.LOG_TAG"
        // TODO need better logic for handling input events
        var lastClickCoordinates: Point? = null
        var clickDuration = ClickDuration.SHORT
    }
}