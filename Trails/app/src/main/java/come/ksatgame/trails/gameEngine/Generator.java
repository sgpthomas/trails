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
}
