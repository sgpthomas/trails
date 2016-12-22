package come.ksatgame.trails.gameEngine;

/**
 * Created by samthomas on 10/2/16.
 */

public class Generator {

    private static Generator instance;

    public static Generator getInstance() {
        if (instance == null)
            instance = new Generator();
        return instance;
    }


    // generated original matrix; to be called before start of game
    public int[][] genMatrix(int l, int w, int max) {
        int track = (int) (Math.random()*w); // keeps track of the usable track
        int matrix[][] = new int[l][w]; // this is what we are generating

        // loop through the rows
        for (int i = 0; i < l; i++) {

            // loop through the cols and set everything to 0
            for (int j = 0; j < w; j++) {
                matrix[i][j] = 0;
            }

            if (i < l - 10) {
                // loop through the cols and choose max 1s
                for (int j = 0; j < max; ) {
                    int x = (int) (Math.random() * w);

                    // make sure we keep an open track
                    if (x != track) {
                        matrix[i][x] = 1;
                        j++;
                    }
                }

                int start = track;
                while (start >= 0) {
                    if (matrix[i][start] != 0)
                        break;
                    start--;
                }
                start++;
                int j = track - start;
                while (start + j < matrix[i].length) {
                    if (matrix[i][start + j] != 0)
                        break;
                    j++;
                }
                track = start + (int) (Math.random() * j);
            }
        }
        return matrix;
    }
}
