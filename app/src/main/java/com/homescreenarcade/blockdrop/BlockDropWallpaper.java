package com.homescreenarcade.blockdrop;

import android.content.Intent;
import android.graphics.Canvas;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.SurfaceHolder;

import com.homescreenarcade.ArcadeCommon;
import com.homescreenarcade.GameWallpaperService;
import com.homescreenarcade.LeftRightWidget;
import com.homescreenarcade.R;
import com.homescreenarcade.UpDownWidget;
import com.homescreenarcade.blockdrop.components.Controls;
import com.homescreenarcade.blockdrop.components.Display;
import com.homescreenarcade.blockdrop.components.GameState;
import com.homescreenarcade.blockdrop.components.Sound;

import java.util.ArrayList;

/**
 * GameWallpaper subclass to run Blockinger (Tetris clone)
 * 
 * Created by Sterling on 2017-03-05.
 */

public class BlockDropWallpaper extends GameWallpaperService {
    private static final String TAG = "BlockDropWallpaper";

    public Display display;
    public GameState game;
    public Controls controls;
    public Sound sound;
    private float scale = 1f, xCenter = 0f, yCenter = 0f;
    private BlockDropEngine engine;

    @Override
    public void onCreate() {
        super.onCreate();

        titleResID = R.string.block_drop_title;
        notifIconResID = R.drawable.block_piece;

        controls = new Controls(this);

        sound = new Sound(this);
        sound.loadEffects();

        display = new Display(this);
        game = GameState.getNewInstance(this);
        game.reconnect(this);
    }

    @Override
    public void onDestroy() {
        sound.release();
        
        super.onDestroy();
    }

    @Override
    public Engine onCreateEngine() {
        engine = new BlockDropEngine();
        return engine;
    }

    @Override
    protected void draw(Canvas canvas) {
        long tempTime = System.currentTimeMillis();
        if (game != null) {
//            engine.setState();
            if (engine.isPreview() || (engine.getState() == STATE_PAUSED) || 
                    (engine.getState() == STATE_RUNNING)) {
                if (game.cycle(tempTime)) {
                    controls.cycle(tempTime);
                }
                game.getBoard().cycle(tempTime);
            }
        }
        
        canvas.save();
        canvas.scale(scale, scale, xCenter, yCenter);
        display.doDraw(canvas, 1000 / GAME_LOOP_MS);
        canvas.restore();
    }

    @Override
    protected void moveLeft() {
        controls.leftButtonPressed(true);
    }

    @Override
    protected void moveRight() {
        controls.rightButtonPressed(true);
    }

//    protected void dPadLeft() {
//        controls.leftButtonPressed(true);
//    }
//    protected void dPadRight() {
//        controls.rightButtonPressed(true);
//    }
//    protected void dPadDown() {
//        controls.dropButtonPressed();
//    }
//    protected void dPadUp() {}

    @Override
    protected void moveUp() {
        controls.rotateRightPressed();
    }
    @Override
    protected void moveDown() {
        controls.dropButtonPressed();
    }

    @Override
    protected void fire() {}

    public void gameOver(long score, String timeString, int i) {
        // called from GameState
        Intent statusIntent = new Intent(ArcadeCommon.ACTION_STATUS)
                .putExtra(ArcadeCommon.STATUS_LIVES, -1);
        LocalBroadcastManager.getInstance(getBaseContext())
                .sendBroadcast(statusIntent);
        engine.setState();
    }

    private class BlockDropEngine extends GameEngine {

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

//            if (!isPreview()) {
//                game.setLevel(game.getLevel()); // kind of a hack, to show the scoreboard on startup
//            }
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            if (DOLOG) Log.d(TAG, "onSurfaceCreated");

            setState();
        }

        @Override
        protected void onPause() {
            super.onPause();
            sound.setInactive(true);
            setState();
        }

        @Override
        protected void onResume() {
            super.onResume();
            sound.setInactive(false);
    	
            setState();
        }

        @Override
        protected String[] getMissingWidgets() {
            super.getMissingWidgets();

            ArrayList<String> raw = new ArrayList<>();
            if (!settings.getBoolean(UpDownWidget.class.getSimpleName(), false)) {
                raw.add(getResources().getString(R.string.up_down));
            }
            if (!settings.getBoolean(LeftRightWidget.class.getSimpleName(), false)) {
                raw.add(getResources().getString(R.string.left_right));
            }

            String[] missing = new String[raw.size()];
            raw.toArray(missing);
            return missing;
        }

        private void setState() {
            if (game != null) {
                game.setRunning(isPreview() || (getState() == STATE_RUNNING));
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            if (DOLOG)
                Log.d(TAG, "onSurfaceChanged: width = [" + width + "], height = [" + height + "]");

            display = new Display(BlockDropWallpaper.this);

            if (height > width) {
                scale = (float) width / (float) height;
                yCenter = height / 2f;
            } else {
                scale = 0.8f;
                yCenter = (height / scale) / (2f * scale);
            }
            xCenter = width / 2f;

            if (getState() != STATE_PAUSED) {
                // Manually redraw with the new dimensions (it won't redraw automatically when paused)
                Canvas canvas = holder.lockCanvas();
                draw(canvas);
                holder.unlockCanvasAndPost(canvas);
            }
        }
        
        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            
            if (visible) {
//                if (wasPreview && !isPreview()) {
//                    restartGame();
//                }
//                wasPreview = isPreview();
                sound.resume();
            } else {
                sound.pause();
            }
        }

        @Override
        protected void restartGame() {
            super.restartGame();

            display = new Display(BlockDropWallpaper.this);
            game = GameState.getNewInstance(BlockDropWallpaper.this);
            game.reconnect(BlockDropWallpaper.this);
        }
    }
}
