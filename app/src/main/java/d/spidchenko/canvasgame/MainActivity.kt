package d.spidchenko.canvasgame

import android.app.Activity
import android.graphics.Point
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.LinearLayout

class MainActivity : Activity(), View.OnTouchListener, View.OnClickListener,
    View.OnLongClickListener {

    lateinit var gameEngine: GameEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)
        gameEngine = GameEngine(this)
        val gameLayout: LinearLayout = findViewById(R.id.gameLayout)
        gameLayout.addView(gameEngine)
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

    override fun onResume() {
        super.onResume()
        gameEngine.resume()
    }

    override fun onPause() {
        super.onPause()
        gameEngine.pause()
    }

    companion object {
        private const val TAG = "MainActivity.LOG_TAG"

        // TODO need better logic for handling input events
        var lastClickCoordinates: Point? = null
        var clickDuration = ClickDuration.SHORT
    }
}