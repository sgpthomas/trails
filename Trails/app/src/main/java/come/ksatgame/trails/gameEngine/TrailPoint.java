package come.ksatgame.trails.gameEngine;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.Random;

/**
 * class represents ordered TrailPoint- to use in keeping track of trail coordinates
 * x controls widthwise position.
 * y controls heightwise position
 * Created by aditi on 12/18/16.
 */
public class TrailPoint {

    public int x;
    public int y;
    public int radius;

    private int color;
    private int counter = 21;
    private int radiusDirection = 0;

    public TrailPoint(int x, int y)  {
        this.x = x;
        this.y = y;
        Random rand = new Random();
        float[] col = { rand.nextInt(255), 255, 255 };
        color = Color.HSVToColor(col);
    }

    public void shiftDown(int offset)  {
        y = y - offset;
    }

    /**
     * shifts the point down by offset while screen scrolling up
     * @param offset
     */
    public void shiftUp(int offset)  {
        y = y + offset;
    }

    public void draw(Canvas canvas, Paint paint, boolean collisionValid) {
        if (inScreen()) {
            if (collisionValid) {
                paint.setColor(color);
            }
            if (counter > 20) {
                counter = 0;
                radiusDirection = (Math.random() < 0.5)  ? -1 : 1;
                radius = (int) (Math.random() * (GameView.playerRadius / 2));
            }
            radius += radiusDirection;
            canvas.drawCircle(this.x, this.y, radius, paint);
            counter++;
        }
    }

    /**
     * @return  whether this point is in screen
     */
    private boolean inScreen() {
        return !(x < 0 || y < 0 || x > GameView.getScreenWidth() || y > GameView.getScreenHeight());
    }

}
