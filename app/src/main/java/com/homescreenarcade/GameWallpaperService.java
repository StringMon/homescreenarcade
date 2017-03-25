package com.homescreenarcade;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.service.wallpaper.WallpaperService;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.SpannableStringBuilder;
import android.text.style.BulletSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import static android.support.v4.app.NotificationCompat.VISIBILITY_SECRET;

/**
 * Ancestor class for game wallpapers containing common interface behaviors. 
 * 
 * This is the core of Home Screen Arcade, the class that ties it all together:
 * - It serves as a base class for the live wallpaper services that implement specific games.
 * - It receives action broadcasts from the game-control widgets, passing them along to its 
 *   descendants as method calls.
 * - Broadcasts from the actual game code are also received here to update score, level, and other 
 *   game status, and are then displayed as a heads-up notification.
 * 
 * Created by Sterling on 2017-03-05.
 */

public abstract class GameWallpaperService 
        extends WallpaperService {
    private static final String TAG = "GameWallpaperService";
    protected static final boolean DOLOG = BuildConfig.DEBUG;

    public static final int STATE_NOT_READY = 0;
    public static final int STATE_READY = 1;
    public static final int STATE_RUNNING = 2;
    public static final int STATE_PAUSED = 3;
    public static final int STATE_GAME_OVER = 4;
    
    private static boolean previewActive = false;

    /*
        These four constants should be set by subclasses at startup.
     */
    protected static int GAME_LOOP_MS = 33; // Milliseconds between game canvas/logic updates
    protected int titleResID = 0;           // Title of the status notification (String resource)
    protected int notifIconResID = 0;       // Status notification icon (Drawable resource)
    protected int lifeIconResId = 0;        // If the game has "lives", this is the icon to represent them (drawable resource)

    /*
        Methods to be implemented by subclasses:
         - draw() gets the game graphics for display on the wallpaper.
         - The rest are game actions; may be no-op if not needed by a particular game.
     */
    protected abstract void draw(Canvas canvas);
    protected abstract void moveLeft();
    protected abstract void moveRight();
    protected abstract void moveUp();
    protected abstract void moveDown();
    protected abstract void fire();
//    protected abstract void dPadLeft();
//    protected abstract void dPadRight();
//    protected abstract void dPadUp();
//    protected abstract void dPadDown();

    protected class GameEngine 
            extends Engine 
            implements SharedPreferences.OnSharedPreferenceChangeListener {
        private Timer timer;
        private int score = 0;
        private int lives = 0;
        private int level = 0;

        boolean isPaused = false;
        
        private long readyTime = 0;
        private IntentFilter actionFilter;
        protected SharedPreferences settings;

        /**
         * Processes action broadcasts from game controller widgets
         */
        private BroadcastReceiver actionReceiver = new BroadcastReceiver() {
            @TargetApi(Build.VERSION_CODES.N_MR1)
            @Override
            public void onReceive(Context context, Intent intent) {
                if (lives < 0) {
                    if (isPaused && readyTime == 0) {
                        showReadyInterstitial();
                        setLives(0);
                        return;
                    } 
                }

                if (isPaused || ArcadeCommon.ACTION_PAUSE.equals(intent.getAction())) {
                    long elapsed = System.currentTimeMillis() - readyTime;
                    if ((readyTime > 0) && (elapsed > 3000)) {
                        // Cutting the "Ready" screen short
                        readyTime = 0;
                        restartGame();
                        onResume();
                    } else if (isPaused) {
                        onResume();
                    } else {
                        onPause();
                    }

                    statusReceiver.onReceive(context, new Intent());

                    return;
                }

                switch (intent.getAction()) {
                    case ArcadeCommon.ACTION_UP:
                        moveUp();
                        break;
                    case ArcadeCommon.ACTION_DOWN:
                        moveDown();
                        break;
                    case ArcadeCommon.ACTION_LEFT:
                        moveLeft();
                        break;
                    case ArcadeCommon.ACTION_RIGHT:
                        moveRight();
                        break;
                    case ArcadeCommon.ACTION_FIRE:
                        fire();
                        break;
//                    case ArcadeCommon.ACTION_DPAD_UP:
//                        dPadUp();
//                        break;
//                    case ArcadeCommon.ACTION_DPAD_DOWN:
//                        dPadDown();
//                        break;
//                    case ArcadeCommon.ACTION_DPAD_LEFT:
//                        dPadLeft();
//                        break;
//                    case ArcadeCommon.ACTION_DPAD_RIGHT:
//                        dPadRight();
//                        break;
                }
            }
        };

        /**
         * Game logic should send STATUS* updates as extras to LocalBroadcastManager messages
         * that will be received here and displayed as a game status "scoreboard" notification.
         */
        private BroadcastReceiver statusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getBooleanExtra(ArcadeCommon.STATUS_RESET_SCORE, false)) {
                    score = 0;
                } else {
                    score += intent.getIntExtra(ArcadeCommon.STATUS_INCREMENT_SCORE, 0);
                }

                RemoteViews notifViews = new RemoteViews(context.getPackageName(),
                        R.layout.status_notification);
                notifViews.setTextViewText(R.id.title, getString(titleResID));
                notifViews.setImageViewResource(R.id.notif_icon, notifIconResID);

                NotificationManager notifMgr =
                        (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                notifViews.setTextViewText(R.id.score, getString(R.string.score, score));

                int newLevel = intent.getIntExtra(ArcadeCommon.STATUS_LEVEL, level);
                if (newLevel != level) {
                    setLevel(newLevel);
                }
                if (level >= 0) {
                    notifViews.setTextViewText(R.id.level, getString(R.string.level, level));
                }

                setLives(intent.getIntExtra(ArcadeCommon.STATUS_LIVES, lives));
                if (previewActive) {
                    return;
                }
                
                notifViews.removeAllViews(R.id.lives_area);
                if (lives < 0) {
                    // Game over, man
                    RemoteViews gameOver = new RemoteViews(getPackageName(), R.layout.status_text);
                    gameOver.setTextViewText(R.id.status_text, "Game Over");
                    notifViews.addView(R.id.lives_area, gameOver);
                    if (!isPaused) {
                        onPause();
                    }
                } else {
                    Bitmap lifeBitmap = BitmapFactory.decodeResource(getResources(), lifeIconResId);
                    for (int i = 0; i < lives; i++) {
                        RemoteViews thisLife = new RemoteViews(getPackageName(), R.layout.status_life_icon);
                        thisLife.setBitmap(R.id.life_icon, "setImageBitmap", lifeBitmap);
                        notifViews.addView(R.id.lives_area, thisLife);
                    }
                }

                Bitmap pauseBtn = BitmapFactory.decodeResource(getResources(),
                        isPaused || (readyTime > 0) 
                                ? R.drawable.ic_play_circle_outline_white_48dp
                                : R.drawable.ic_pause_circle_outline_white_48dp);
                notifViews.setBitmap(R.id.play_pause, "setImageBitmap", pauseBtn);
                notifViews.setOnClickPendingIntent(R.id.play_pause,
                        PendingIntent.getBroadcast(context, 0, 
                                new Intent(ArcadeCommon.ACTION_PAUSE), 0));
                
                final NotificationCompat.Builder notif = new NotificationCompat.Builder(context)
                        .setSmallIcon(notifIconResID)
                        .setContent(notifViews)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setVisibility(VISIBILITY_SECRET); // keep the scroreboard off the lock screen
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // This forces a heads-up notification
                    notif.setVibrate(new long[0]);
                }

                notifMgr.notify(0, notif.build());
            }
        };

        /**
         * This timer implements the "game loop", showing instructions if needed, otherwise calling
         * descendants' draw() method to get the actual game game graphics.
         */
        private class GameTask extends TimerTask {
            @Override
            public void run() {
                updateSurface();
            }
        }
        @SuppressLint("InflateParams")
        private void updateSurface() {
            View metaView = null;
            if (!isPreview()) {
                String[] missingWidgets = getMissingWidgets();
                if (missingWidgets.length > 0) {
                    // Show instructions screen
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    metaView = inflater.inflate(R.layout.instruction_view, null);
                    ((TextView) metaView.findViewById(R.id.title)).setText(
                            getResources().getString(titleResID));

                    SpannableStringBuilder content = new SpannableStringBuilder();
                    int pad = getResources().getDimensionPixelOffset(R.dimen.bullet_padding);
                    for (String missing : missingWidgets) {
                        int start = content.length();
                        content.append(missing)
                                .append('\n');
                        int end = content.length();
                        content.setSpan(new BulletSpan(pad), start, end, 0);
                    }
                    ((TextView) metaView.findViewById(R.id.list)).setText(content);

                    cancelTimer();

                } else if (System.currentTimeMillis() - readyTime < 5000) {
                    // Ready player one!
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    metaView = inflater.inflate(R.layout.get_ready_view, null);
                    if (!isPaused) {
                        onPause();
                    }
                } else if (readyTime > 0) {
                    readyTime = 0;
                    if (level <= 1) {
                        resetGame();
                    }
                    restartGame();
                    onResume();
                }
            }

            final SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                if (metaView != null) {
                    canvas = holder.lockCanvas();
                    if ((canvas != null) && (canvas.getWidth() > 0) && (canvas.getHeight() > 0)) {
                        canvas.drawColor(Color.BLACK);
                        final int specW = View.MeasureSpec.makeMeasureSpec(canvas.getWidth(), View.MeasureSpec.EXACTLY);
                        final int specH = View.MeasureSpec.makeMeasureSpec(canvas.getHeight(), View.MeasureSpec.EXACTLY);
                        metaView.measure(specW, specH);
                        metaView.layout(0, 0, canvas.getWidth(), canvas.getHeight());
                        metaView.draw(canvas);
                    }
                } else if (getState() != STATE_PAUSED) {
                    // Show the actual game graphics from descendant class
                    canvas = holder.lockCanvas();
                    if ((canvas != null) && (canvas.getWidth() > 0) && (canvas.getHeight() > 0)) {
                        draw(canvas);
                    }
                }
                
            } finally {
                if (canvas != null) {
                    try {
                        holder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        Log.e(TAG, "Unlocking canvas: " + e);
                    }
                }
            }
        }
        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            if (DOLOG) Log.d(TAG, "onCreate()");

            actionFilter = new IntentFilter();
            actionFilter.addAction(ArcadeCommon.ACTION_FIRE);
