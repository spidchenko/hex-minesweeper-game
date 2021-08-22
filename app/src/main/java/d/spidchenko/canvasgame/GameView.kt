package d.spidchenko.canvasgame

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.Log
import android.view.SurfaceView
import d.spidchenko.canvasgame.Hexagon
import android.view.SurfaceHolder
import d.spidchenko.canvasgame.GameView
import d.spidchenko.canvasgame.MainActivity
import java.util.ArrayList
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

class GameView(context: Context?) : SurfaceView(context), Runnable {
    private var firstTime = true
    private val gameRunning = true
    private val paddingSize = 100
    private var maxX = 0
    private var maxY = 0
    private var hexagons: ArrayList<Hexagon>? = null
    private var gameThread: Thread? = null
    private val paint: Paint = Paint().apply {
        isAntiAlias = true
        color = Color.YELLOW
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }
    private val thinPaint: Paint
    private var canvas: Canvas? = null
    private val surfaceHolder: SurfaceHolder = holder
    override fun run() {
        while (gameRunning) {
            draw()
        }
    }

    //    private void update() {
    //        if(!firstTime) {
    //            ship.update();
    //            for (Asteroid asteroid : asteroids) {
    //                asteroid.update();
    //            }
    //        }
    //    }
    private fun init() {
        maxX = surfaceHolder.surfaceFrame.width()
        maxY = surfaceHolder.surfaceFrame.height()
        val horizontalSpacing = (Hexagon.HEX_SIZE * sqrt(3.0)).roundToInt()
        val verticalSpacing = (1.5 * Hexagon.HEX_SIZE).roundToInt()
        Hexagon.zeroCenter = Point(maxX / 2, maxY / 2)
        val numRows = (maxX - 2 * paddingSize) / horizontalSpacing
        val numColumns = (maxY - 2 * paddingSize) / verticalSpacing
        Log.d(TAG, "init: numRows = $numRows")
        Log.d(TAG, "init: numColumns = $numColumns")
        fillWithHexagons()
    }

    private fun fillWithHexagons() {
        hexagons = ArrayList()
        for (q in -7..7) for (r in -6..6) {
            //check if visible
            val newXCord = Hexagon.zeroCenter!!.x + Hexagon.HEX_SIZE * (sqrt(3.0) * q + sqrt(3.0) / 2 * r)
            if (newXCord > paddingSize && newXCord < maxX - paddingSize) {
                hexagons!!.add(Hexagon(q, r))
            }
        }
    }

    private fun draw() {
        if (surfaceHolder.surface.isValid) {  //проверяем валидный ли surface
            if (firstTime) { // инициализация при первом запуске
                init()
                firstTime = false
                //                unitW = surfaceHolder.getSurfaceFrame().width()/maxX; // вычисляем число пикселей в юните
//                unitH = surfaceHolder.getSurfaceFrame().height()/maxY;
//                ship = new Ship(getContext()); // добавляем корабль
            }
            canvas = surfaceHolder.lockCanvas() // закрываем canvas
            //todo NullPointerException here! on sleep ...
            canvas!!.drawColor(Color.BLACK) // заполняем фон чёрным
            for (hexagon in hexagons!!) {
                hexagon.draw(canvas!!, thinPaint)
            }
            if (MainActivity.tapPosition != null) {
                val tap = MainActivity.tapPosition
                //                canvas.drawPoint(tap.x, tap.y, paint);
                var nearestHexIndex = 0
                var minDistance = Double.MAX_VALUE
                for (i in hexagons!!.indices) {
                    val distance = sqrt(
                            (hexagons!![i].centerPoint.x - tap!!.x).toDouble().pow(2.0) + (hexagons!![i].centerPoint.y - tap.y).toDouble().pow(2.0)
                    )
                    if (distance < minDistance) {
                        minDistance = distance
                        nearestHexIndex = i
                    }
                }
                if (minDistance < Hexagon.HEX_SIZE) {
                    hexagons!![nearestHexIndex].draw(canvas!!, paint)
                }
            }
            surfaceHolder.unlockCanvasAndPost(canvas) // открываем canvas
        }
    } //    private void control() { // пауза на 17 миллисекунд

    //        try {
    //            gameThread.sleep(17);
    //        } catch (InterruptedException e) {
    //            e.printStackTrace();
    //        }
    //    }
    //    private void checkCollision(){ // перебираем все астероиды и проверяем не касается ли один из них корабля
    //        for (Asteroid asteroid : asteroids) {
    //            if(asteroid.isCollision(ship.x, ship.y, ship.size)){
    //                // игрок проиграл
    //                gameRunning = false; // останавливаем игру
    //                // TODO добавить анимацию взрыва
    //            }
    //        }
    //    }
    //    private void checkIfNewAsteroid(){ // каждые 50 итераций добавляем новый астероид
    //        if(currentTime >= ASTEROID_INTERVAL){
    //            Asteroid asteroid = new Asteroid(getContext());
    //            asteroids.add(asteroid);
    //            currentTime = 0;
    //        }else{
    //            currentTime ++;
    //        }
    //    }
    companion object {
        private const val TAG = "GameView.LOG_TAG"
    }

    init {
        //инициализируем обьекты для рисования
        //        paint.color = Color.YELLOW
//        paint.style = Paint.Style.STROKE
//        paint.strokeWidth = 10f

        thinPaint = Paint()
        thinPaint.color = Color.RED
        thinPaint.style = Paint.Style.STROKE
        thinPaint.strokeWidth = 1f

        // инициализируем поток
        gameThread = Thread(this)
        gameThread!!.start()
    }
}