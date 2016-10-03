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


    static private int[][] genMatrix(int l, int w, int max) //generated original matrix; to be called before start of game
    {
        int track = (int) (Math.random() * l);    //keeps track of the usable track
        int matrix[][] = new int[l][w];
        for (int i = 1; i < l; i++) {
            int[] a = new int[max];
            for (int j = 0; j < max; ) {
                int x = (int) (Math.random() * l);
                if (x != track) {
                    a[j] = x;
                    j++;
                }
            }

            for (int j = 0; j < w; j++) {
                matrix[i][j] = 0;
                for (int k = 0; k < max; k++) {
                    if (j == a[k])
                        matrix[i][j] = 1;
                }
            }
            int j = 0;
            int start = track;
            boolean b = true;
            while (b && track - j >= 0) {
                b = matrix[i][track - j] == 0;
                j++;
            }
            start = track - --j;
            b = true;
            while (b && start + j < matrix[i].length) {
                b = matrix[i][start + j] == 0;
                j++;
            }
            track = start + (int) (Math.random() * j);
        }
        return matrix;
    }
}