//            actionFilter.addAction(ArcadeCommon.ACTION_DPAD_UP);
//            actionFilter.addAction(ArcadeCommon.ACTION_DPAD_DOWN);
//            actionFilter.addAction(ArcadeCommon.ACTION_DPAD_LEFT);
//            actionFilter.addAction(ArcadeCommon.ACTION_DPAD_RIGHT);
            actionFilter.addAction(ArcadeCommon.ACTION_UP);
            actionFilter.addAction(ArcadeCommon.ACTION_DOWN);
            actionFilter.addAction(ArcadeCommon.ACTION_LEFT);
            actionFilter.addAction(ArcadeCommon.ACTION_RIGHT);
            actionFilter.addAction(ArcadeCommon.ACTION_PAUSE);

            IntentFilter statusFilter = new IntentFilter();
            statusFilter.addAction(ArcadeCommon.ACTION_STATUS);
            if (!isPreview()) {
                LocalBroadcastManager.getInstance(getBaseContext())
                        .registerReceiver(statusReceiver, statusFilter);
            }

            settings = getSharedPreferences("settings", Context.MODE_PRIVATE);
            settings.registerOnSharedPreferenceChangeListener(this);
            if (getMissingWidgets().length == 0) {
                readyTime = System.currentTimeMillis();
            }
        }

        @Override
        public void onDestroy() {
            if (!isPreview()) {
                LocalBroadcastManager.getInstance(getBaseContext())
                        .unregisterReceiver(statusReceiver);
            }

            settings.unregisterOnSharedPreferenceChangeListener(this);
            
            onPause();

            NotificationManager notifMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notifMgr.cancel(0);

            super.onDestroy();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (DOLOG) Log.d(TAG, "onVisibilityChanged, visible = " + visible + ", isPreview() = " + isPreview());
            
            cancelTimer();

            previewActive = isPreview() && visible;
            
            if (visible) {
                startTimer();
                
                if (isPreview()) {
                    NotificationManager notifMgr =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notifMgr.cancel(0);
                } else {
                    registerReceiver(actionReceiver, actionFilter);
                }

            } else {
                if (!isPreview()) {
                    try {
                        unregisterReceiver(actionReceiver);
                    } catch (IllegalArgumentException e) {
                        // Simply means that there's nothing to unregister
                    }
                }
            }
        }

        /**
         * Game wallpaper subclasses should implement this method to check what game control 
         * widgets they require versus which ones are currently active. 
         * 
         * @return an array of widget class names required by the game but not active
         */
        protected String[] getMissingWidgets() {
            if (settings == null) {
                settings = getSharedPreferences("settings", Context.MODE_PRIVATE);
            }

            return new String[] {};
        }

        private void startTimer() {
            timer = new Timer();
            timer.schedule(new GameTask(), 0, GAME_LOOP_MS);
        }

        private void cancelTimer() {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        }

        protected void onPause() {
            isPaused = true;
            if (!isPreview()) {
                statusReceiver.onReceive(GameWallpaperService.this, new Intent());
            }
        }
        protected void onResume() {
            isPaused = false;
            if (!isPreview()) {
                statusReceiver.onReceive(GameWallpaperService.this, new Intent());
            }
        }

        /**
         * When game control widgets are added to or removed from the launcher, this restarts the
         * game loop to update the instructions (if needed).
         */
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (//key.equals(DPadWidget.class.getSimpleName()) ||
                    key.equals(FireButtonWidget.class.getSimpleName()) ||
                    key.equals(UpDownWidget.class.getSimpleName()) ||
                    key.equals(LeftRightWidget.class.getSimpleName())) {
                readyTime = System.currentTimeMillis();
                isPaused = false;
                if (timer == null) {
                    startTimer();
                } else {
                    updateSurface();
                }
            }
        }

        protected void showReadyInterstitial() {
            readyTime = System.currentTimeMillis() - 2000;
        }

        private int getLives() {
            return lives;
        }
        private void setLives(int lives) {
            if (lives > getLives() && (lives == 0)) {
                resetGame();
            }
            this.lives = lives;
        }

        public int getLevel() {
            return level;
        }
        public void setLevel(int level) {
            this.level = level;
        }

        public int getState() {
            if (getMissingWidgets().length > 0) {
                return STATE_NOT_READY;
            } else if (readyTime > 0) {
                return STATE_READY;
            } else if (isPaused || !isVisible()) {
                return STATE_PAUSED;
            } else if (lives < 0) {
                return STATE_GAME_OVER;
            } else {
                return STATE_RUNNING;
            }
        }

        /*
         * Subclasses should implement these two methods as needed to interface with game-specific 
         * logic:
         * 
         * reset: clear game status in preparation for a new session.
         * restart: get the game action underway
         */
        protected void resetGame() {
        }
        protected void restartGame() {
        }
    }
}
