package come.ksatgame.trails.gameEngine;

import android.content.Context;
import android.content.Intent;
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
import com.ksatgames.trails.GameOverScreen;
import java.util.ArrayList;

/**
 * Created by samthomas on 10/2/16.
 */

public class GameView extends SurfaceView implements Runnable {

    Context context;

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

    float speedPerSecond = 300;
    int score = 0;
    // Progress of matrix
    int matrixPosition = 0;

    // window dimensions
    int screenWidth;
    int screenHeight;

    // matrix
    int[][] matrix;
    int[][] submatrix;
    int passed = 0; // counts number of blocks that have already fallen through
    int blockSize;
    // int dir = 1;   // 1 is upwards, 2 is down,
    // 3 is matrix stopped and ball moves upwards, 4 is matrix is stopped, ball moves downwards
    enum Direction { UP, DOWN, STOP_UP, STOP_DOWN; }
    Direction dir = Direction.UP;
    ArrayList<Pair> trail = new ArrayList<>(0);
    // stores coordinates between which trail is to be drawn

    public GameView(Context context, int numBlocks, float speedPerSecond) {

        // initialize our object
        super(context);
        this.context = context;

        // initialize window dimensions
        screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

        playerRect = getRect(screenWidth / 2 + playerRadius, screenHeight - playerHeight - playerRadius,
                2 * playerRadius, 2 * playerRadius);
        playerX = (screenWidth / 2);

        // Initialize ourHolder and paint objects
        ourHolder = getHolder();
        paint = new Paint();

        // set player speed
        this.speedPerSecond = speedPerSecond;

        // get matrix
        int cols = 5;
        blockSize = screenWidth / cols;

        matrix = Generator.getInstance().genMatrix((screenHeight/blockSize)*numBlocks, cols, 2);

        submatrix=new int[screenHeight/blockSize+3][cols];
        for(int i=0; i<submatrix.length; i++) {
            for(int j=0; j<cols; j++) {
                submatrix[i][j] = matrix[matrix.length-(screenHeight/blockSize)-4+i][j];
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
            // We can then use the result to time animations and more.
            long timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame > 0) {
                fps = 1000 / timeThisFrame;
            }
        }
    }

