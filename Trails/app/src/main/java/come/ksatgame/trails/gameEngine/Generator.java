package come.ksatgame.trails.gameEngine;

/**
 * Created by samthomas on 10/2/16.
 */

public class Generator {

    private static Generator instance;

    private Generator() {

    }

    public static Generator getInstance() {
        if (instance == null)
            instance = new Generator();
        return instance;

    }

    //max is maximum number of obstacles per line. assumption: less then half of width
    private int[][] genMatrix(int l, int w, int max) //generated original matrix; to be called before start of game
    {
        int matrix[][] = new int[l][w];
        for (int i = 0; i < l; i++) {
            int[] a = new int[max];
            for (int j = 0; j < max; j++) {
                a[j] = (int) Math.random() * l;
            }
            int k = 0;
            for (int j = 0; j < w; j++) {
                if (j == a[k]) {
                    matrix[i][j] = a[k++];
                }
            }
        }
        return matrix;
    }
}
