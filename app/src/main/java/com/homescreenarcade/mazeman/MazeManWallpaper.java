package com.homescreenarcade.mazeman;

import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;

import com.homescreenarcade.BuildConfig;
import com.homescreenarcade.GameWallpaperService;
import com.homescreenarcade.LeftRightWidget;
import com.homescreenarcade.R;
import com.homescreenarcade.UpDownWidget;

import java.util.ArrayList;

import static android.R.attr.format;
import static com.homescreenarcade.mazeman.GameEngine.DOWN;
import static com.homescreenarcade.mazeman.GameEngine.LEFT;
import static com.homescreenarcade.mazeman.GameEngine.RIGHT;
import static com.homescreenarcade.mazeman.GameEngine.UP;
import static com.homescreenarcade.mazeman.GameEngine.WON;

/**
 * GameWallpaper subclass to run Pac-Mon
 *
 * Created by Sterling on 2017-03-09.
 */

public class MazeManWallpaper extends GameWallpaperService {
    private static final String TAG = "MazeManWallpaper";
    private static final boolean DOLOG = BuildConfig.DEBUG;

    protected GameSurfaceView gameView = null;
    private com.homescreenarcade.mazeman.GameEngine gameEngine;
    private MazeManEngine lwpEngine;
    private float scale = 1f, xCenter = 0f, yCenter = 0f;

    @Override
    public void onCreate() {
        super.onCreate();

        titleResID = R.string.mazeman_title;
        lifeIconResId = R.drawable.mazeman_small;
        notifIconResID = R.drawable.mazeman_small;

        GAME_LOOP_MS = com.homescreenarcade.mazeman.GameEngine.FRAME_PERIOD;

        SoundEngine soundEngine = new SoundEngine(this);
        gameEngine = new com.homescreenarcade.mazeman.GameEngine(this, soundEngine, 1);
    }

    @Override
    public Engine onCreateEngine() {
        lwpEngine = new MazeManEngine();
        return lwpEngine;
    }

    @Override
    protected synchronized void draw(Canvas canvas) {
        if (gameView != null) {
            if (gameEngine.isRunning) {
//                if (lwpEngine.getState() == STATE_READY) {
//                    try {
//                        // send the thread to sleep for a short period
//                        // very useful for battery saving
//                        Thread.sleep(gameEngine.FRAME_PERIOD);
//                    } catch (InterruptedException e) {
//                        return;
//                    }
//                }
//                
//                if (lwpEngine.getState() == STATE_READY) {
//                    gameEngine.updateReady();
//                } else 
                    if (lwpEngine.getState() == STATE_GAME_OVER) {
//                    if (gameEngine.getGameState() == GameSurfaceView.GAMEOVER)
                        gameEngine.updateGameOver();
                } else if (lwpEngine.getState() != STATE_PAUSED) {
//                    gameEngine.gameState = GameSurfaceView.RUNNING;
//                    if (gameEngine.getGameState() == GameSurfaceView.RUNNING) gameEngine.updateRunning();
                    if (gameEngine.getGameState() == GameSurfaceView.WON) {
                        gameEngine.updateWon();
                    } else if (gameEngine.getGameState() == GameSurfaceView.DIE) {
                        gameEngine.updateDie();
                        gameEngine.gameState = com.homescreenarcade.mazeman.GameEngine.READY;
                    } else {
                        gameEngine.updateRunning();
                    }
                }
            }
            
            canvas.save();
            canvas.scale(scale, scale, xCenter, yCenter);
            gameView.drawGame(canvas);
            canvas.restore();
        }
    }

//    protected void dPadLeft() {
//        gameEngine.setInputDir(LEFT);
//    }
//    protected void dPadRight() {
//        gameEngine.setInputDir(RIGHT);
//    }
//    protected void dPadUp() {
//        gameEngine.setInputDir(UP);
//    }
//    protected void dPadDown() {
//        gameEngine.setInputDir(DOWN);
//    }
    protected void moveDown() {
        gameEngine.setInputDir(DOWN);
    }
    protected void moveUp() {
        gameEngine.setInputDir(UP);
    }
    protected void moveLeft() {
        gameEngine.setInputDir(LEFT);
    }
    protected void moveRight() {
        gameEngine.setInputDir(RIGHT);
    }
    protected void fire() {}

    private class MazeManEngine extends GameWallpaperService.GameEngine {
        private int format;

        @Override
        protected void onPause() {
            super.onPause();

            setState();
        }

        @Override
        protected void onResume() {
            super.onResume();

            setState();
        }

        private void setState() {
            if (isPreview() || (getState() == STATE_RUNNING)) {
                gameEngine.resume();
//                gameView.resume();
            } else {
                gameEngine.pause();
//                gameView.pause();
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
         
            setState();
        }

        @Override
        protected String[] getMissingWidgets() {
            super.getMissingWidgets();

            ArrayList<String> raw = new ArrayList<>();
            if (!settings.getBoolean(LeftRightWidget.class.getSimpleName(), false)) {
                raw.add(getResources().getString(R.string.left_right));
            }
            if (!settings.getBoolean(UpDownWidget.class.getSimpleName(), false)) {
                raw.add(getResources().getString(R.string.up_down));
            }

            String[] missing = new String[raw.size()];
            raw.toArray(missing);
            return missing;
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            if (DOLOG) Log.d(TAG, "onSurfaceChanged() called with: width = [" + width + "], height = [" + height + "]");

            if (height > width) {
                scale = 0.6f;
                yCenter = height / 2f;
                xCenter = width / 2f;
            } else {
                scale = (float) height / (float) width;
                yCenter = height * 0.3f;
                xCenter = width / 2f;
            }

            this.format = format;
            final int specW = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            final int specH = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);

            gameView = new GameSurfaceView(MazeManWallpaper.this, gameEngine, 
                    Math.min(width, height), Math.max(width, height));
            gameView.measure(specW, specH);
            gameView.layout(0, 0, width, height);
        }

        @Override
        public void setLevel(int level) {
            super.setLevel(level);
        
            if (gameEngine.gameState == com.homescreenarcade.mazeman.GameEngine.WON) {
                showReadyInterstitial();
                gameEngine.loadMaze(level);
                gameEngine.gameState = com.homescreenarcade.mazeman.GameEngine.READY;
            }
        }

        @Override
        protected void resetGame() {
            super.resetGame();
            
            gameEngine.resetGameplay(1);
        }

        @Override
        protected void restartGame() {
            super.restartGame();

            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            Point dimens = new Point(gameView.getWidth(), gameView.getHeight());
            onSurfaceChanged(getSurfaceHolder(), format, dimens.x, dimens.y);
        }
    }
}
