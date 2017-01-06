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
import com.ksatgames.trails.LevelClearedScreen;
import com.ksatgames.trails.R;

import java.util.ArrayList;
import java.util.Random;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
    volatile boolean gamePaused;

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
    public final static int playerRadius = 25;
    final int playerHeight = 250;

    float speedPerSecond = 100;
    int score = 0;
    // Progress of matrix
    int matrixPosition = 0;

    // window dimensions
    static int screenWidth = 0;
    static int screenHeight = 0;

    // matrix
    int[][] matrix;
    int[][] submatrix;
    int passed = 0; // counts number of blocks that have already fallen through
    int blockSize;
    // int dir = 1;   // 1 is upwards, 2 is down,
    // 3 is matrix stopped and ball moves upwards, 4 is matrix is stopped, ball moves downwards
    enum Direction { UP, DOWN, STOP_UP, STOP_DOWN }
    Direction dir = Direction.UP;
    ArrayList<TrailPoint> trail = new ArrayList<>(0); // stores coordinates between which trail is to be drawn

    GameControls controls;

    public static boolean collisionValid; // do we want collisions with trail to be possible at this point?

    boolean gameWon;
    boolean endless;

//    Rect restart=new Rect((int)(screenWidth*0.5),(int)(screenHeight*0.7), (int)(screenWidth*0.6), (int)(screenHeight*0.8));
//    Bitmap restartBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.restart);

    int totScore;
    int level;
    int levelSelection;
    //0 if quick play, start level if game started through level selection

    public GameView(Context context, int numBlocks, float speedPerSecond, int level, int totScore, boolean isEndless, int levelSelection) {

        // initialize our object
        super(context);
        this.context = context;

        // initialize window dimensions
        screenHeight = getScreenHeight();
        screenWidth = getScreenWidth();

        playerRect = getRect(screenWidth / 2 + playerRadius, screenHeight - playerHeight - playerRadius,
                2 * playerRadius, 2 * playerRadius);
        playerX = (screenWidth / 2);

        // Initialize ourHolder and paint objects
        ourHolder = getHolder();
        paint = new Paint();

        // set player speed
        this.speedPerSecond = speedPerSecond;

        int cols=4+ level/3;
        // get matrix
        blockSize = screenWidth / cols;

        matrix = Generator.getInstance().genMatrix((screenHeight/blockSize)*numBlocks, cols, (cols/2)-1, screenHeight/(blockSize));

        submatrix=new int[screenHeight/blockSize+3][cols];
        for(int i=0; i<submatrix.length; i++) {
            for(int j=0; j<cols; j++) {
                submatrix[i][j] = matrix[matrix.length-(screenHeight/blockSize)-4+i][j];
            }
        }

        controls = new GameControls(this.getResources());

        this.level=level;
        this.totScore=totScore;
        this.levelSelection=levelSelection;

        // Set our boolean to true - game on!
        gameWon = false;
        endless = isEndless;

        playing = true;
        gamePaused = false;
    }

    public static int getScreenHeight() {
        if (screenHeight == 0) {
            screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        }
        return screenHeight;
    }

    public static int getScreenWidth() {
        if (screenWidth == 0) {
            screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        }
        return screenWidth;
    }

    @Override
    public void run() {
        while (playing) {

            // Capture the current time in milliseconds in startFrameTime
            long startFrameTime = System.currentTimeMillis();

            // Update the frame
            update();

            //check condition to prevent canvas null pointer
            if (playing) {
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
        else if (matrixPosition < (-blockSize) && dir == Direction.DOWN) {
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
        else if (dir == Direction.STOP_DOWN && playerRect.centerY() >= screenHeight-(2*playerRadius)-(2*speedPerSecond/fps)) {
            // the multiplication by 2 is logically arbitrary- it just makes a good bounce while testing
            if(!endless) {
                gameWon=true;
                endGame();
            }
            dir = Direction.STOP_UP;
            passed = 0;
            score += 100;
        }
        // resume matrix motion after ball has bounced off bottom and reached a certain height
        else if (dir == Direction.STOP_UP && passed == 0 && playerRect.centerY() <= screenHeight-playerHeight-playerRadius+2*matrixPosition) {
            dir = Direction.UP;
        }
        // start moving the matrix downwards again after ball reaches a certain height
        else if (passed < 1 && matrixPosition<=screenHeight-(playerHeight+playerRadius)*2 && dir == Direction.STOP_DOWN) {
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

        collisionValid = !((dir == Direction.STOP_DOWN && matrixPosition >= screenHeight-
                (playerHeight+playerRadius)*2)
                ||(playerRect.centerY()>(screenHeight-playerHeight)&& dir==Direction.STOP_UP));

        // collision handling
        //for tiles
        if (playerRect.centerY() > (playerRadius+3)) {
            // +3 so that the player doesn't collide with an unseen rectangle at the top of the screen
            for (int y = 0; y < submatrix.length; y++) {
                int top=(y - 2) * blockSize + ((dir == Direction.UP || dir == Direction.DOWN) ? matrixPosition : 0);
                if((top<playerRect.centerY() && playerRect.centerY()<top+blockSize) ||
                        playerRect.contains(playerRect.centerX(),top) ||
                        playerRect.contains(playerRect.centerX(),top+blockSize)) {
                    for (int x = 0; x < submatrix[y].length; x++) {
                        if (submatrix[y][x] == 1) {
                            Rect subRect = getRect(x * blockSize, (y - 2) * blockSize +
                                            ((dir == Direction.UP || dir == Direction.DOWN) ? matrixPosition : 0)
                                    , blockSize, blockSize);
                            if (Rect.intersects(playerRect, subRect)) {
                                endGame();
                            }
                        }
                    }
                }
            }
        }
        //collision handling for trail
        Rect bigRect=new Rect(playerRect.left-playerRadius, playerRect.top-playerRadius,
                playerRect.right+playerRadius, playerRect.bottom+playerRadius);
        int lastIndex=0;
        for(int i=trail.size()-1; i>0; i--) {
            if(!bigRect.contains(bigRect.centerX(), trail.get(i).y))  {
                lastIndex = i;
                break;
            }
        }
        for(int i=0; i<trail.size()-1; i++) {
            TrailPoint start=trail.get(i);
            if(i<lastIndex && bigRect.contains(start.x, start.y) && collisionValid){
                if(Rect.intersects(playerRect, new Rect(start.x-(int)(start.radius*0.5), start.y-(int)(start.radius*0.5),
                        start.x+(start.radius), start.y+(start.radius)))) {
                    endGame();
                }
            }
        }

        // make sure player can't leave the screen
        if (playerX < playerRadius) {
            playerX = playerRadius;
        } else if (playerX > screenWidth - playerRadius) {
            playerX = screenWidth - playerRadius;
        }

        // move the trail
        for(int i=0; i<trail.size(); i++)   {
//        for (TrailPoint p : trail) { not used to avoid ConcurrentModificationException
            if (dir == Direction.UP)
                trail.get(i).shiftUp((int) (speedPerSecond / fps));
            else if (dir == Direction.DOWN)
                trail.get(i).shiftDown((int) (speedPerSecond / fps));
        }
    }

    // Draw the newly updated scene
    public void draw() {
        // Make sure our drawing surface is valid or we crash
        if (ourHolder.getSurface().isValid()) {
            // Lock the canvas ready to draw
            canvas = ourHolder.lockCanvas();

            // Draw the background color
            canvas.drawColor(Color.argb(255, 255, 255, 255)); // white

            paint.setAntiAlias(true);
            // set color of rectangles
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(10);
            paint.setStyle(Paint.Style.FILL);

            // matrix logic
            for (int y = 0; y < submatrix.length; y++) {
                for (int x = 0; x < submatrix[y].length; x++) {
                    if (submatrix[y][x] == 1) {
                        canvas.drawRect(getRect(x * blockSize, (y - 2) * blockSize +
                                ((dir == Direction.UP || dir == Direction.DOWN) ? matrixPosition : 0),
                                blockSize, blockSize), paint);
                    }
                }
            }

            // player draw logic
            playerRect.set(getRect((int) playerX - playerRadius,
                    (dir == Direction.DOWN ? playerHeight + playerRadius : screenHeight - playerHeight - playerRadius -
                            ((dir == Direction.STOP_UP || dir == Direction.STOP_DOWN) ? matrixPosition : 0)),
                    2 * playerRadius, 2 * playerRadius));
            // adding current player location to list of trail coordinates
            if(endless) {
                trail.add(new TrailPoint(playerRect.centerX(), playerRect.centerY()));
            }
            else {
                trail.add(new TrailPoint(playerRect.centerX(), playerRect.centerY(), trail.size()));
            }

            // now to draw trail
            paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeJoin(Paint.Join.ROUND);
            for (int i = 0; i < trail.size() - 1; i++) {
                TrailPoint start = trail.get(i);
                start.draw(canvas, paint);

            }

            // drawing player
            paint.setColor(Color.BLUE);
            paint.setStrokeWidth(10);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRoundRect(new RectF(playerRect), playerRadius/2, playerRadius/2, paint);

            paint.setColor(Color.argb(255, 0, 255, 100));   //something that stands out against white and black
            // print score
            paint.setTextSize(70);
            canvas.drawText("Score:"+score, screenWidth-400-(score%100), 100, paint);

            controls.draw(canvas, paint);

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
        gamePaused = true;
        playing = false;
        draw();
//        drawPause();
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
        gamePaused = false;
        gameThread = new Thread(this);
        gameThread.start();
    }

    // ends game and goes to the GameOver Screen
    public void endGame() {
        playing = false;
        // blankRows from the empty rows+3 from the "padding"+onScreen rows= not-counted rows
        // You clear the level if you've completed a loop, that is to say, bounced off the bottom of the screen
        if (gameWon) {
            Intent intent = new Intent(this.context, LevelClearedScreen.class);
                intent.putExtra("SCORE", score);
                intent.putExtra("LEVEL", level);
                intent.putExtra("TOT_SCORE", totScore);
                intent.putExtra("LEVEL_SELECTED", levelSelection);
                intent.putExtra("ENDLESS", endless);
            this.context.startActivity(intent);
        }
        else {
            Intent intent = new Intent(this.context, GameOverScreen.class);
                intent.putExtra("TOT_SCORE", totScore+score);
                intent.putExtra("LEVEL_SELECTED", levelSelection);
                intent.putExtra("ENDLESS", endless);
            this.context.startActivity(intent);
        }
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
                float y = motionEvent.getY();

                if (controls.isPressed((int) playerNewX, (int) y)) {
                    if (controls.isPaused)
                        pause();
                    else
                        resume();
                }

                if (!gamePaused)
                    playerDeltaX = playerNewX - playerX;

                return true;

//                if(!gamePaused) {
//                    if (pause.contains((int) playerNewX, (int) y)) {
//                        gamePaused = true;
//                        pause();
//                        drawPause();
//                    } else {
//                        playerDeltaX = playerNewX - playerX;
//                    }
//                    break;
//                } else {
//                    if (play.contains((int) playerNewX, (int) y))   {
//                        resume();
//                    }
//                }
        }
        return false;
    }
}