    // Everything that needs to be updated goes in here
    // We will also do other things like collision detection.
    public void update() {
        // when you reach the end of the maze, change direction to 3 or 4
        if (passed == matrix.length-screenHeight/blockSize-5) {
            if (dir == Direction.UP) {
                dir = Direction.STOP_UP;
            }

            if (dir == Direction.DOWN) {
                dir = Direction.STOP_DOWN;
                matrixPosition = screenHeight-(playerHeight+playerRadius)*2;
            }
        }

        // get in a fresh maze row while going up
        if (matrixPosition > blockSize && dir == Direction.UP) {
            matrixPosition = 0;
            passed++;
            score++;
            for (int i = 0; i < submatrix.length; i++) {
                for (int j = 0; j < submatrix[i].length; j++) {
                    //there's a two row padding for smooth transitions
                    submatrix[i][j] = matrix[matrix.length-(screenHeight/blockSize)-4-passed+i][j];
                }
            }
        }

        // getting in a fresh row while going down
        if (matrixPosition < (-blockSize) && dir == Direction.DOWN) {
            matrixPosition = 0;
            passed++;
            score++;
            for (int i = 0; i < submatrix.length; i++) {
                for (int j=0; j<submatrix[i].length; j++) {
                    submatrix[i][j]=matrix[passed+i+1][j];
                }
            }
        }
        // bouncing off the top of the screen and starting downwards
        if (matrixPosition >= screenHeight-playerHeight-playerRadius-2 && dir == Direction.STOP_UP) {
            dir = Direction.STOP_DOWN;
            passed = 0;
            score += 100;
        }

        // bouncing off the bottom of the screen
        if(dir == Direction.STOP_DOWN && playerRect.centerY() >= screenHeight-(2*playerRadius)-(2*speedPerSecond/fps)) {
            // the multiplication by 2 is logically arbitrary- it just makes a good bounce while testing
            dir = Direction.STOP_UP;
            passed = 0;
            score += 100;
        }

        //r esume matrix motion after ball has bounced off bottom and reached a certain height
        if(dir == Direction.STOP_UP && passed == 0 && playerRect.centerY() <= screenHeight-playerHeight-playerRadius+2*matrixPosition) {
            dir = Direction.UP;
        }

        // start moving the matrix downwards again after ball reaches a certain height
        if(passed < 1 && matrixPosition<=screenHeight-(playerHeight+playerRadius)*2 && dir == Direction.STOP_DOWN) {
            dir = Direction.DOWN;
            matrixPosition = 0;
        }

        // smooth movement
        if (fps != 0) {
            if (dir == Direction.DOWN || dir == Direction.STOP_DOWN)
                matrixPosition -= speedPerSecond / fps;
            else
                matrixPosition += speedPerSecond / fps;
            if (playerDeltaX != 0) {
                if (Math.abs(playerX - playerNewX) < (2 * playerRadius)) {
                    playerDeltaX = 0;
                    playerNewX = 0;
                }
                else {
                    playerX += playerDeltaX / (playerSpeed);
                }
            }
        }

        // collision handling
        if (playerRect.centerY() > (playerRadius+3)) {
            // so that the player doesn't collide with an unseen rectangle at the top of the screen
            for (int y = 0; y < submatrix.length; y++) {
                for (int x = 0; x < submatrix[y].length; x++) {
                    if (submatrix[y][x] == 1) {
                        Rect subRect = getRect(x * blockSize, (y - 2) * blockSize +
                                ((dir == Direction.UP || dir == Direction.DOWN) ? matrixPosition : 0), blockSize, blockSize);
                        if (Rect.intersects(playerRect, subRect)) {
                            endGame();
                        }
                    }
                }
            }
        }

        // check intersection with trail
        if (!(dir == Direction.STOP_DOWN && matrixPosition >= screenHeight-(playerHeight+playerRadius)*2)) {
            boolean flag = false; // has intersection occured?
            // consider optimization by deciding which variables to store as local and which to just call playerRect for each time
            outer: for (int i = 0; i < trail.size() - 3; i++) {
                int y1 = trail.get(i).y;
                int y2 = trail.get(i + 1).y;
                // if the segment is in the same vertical region as the player
                if (y2 < y1 && (playerRect.contains(playerRect.centerX(), y1)
                        || playerRect.contains(playerRect.centerX(), y1)
                        || (y1 > playerRect.centerY() && y2 < playerRect.centerY()))) {
                    int x1 = trail.get(i).x;
                    int x2 = trail.get(i + 1).x;
                    if (x1 == x2) {
                        flag=playerRect.contains(x1, playerRect.centerY());
                    } else {
                        float slope = ((float) (y2 - y1)) / (x2 - x1);
                        for (int j = y2; j < y1; j++) {
                            int x = x1 + (int) ((j - y2)/slope);
                            if (playerRect.contains(x, j)) {
                                // might need to test points around this region for slower systems
                                flag = true;
                                break outer;
                            }
                        }
                    }
                }
            }

            if (flag) {
                endGame();
            }
        }

        if (playerX < playerRadius) {
            playerX = playerRadius;
        } else if (playerX > screenWidth - playerRadius) {
            playerX = screenWidth - playerRadius;
        }

        if (trail.size() > 0) {
            for (Pair p : trail) {
                if (dir == Direction.UP)
                    p.shiftUp((int) (speedPerSecond / fps));
                else if (dir == Direction.DOWN)
                    p.shiftDown((int) (speedPerSecond / fps));
            }
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
                        canvas.drawRect(getRect(x * blockSize, (y - 2) * blockSize + ((dir == Direction.UP || dir == Direction.DOWN)
                                ? matrixPosition : 0), blockSize, blockSize), paint);
                    }
                }
            }

            // player draw logic
            playerRect.set(getRect((int) playerX - playerRadius,
                    (dir == Direction.DOWN ? playerHeight+playerRadius : screenHeight-playerHeight-playerRadius-
                            ((dir == Direction.STOP_UP || dir == Direction.STOP_DOWN) ? matrixPosition : 0)),
                    2*playerRadius, 2*playerRadius));
            // adding current player location to list of trail coordinates
            trail.add(new Pair(playerRect.centerX(), playerRect.centerY()));

            // now to draw trail
            paint.setAntiAlias(true);
            paint.setStrokeWidth(playerRadius*2);
            boolean collisionValid = !(dir == Direction.STOP_DOWN && matrixPosition >= screenHeight-(playerHeight+playerRadius)*2);
            if (!collisionValid) paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeJoin(Paint.Join.ROUND);
            // this could be optimized later by changing condition so that iteration stops at first out of screen
            // or one could try and use Path class
            for (int i = 0; i < trail.size()-1; i++) {
                Pair start = trail.get(i);
                Pair stop = trail.get(i+1);
                if (start.inScreen(screenHeight, screenWidth) || stop.inScreen(screenHeight, screenWidth)) {
                    if(start.y > stop.y) {
                        if (collisionValid) paint.setColor(Color.RED);
                        canvas.drawLine(start.x, start.y + 4, stop.x, stop.y - 4, paint);
                    } else {
                        if (collisionValid) paint.setColor(Color.GREEN);
                        canvas.drawLine(start.x, start.y - 4, stop.x, stop.y + 4, paint);
                    }
                }
            }
//            float t[]=new float[trail.size()];
//            for(int i=0; i<trail.size();i++){
//                t[i]=trail.get(i).y;
//            }
//            canvas.drawLines(t,0,2, paint);

            // drawing player
            paint.setColor(Color.BLUE);
            paint.setStrokeWidth(10);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRoundRect(new RectF(playerRect), playerRadius/2, playerRadius/2, paint);

            paint.setColor(Color.argb(255, 250, 100, 200));   //something that stands out against white and black
            // print score
            paint.setTextSize(70);
            canvas.drawText("Score:"+score, screenWidth-400-(score%100), 100, paint);

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

    // ends game and goes to the GameOver Screen
    public void endGame() {
        Intent intent = new Intent(this.context, GameOverScreen.class);
        this.context.startActivity(intent);
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