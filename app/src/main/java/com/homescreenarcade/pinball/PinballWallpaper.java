package com.homescreenarcade.pinball;

import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.View;

import com.homescreenarcade.GameWallpaperService;
import com.homescreenarcade.LeftRightWidget;
import com.homescreenarcade.R;

import java.util.ArrayList;

/**
 * GameWallpaper subclass to run Vector Pinball
 * 
 * Created by Sterling on 2017-03-12.
 */

public class PinballWallpaper extends GameWallpaperService {
    private Field field = new Field();
    private FieldDriver fieldDriver = new FieldDriver();
    private FieldViewManager fieldViewManager = new FieldViewManager();
    private CanvasFieldView gameView;
    private PinballEngine engine;
    private float scale = 1f, xCenter = 0f, yCenter = 0f;

    private final Handler releaseFlipperHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    field.setLeftFlippersEngaged(false);
                    break;
                case 1:
                    field.setRightFlippersEngaged(false);
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        titleResID = R.string.pinball_title;
        lifeIconResId = R.drawable.pin_ball;
        notifIconResID = R.drawable.pinball_notif;
        GAME_LOOP_MS = 16;

        FieldLayout.setContext(this);
        field.setAudioPlayer(new VPSoundpool.Player());

        fieldViewManager.setField(field);
        fieldViewManager.setHighQuality(true);

        fieldDriver.setFieldViewManager(fieldViewManager);
        fieldDriver.setField(field);

        gameView = new CanvasFieldView(PinballWallpaper.this, null);
        fieldViewManager.setFieldView(gameView);
        fieldDriver.resetFrameRate();

        // Initialize audio, loading resources in a separate thread.
        VPSoundpool.initSounds(this);
        (new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                VPSoundpool.loadSounds();
                return null;
            }
        }).execute();
    }

    @Override
    public Engine onCreateEngine() {
        engine = new PinballEngine();
        return engine;
    }

    @Override
    protected void draw(Canvas canvas) {
        int state = engine.getState();
        if ((gameView != null) && 
                (engine.isPreview() ||
                 (state == STATE_READY) ||
                 (state == STATE_GAME_OVER) ||
                 ((state == STATE_RUNNING) && field.gameState.gameInProgress))) {
            fieldViewManager.cacheScaleAndOffsets();

            canvas.save();
            if (scale < 1) {
                canvas.scale(scale, scale, xCenter, yCenter);
            } else if (field.gameState.gameInProgress){
                canvas.translate(xCenter, yCenter);
            }
            gameView.doDraw(canvas);
            canvas.restore();

        }
    }

    @Override
    protected void moveLeft() {
        if (!field.getGameState().isGameInProgress() || field.getGameState().isPaused()) {
            if (fieldViewManager.startGameAction != null) {
                fieldViewManager.startGameAction.run();
                return;
            }
        }

        launchBallIfNeeded();
        
//        if (releaseFlipperHandler.hasMessages(0)) {
            releaseFlipperHandler.removeMessages(0);
//        } else {
//            if (field.layout.getLeftFlipperElements().get(0).isFlipperEngaged()) {
//                field.setLeftFlippersEngaged(false);
//            } else {
                field.setLeftFlippersEngaged(true);
                releaseFlipperHandler.sendEmptyMessageDelayed(0, 500);
//            }
//        }
    }
    
    @Override
    protected void moveRight() {
        if (!field.getGameState().isGameInProgress() || field.getGameState().isPaused()) {
            if (fieldViewManager.startGameAction != null) {
                fieldViewManager.startGameAction.run();
                return;
            }
        }

        launchBallIfNeeded();
        
//        if (releaseFlipperHandler.hasMessages(1)) {
            releaseFlipperHandler.removeMessages(1);
//        } else {
//            if (field.layout.getRightFlipperElements().get(0).isFlipperEngaged()) {
//                field.setRightFlippersEngaged(false);
//            } else {
                field.setRightFlippersEngaged(true);
                releaseFlipperHandler.sendEmptyMessageDelayed(1, 500);
//            }
//        }
    }

    void launchBallIfNeeded() {
        // Remove "dead" balls and launch if none already in play.
        field.removeDeadBalls();
        if (field.getBalls().size() == 0)
            field.launchBall();
    }
    
//    protected void dPadLeft() {}
//    protected void dPadRight() {}
//    protected void dPadUp() {}
//    protected void dPadDown() {}

    // Up & Down buttons change tables, but only if no ball is in play
    protected synchronized void moveUp() {
        if (field.getBalls().isEmpty()) {
            if (engine.getLevel() >= FieldLayout.numberOfLevels() - 1) {
                engine.setLevel(0);
            } else {
                engine.setLevel(engine.getLevel() + 1);
            }
            field.resetForLevel(this, engine.getLevel() + 1);
        }
    }
    protected synchronized void moveDown() {
        if (field.getBalls().isEmpty()) {
            if (engine.getLevel() <= 1) {
                engine.setLevel(FieldLayout.numberOfLevels() - 1);
            } else {
                engine.setLevel(engine.getLevel() - 1);
            }
            field.resetForLevel(this, engine.getLevel() + 1);
        }
    }

    protected void fire() {}

    private class PinballEngine extends GameEngine {
        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

            fieldViewManager.setStartGameAction(new Runnable() {@Override
            public void run() {doStartGame();}});
        }

        void doStartGame() {
            if (field.getGameState().isPaused()) {
                onResume();
                return;
            }
            
            if (!field.getGameState().isGameInProgress()) {
                field.resetForLevel(PinballWallpaper.this, getLevel() + 1);
                field.startGame();
                onResume();
            }
        }

        @Override
        protected void onResume() {
            super.onResume();

            if (field.getGameState().isPaused()) {
                field.getGameState().setPaused(false);

                fieldDriver.start();
            }
        }

        @Override
        protected void onPause() {
            VPSoundpool.pauseMusic();
            if (!field.getGameState().isPaused()) {
                field.getGameState().setPaused(true);

                fieldDriver.stop();
            }
            
            super.onPause();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        
            field.endGame();
        }

        @Override
        protected String[] getMissingWidgets() {
            super.getMissingWidgets();

            ArrayList<String> raw = new ArrayList<>();
            if (!settings.getBoolean(LeftRightWidget.class.getSimpleName(), false)) {
                raw.add(getResources().getString(R.string.left_right));
            }

            String[] missing = new String[raw.size()];
            raw.toArray(missing);
            return missing;
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
//            if (DOLOG) Log.d(TAG, "onSurfaceChanged() called with: width = [" + width + "], height = [" + height + "]");

            if (height > width) {
                scale = 0.8f;
                fieldViewManager.setZoom(1);
                yCenter = (height / scale) / (2f * scale);
                xCenter = width / 2f;
            } else {
                scale = 1.0f;
                fieldViewManager.setZoom(1.4f);
                yCenter = 0;
                xCenter = width / 4f;
            }

            final int specW = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            final int specH = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
            gameView.measure(specW, specH);
            gameView.layout(0, 0, width, height);

            if (isPreview()) {
                // Run the game in preview
                field.resetForLevel(PinballWallpaper.this, 1);
                onResume();
                field.startGame();
                launchBallIfNeeded();
            }
        }

        @Override
        protected void resetGame() {
            super.resetGame();

            field.resetForLevel(PinballWallpaper.this, engine.getLevel() + 1);
        }

        @Override
        protected void restartGame() {
            super.restartGame();

            field.startGame();
            field.getGameState().setPaused(true);
        }
    }
}
