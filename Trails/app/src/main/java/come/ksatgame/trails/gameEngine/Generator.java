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


    public int[][] genMatrix(int l, int w, int max) //generated original matrix; to be called before start of game
    {
        int track= (int) (Math.random()*w);	//keeps track of the usable track
        int matrix[][] = new int[l][w];
        for (int i = 0; i < l; i++) {

            for (int j = 0; j < w; j++) {
                matrix[i][j]=0;
            }
            for (int j = 0; j < max;) {
                int x = (int) (Math.random()*l);
                if(x!=track)	{
                    matrix[i][x]=1;
                    j++;
                }
            }
            int j=0;
            int start=track;
            boolean b=true;
            while(b && start>=0)
            {
                b=matrix[i][start]==0;
                if(!b)
                    start++;
                start--;
            }
            start++;
            j=track-start;
            b=true;
            while(b && start+j<matrix[i].length)
            {
                b=matrix[i][start+j]==0;
                if(!b)
                    j--;
                j++;
            }
            track=start+(int)(Math.random()*j);
        }
        return matrix;
    }
}
