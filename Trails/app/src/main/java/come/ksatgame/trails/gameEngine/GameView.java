package come.ksatgame.trails.gameEngine;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by samthomas on 10/2/16.
 */

public class GameView extends SurfaceView implements Runnable {

    // This is our thread
    Thread gameThread = null;

    // This is new. We need a SurfaceHolder
    // When we use Paint and Canvas in a thread
    // We will see it in action in the draw method soon.
    SurfaceHolder ourHolder;

    // A boolean which we will set and unset
    // when the game is running- or not.
    volatile boolean playing;

    // A Canvas and a Paint object
    Canvas canvas;
    Paint paint;

    // This variable tracks the game frame rate
    long fps;

    // Bob starts off not moving
    Rect playerRect;
    float playerX;
    float playerNewX;
    float playerDeltaX = 0;
    final int playerSpeed = 5;
    final int playerRadius = 25;
    final int playerHeight = 250;
    float speedPerSecond = 400;

    // Progress of matrix
    int matrixPosition = 0;

    // window dimensions
    int screenWidth;
    int screenHeight;

    // matrix
    int[][] matrix;
    int[][] submatrix;
    int passed=0; //counts number of blocks that have already fallen through
    int blockSize;
    int dir=1;   // 1 is upwards, 2 is down,
    // 3 is matrix stopped and ball moves upwards, 4 is matrix is stopped, ball moves downwards
    public GameView(Context context) {
        // initialize our object
        super(context);

        // initialize window dimensions
        screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

        playerRect = getRect((int)screenWidth/2 + playerRadius, screenHeight-playerHeight-playerRadius, 2*playerRadius, 2*playerRadius);
        playerX = (screenWidth / 2);

        // Initialize ourHolder and paint objects
        ourHolder = getHolder();
        paint = new Paint();

        // get matrix
        int cols = 7;
        blockSize = screenWidth / cols;
        //ten screen's worth falling blocks
        matrix = Generator.getInstance().genMatrix((screenHeight/blockSize)*10, cols, 2);
        submatrix=new int[screenHeight/blockSize+3][cols];
        for(int i=0; i<submatrix.length; i++)
        {
            for(int j=0; j<cols; j++)
            {
                submatrix[i][j]=matrix[matrix.length-(screenHeight/blockSize)-4+i][j];
            }
        }
        // Set our boolean to true - game on!
        playing = true;

    }

    @Override
    public void run() {
        while (playing) {

            // Capture the current time in milliseconds in startFrameTime
            long startFrameTime = System.currentTimeMillis();

            // Update the frame
            update();

            // Draw the frame
            draw();

            // Calculate the fps this frame
            // We can then use the result to
            // time animations and more.
            long timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame > 0) {
                fps = 1000 / timeThisFrame;
            }
        }
    }

    // Everything that needs to be updated goes in here
    // In later projects we will have dozens (arrays) of objects.
    // We will also do other things like collision detection.
    public void update() {

        if (passed == matrix.length-screenHeight/blockSize-5) {
            dir = 3;
            passed = 0;
        }

        if (matrixPosition>blockSize && dir == 1)
        {
            matrixPosition=0;
            passed++;
            for (int i = 0; i < submatrix.length; i++)
            {
                for(int j = 0; j < submatrix[i].length; j++)
                {
                    //there's a two row padding for smooth transtions
                    submatrix[i][j]=matrix[matrix.length-(screenHeight/blockSize)-4-passed+i][j];
                }
            }
        }

        if (matrixPosition<(-blockSize) && dir == 2)
        {
            matrixPosition=0;
            passed++;
            for(int i=0; i<submatrix.length; i++)
            {
                for(int j=0; j<submatrix[i].length; j++)
                {
                    //there's a three row padding for smooth transtions
                    submatrix[i][j]=matrix[passed+i+1][j];
                }
            }
        }

        if(matrixPosition>=screenHeight-playerHeight-playerRadius-2 && dir == 3)
        {
            dir = 4;
        }

        if(matrixPosition<=1 && dir == 4)
        {
            dir = 2;
        }

        // player smooth movement
        if (fps != 0) {
            if (dir==4 || dir==2)
                matrixPosition -= speedPerSecond / fps;
            else
                matrixPosition += speedPerSecond / fps;
            if (playerDeltaX != 0) {
                if (Math.abs(playerX - playerNewX) < 15) {
                    playerDeltaX = 0;
                    playerNewX = 0;
                } else {
                    playerX += playerDeltaX / (playerSpeed);
                }
            }
        }

        for (int y = 0; y < submatrix.length; y++) {
            for (int x = 0; x < submatrix[y].length; x++) {
                if (submatrix[y][x] == 1) {
                    Rect subRect = getRect(x * blockSize, (y - 1) * blockSize + ((dir == 1 || dir == 2) ? matrixPosition : 0), blockSize, blockSize);
                    if (Rect.intersects(playerRect, subRect)) {
                        throw new RuntimeException("Crash");
                    }
                }
            }
        }
        if(matrixPosition<=screenHeight-(playerHeight+playerRadius)*2 && dir==4) {
            dir = 2;
            matrixPosition=0;
        }
    }

    // Draw the newly updated scene
    public void draw() {

        // Make sure our drawing surface is valid or we crash
        if (ourHolder.getSurface().isValid()) {
            // Lock the canvas ready to draw
            canvas = ourHolder.lockCanvas();

            // Draw the background color
            canvas.drawColor(Color.argb(255,  255, 255, 255)); // white

            // set color of rectangles
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(10);
            paint.setStyle(Paint.Style.FILL);

            // matrix logic
            for (int y = 0; y < submatrix.length; y++) {
                for (int x = 0; x < submatrix[y].length; x++) {
                    if (submatrix[y][x] == 1) {
                        canvas.drawRect(getRect(x * blockSize, (y - 1) * blockSize + ((dir == 1 || dir == 2) ? matrixPosition : 0), blockSize, blockSize), paint);
                    }
                }
            }

            // player draw logic
            paint.setColor(Color.BLUE);

        playerRect.set(getRect((int)playerX + playerRadius,
                    (dir == 2 ? playerHeight+playerRadius : screenHeight-playerHeight-playerRadius-((dir == 3 || dir == 4) ? matrixPosition : 0)),
                    2*playerRadius, 2*playerRadius));

            canvas.drawRoundRect(new RectF(playerRect), playerRadius/2, playerRadius/2, paint);
            paint.setColor(Color.RED);
            canvas.drawLine(0, matrixPosition, screenWidth, matrixPosition, paint);

            // Draw everything to the screen
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    private Rect getRect(int x, int y, int width, int height) {
        return new Rect(x, y, x + width, y + height);
    }

    // If SimpleGameEngine Activity is paused/stopped
    // shutdown our thread.
    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }

    }

    // If SimpleGameEngine Activity is started then
    // start our thread.
    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    // The SurfaceView class implements onTouchListener
    // So we can override this method and detect screen touches.
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

            // Player has touched the screen
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:

                playerNewX = motionEvent.getX();
                playerDeltaX = playerNewX - playerX;

                break;
        }
        return true;
    }
}