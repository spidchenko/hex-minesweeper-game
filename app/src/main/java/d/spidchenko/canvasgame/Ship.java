package d.spidchenko.canvasgame;

import android.content.Context;

public class Ship extends SpaceBody {

    public Ship(Context context) {
        bitmapId = R.drawable.ship; // определяем начальные параметры
        size = 5;
        x=7;
        y=GameView.maxY - size - 1;
        speed = (float) 0.2;

        init(context); // инициализируем корабль
    }

    @Override
    public void update() { // перемещаем корабль в зависимости от нажатой кнопки
        if(MainActivity.isLeftPressed && x >= 0){
            x -= speed;
        }
        if(MainActivity.isRightPressed && x <= GameView.maxX - 5){
            x += speed;
        }
    }
}
