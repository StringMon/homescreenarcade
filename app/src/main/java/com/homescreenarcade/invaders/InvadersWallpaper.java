package com.homescreenarcade.invaders;

import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;

import com.homescreenarcade.FireButtonWidget;
import com.homescreenarcade.GameWallpaperService;
import com.homescreenarcade.LeftRightWidget;
import com.homescreenarcade.R;
import com.homescreenarcade.UpDownWidget;

import java.util.ArrayList;

/**
 * GameWallpaper subclass to run Space Invaders
 *
 * Created by Sterling on 2017-03-01.
 */

public class InvadersWallpaper extends GameWallpaperService {
    private static final String TAG = "InvadersWallpaper";
    private static final boolean DOLOG = true;

    protected InvaderView gameView = null;
    private int format;
    private InvaderEngine engine;
    private float scale = 1f, xCenter = 0f, yCenter = 0f;

    public void onCreate() {
        titleResID = R.string.invaders_title;
        lifeIconResId = R.drawable.invaders_tank;
        notifIconResID = R.drawable.ic_invaders;

        GAME_LOOP_MS = 80;
    }

    @Override
    public Engine onCreateEngine() {
        engine = new InvaderEngine();
        return engine;
    }

    protected void draw(Canvas canvas) {
        if ((gameView != null) && (engine.isPreview() || engine.getState() == STATE_RUNNING)) {
            canvas.save();
            canvas.scale(scale, scale, xCenter, yCenter);
            gameView.draw(canvas);
            canvas.restore();        }
    }

    protected void moveLeft() {
        gameView.mMove = true;
        gameView.mMoveLeft = true;
        gameView.mTap = true;
    }
//    protected void dPadLeft() {
//        moveLeft();
//    }

    protected void moveRight() {
        gameView.mMove = true;
        gameView.mMoveLeft = false;
        gameView.mTap = true;
    }
//    protected void dPadRight() {
//        moveRight();
//    }

    protected void fire() {
        gameView.mFire = true;
        gameView.mTap = true;
    }
    protected void moveUp() {
        fire();
    }

    protected void moveDown() {}
//    protected void dPadUp() {}
//    protected void dPadDown() {}

    private class InvaderEngine extends GameEngine {

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            if (DOLOG) Log.d(TAG, "onSurfaceChanged() called with: width = [" + width + "], height = [" + height + "]");

            if (height > width) {
                scale = Math.min(1, (width / (float) height) / 0.63f);
                yCenter = height / 2f;
            } else {
                scale = 1f;
                yCenter = 0;
            }
            xCenter = width / 2f;
            
            InvadersWallpaper.this.format = format;
            final int specW = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            final int specH = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);

            gameView = new InvaderView(getBaseContext());
            gameView.measure(specW, specH);
            gameView.layout(0, 0, width, height);
        }

        @Override
        protected String[] getMissingWidgets() {
            super.getMissingWidgets();
            
            ArrayList<String> raw = new ArrayList<>();
            if (!settings.getBoolean(LeftRightWidget.class.getSimpleName(), false)) {
                raw.add(getResources().getString(R.string.left_right));
            }
            if (!settings.getBoolean(UpDownWidget.class.getSimpleName(), false) &&
                    !settings.getBoolean(FireButtonWidget.class.getSimpleName(), false)) {
                raw.add(getResources().getString(R.string.fire_btn));
            }

            String[] missing = new String[raw.size()];
            raw.toArray(missing);
            return missing;
        }

        @Override
        protected void resetGame() {
            super.resetGame();

            gameView.resetGameplay();
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
