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

    // This is used to help calculate the fps
    private long timeThisFrame;

    // Declare an object of type Bitmap
//    Bitmap bitmapBob;

    // Bob starts off not moving
    float playerX;
    float playerDeltaX;
    final int playerRadius = 25;
    final int playerHeight = 250;

    // He can walk at 150 pixels per second
    float speedPerSecond = 300;

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

    public GameView(Context context) {
        // initialize our object
        super(context);

        // initialize window dimensions
        screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

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
            draw(submatrix);

            // Calculate the fps this frame
            // We can then use the result to
            // time animations and more.
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame > 0) {
                fps = 1000 / timeThisFrame;
            }
        }
    }

    // Everything that needs to be updated goes in here
    // In later projects we will have dozens (arrays) of objects.
    // We will also do other things like collision detection.
    public void update() {

        // update matrix position
        if (fps != 0) {
            matrixPosition += speedPerSecond / fps;

            if (playerDeltaX > 0) {
//                playerDeltaX +=
            }
        }

        if (matrixPosition>blockSize)
        {
            matrixPosition=0;
            passed++;
            for(int i=0; i<submatrix.length; i++)
            {
                for(int j=0; j<submatrix[i].length; j++)
                {
                    //there's a two row padding for smooth transtions
                    submatrix[i][j]=matrix[matrix.length-(screenHeight/blockSize)-4-passed+i][j];
                }
            }
        }
    }

    // Draw the newly updated scene
    public void draw(int[][] matrix) {

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
            for (int y = 0; y < matrix.length; y++) {
                for (int x = 0; x < matrix[y].length; x++) {
                    if (matrix[y][x] == 1) {
                        canvas.drawRect(getRect(x*blockSize, (y-1)*blockSize+matrixPosition , blockSize, blockSize), paint);
                    }
                }
            }
            // player draw logic
            paint.setColor(Color.BLUE);

            canvas.drawRoundRect(new RectF(getRect((int)playerX+playerRadius, screenHeight-playerHeight-playerRadius,
                    2*playerRadius, 2*playerRadius)), playerRadius/2, playerRadius/2, paint);

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

                playerDeltaX = motionEvent.getX() - playerX;
                Log.d("asdf", Float.toString(playerDeltaX));

                break;

            case MotionEvent.ACTION_MOVE:

                playerX = motionEvent.getX();

            // Player has removed finger from screen
            case MotionEvent.ACTION_UP:

                break;
        }
        return true;
    }
}