package d.spidchenko.canvasgame;

import static java.lang.Math.cos;
import static java.lang.Math.multiplyExact;
import static java.lang.Math.round;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import java.util.ArrayList;

public class Hexagon {
    public static final int HEX_SIZE = 50;
    public static Point zeroCenter;
    private final ArrayList<Point> points = new ArrayList<>();
    public final Point centerPoint;
//    private final int circumradius;

    public int r;
    public int q;

    Hexagon(int q, int r) {
        this.r = r;
        this.q = q;

        int centerX = zeroCenter.x + (int) Math.round(HEX_SIZE * (sqrt(3) * q + sqrt(3) / 2 * r));
        int centerY = zeroCenter.y + (int) Math.round(HEX_SIZE * (3. / 2 * r));
        centerPoint = new Point(centerX, centerY);

        for (int i = 0; i <= 5; i++) {
            points.add(getNthHexCorner(centerPoint, i));
        }
    }

    public void drawCorners(Canvas canvas, Paint paint) {
        for (Point point : points) {
            canvas.drawPoint(point.x, point.y, paint);
        }
    }

    public void draw(Canvas canvas, Paint paint) {
        for (int i = 0; i < 5; i++) {
            Point currentPoint = points.get(i);
            Point nextPoint = points.get(i + 1);
            canvas.drawLine(currentPoint.x, currentPoint.y, nextPoint.x, nextPoint.y, paint);
        }
        canvas.drawLine(
                points.get(points.size() - 1).x,
                points.get(points.size() - 1).y,
                points.get(0).x,
                points.get(0).y,
                paint);

    }

    public Point getNthHexCorner(Point center, int i) {
        double angleDeg = 60 * i - 30;
        double angleRad = Math.PI / 180 * angleDeg;
        return new Point(
                (int) Math.round(center.x + HEX_SIZE * cos(angleRad)),
                (int) Math.round(center.y + HEX_SIZE * sin(angleRad))
        );
    }


}
