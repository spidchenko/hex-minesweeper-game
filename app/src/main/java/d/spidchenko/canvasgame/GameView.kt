package d.spidchenko.canvasgame;

import static java.lang.Math.sqrt;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class GameView extends SurfaceView implements Runnable {
    private static final String TAG = "GameView.LOG_TAG";

    private boolean firstTime = true;
    private boolean gameRunning = true;
    private int paddingSize = 100;
    private int maxX;
    private int maxY;

    private ArrayList<Hexagon> hexagons;
    private Thread gameThread = null;
    private Paint paint;
    private Paint thinPaint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    public GameView(Context context) {
        super(context);
        //инициализируем обьекты для рисования
        surfaceHolder = getHolder();
        paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);

        thinPaint = new Paint();
        thinPaint.setColor(Color.RED);
        thinPaint.setStyle(Paint.Style.STROKE);
        thinPaint.setStrokeWidth(1);

        // инициализируем поток
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        while (gameRunning) {
            draw();
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

    private void init() {

        maxX = surfaceHolder.getSurfaceFrame().width();
        maxY = surfaceHolder.getSurfaceFrame().height();


        int horizontalSpacing = (int) Math.round(Hexagon.HEX_SIZE * sqrt(3));
        int verticalSpacing = (int) Math.round(1.5 * Hexagon.HEX_SIZE);

        Hexagon.zeroCenter = new Point(maxX / 2, maxY / 2);

        int numRows = (maxX - 2 * paddingSize) / horizontalSpacing;
        int numColumns = (maxY - 2 * paddingSize) / verticalSpacing;

        Log.d(TAG, "init: numRows = " + numRows);
        Log.d(TAG, "init: numColumns = " + numColumns);

        fillWithHexagons();


    }

    private void fillWithHexagons() {
        hexagons = new ArrayList<>();
        for (int q = -7; q <= 7; q++)
            for (int r = -6; r <= 6; r++) {
                //check if visible
                double newXCord = Hexagon.zeroCenter.x + Hexagon.HEX_SIZE * (sqrt(3) * q + sqrt(3) / 2 * r);
                if ((newXCord > paddingSize) &&(newXCord < maxX - paddingSize)){
                    hexagons.add(new Hexagon(q, r));
                }


            }
    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {  //проверяем валидный ли surface

            if (firstTime) { // инициализация при первом запуске
                init();
                firstTime = false;
//                unitW = surfaceHolder.getSurfaceFrame().width()/maxX; // вычисляем число пикселей в юните
//                unitH = surfaceHolder.getSurfaceFrame().height()/maxY;
//                ship = new Ship(getContext()); // добавляем корабль
            }

            canvas = surfaceHolder.lockCanvas(); // закрываем canvas
            //todo NullPointerException here! on sleep ...
            canvas.drawColor(Color.BLACK); // заполняем фон чёрным

            for (Hexagon hexagon : hexagons) {
                hexagon.draw(canvas, thinPaint);
            }

            if (MainActivity.tapPosition != null) {
                Point tap = MainActivity.tapPosition;
//                canvas.drawPoint(tap.x, tap.y, paint);

                int nearestHexIndex = 0;
                double minDistance = Double.MAX_VALUE;
                for (int i = 0; i < hexagons.size(); i++) {
                    double distance = sqrt(
                            Math.pow((hexagons.get(i).centerPoint.x - tap.x), 2) + Math.pow((hexagons.get(i).centerPoint.y - tap.y), 2)
                    );
                    if (distance < minDistance) {
                        minDistance = distance;
                        nearestHexIndex = i;
                    }
                }

                if (minDistance < Hexagon.HEX_SIZE) {
                    hexagons.get(nearestHexIndex).draw(canvas, paint);
                }

            }

            surfaceHolder.unlockCanvasAndPost(canvas); // открываем canvas
        }
    }


//    private void control() { // пауза на 17 миллисекунд
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

}
