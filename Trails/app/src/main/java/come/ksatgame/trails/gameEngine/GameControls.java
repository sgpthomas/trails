package come.ksatgame.trails.gameEngine;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.ksatgames.trails.R;

/**
 * Created by samthomas on 1/4/17.
 */

public class GameControls implements Renderable {

    Rect pause;
    Bitmap pauseBitmap;

    Rect play;
    Bitmap playBitmap;

    public boolean isPaused = false;

    public GameControls(Resources resources) {
        pause = new Rect(0, 0, (int)(GameView.getScreenWidth()*0.2), (int)(GameView.getScreenWidth()*0.2));
        pauseBitmap = BitmapFactory.decodeResource(resources, R.drawable.pause);

        play = new Rect((int)(GameView.getScreenWidth()*0.4), (int)(GameView.getScreenHeight()*0.7), (int)(GameView.getScreenWidth()*0.6),
                (int)(GameView.getScreenHeight()*0.7 + GameView.getScreenWidth()*0.2));
        playBitmap = BitmapFactory.decodeResource(resources, R.drawable.play);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        if (isPaused) {
            // Draw the background color
            canvas.drawColor(Color.argb(150, 255, 255, 255));
            //translucent white

            // buttons
            canvas.drawBitmap(playBitmap, null, play, paint);
//            canvas.drawBitmap(restartBitmap, null, restart, paint);
        } else {
            // pause button
            canvas.drawBitmap(pauseBitmap, null, pause, paint);
        }
    }

    public boolean isPressed(int x, int y) {
        if (isPaused) {
            if (play.contains(x, y)) {
                isPaused = false;
                return true;
            }
        } else {
            if (pause.contains(x, y)) {
                isPaused = true;
                return true;
            }
        }

        return false;
    }
}
