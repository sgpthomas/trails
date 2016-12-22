package come.ksatgame.trails.gameEngine;

/**
 * class represents ordered Pair- to use in keeping track of trail coordinates
 * x controls widthwise position.
 * y controls heightwise position
 * Created by aditi on 12/18/16.
 */
public class Pair {

    public int x;
    public int y;

    public Pair(int x, int y)  {
        this.x = x;
        this.y = y;
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

    /**
     *
     * @param top screen height- the highest height coord. permissible to count as in screen
     * @param right screen width- the highest width coord. permissible to count as in screen
     * @return  whether this point is in screen
     */
    public boolean inScreen(int top, int right) {
        return !(x < 0 || y < 0 || x > right || y > top);
    }

}
