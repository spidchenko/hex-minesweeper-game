package d.spidchenko.canvasgame;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private static final String TAG = "MainActivity.LOG_TAG";
    public static Point tapPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GameView gameView = new GameView(this); // создаём gameView

        LinearLayout gameLayout = (LinearLayout) findViewById(R.id.gameLayout); // находим gameLayout
        gameLayout.addView(gameView); // и добавляем в него gameView

        gameLayout.setOnTouchListener(this);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, "onTouch: " + (int) event.getX() + " " + (int) event.getY());
        tapPosition = new Point((int) event.getX(), (int) event.getY());
        return false;
    }
}