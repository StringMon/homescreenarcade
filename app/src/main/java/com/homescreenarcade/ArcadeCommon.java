package com.homescreenarcade;

/**
 * Constants used as keys to pass data to the broadcast receivers in GameWallpaperService.
 * 
 * Created by Sterling on 2017-03-02.
 */

public class ArcadeCommon {
    // Actions, coming from controller widgets or the "scoreboard" notification
    static final String ACTION_LEFT = "com.homescreenarcade.LEFT";
    static final String ACTION_RIGHT = "com.homescreenarcade.RIGHT";
    static final String ACTION_UP = "com.homescreenarcade.ACTION_UP";
    static final String ACTION_DOWN = "com.homescreenarcade.ACTION_DOWN";
    static final String ACTION_FIRE = "com.homescreenarcade.FIRE";
    static final String ACTION_PAUSE = "com.homescreenarcade.PAUSE";
// At one time, I had a 4-in-1 D-Pad controller widget. It would be easy to bring back if desired.
//    static final String ACTION_DPAD_UP = "com.homescreenarcade.ACTION_DPAD_UP";
//    static final String ACTION_DPAD_DOWN = "com.homescreenarcade.ACTION_DPAD_DOWN";
//    static final String ACTION_DPAD_LEFT = "com.homescreenarcade.ACTION_DPAD_LEFT";
//    static final String ACTION_DPAD_RIGHT = "com.homescreenarcade.ACTION_DPAD_RIGHT";

    public static final String ACTION_STATUS = "com.homescreenarcade.STATUS";
    // Status keys, coming from game logic, mostly to update the "scoreboard"
    public static final String STATUS_INCREMENT_SCORE = "increment";
    public static final String STATUS_RESET_SCORE = "reset";
    public static final String STATUS_LIVES = "lives";
    public static final String STATUS_LEVEL = "level";
}
