package com.homescreenarcade;

import android.annotation.TargetApi;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.homescreenarcade.mazeman.MazeManWallpaper;
import com.homescreenarcade.pinball.PinballWallpaper;
import com.homescreenarcade.invaders.InvadersWallpaper;
import com.homescreenarcade.blockdrop.BlockDropWallpaper;

/**
 * A simple activity to get the user started playing games on their home screen.
 */
public class LauncherActivity extends AppCompatActivity {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.launcher_activity);

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            // ACTION_CHANGE_LIVE_WALLPAPER wasn't supported before JB, so hide the buttons. They'll 
            // just have to figure out how to set the LWPs themselves.
            findViewById(R.id.invaders).setVisibility(View.GONE);
            findViewById(R.id.block_drop).setVisibility(View.GONE);
            findViewById(R.id.mazeman).setVisibility(View.GONE);
            findViewById(R.id.pinball).setVisibility(View.GONE);
            
        } else {
            // JB or later: set up the LWP-shortcut buttons
            
            findViewById(R.id.invaders).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
                            .putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                                    new ComponentName(LauncherActivity.this, InvadersWallpaper.class));
                    startActivity(intent);
                    finish();
                }
            });

            findViewById(R.id.block_drop).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
                            .putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                                    new ComponentName(LauncherActivity.this, BlockDropWallpaper.class));
                    startActivity(intent);
                    finish();
                }
            });

            findViewById(R.id.mazeman).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
                            .putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                                    new ComponentName(LauncherActivity.this, MazeManWallpaper.class));
                    startActivity(intent);
                    finish();
                }
            });

            findViewById(R.id.pinball).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
                            .putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                                    new ComponentName(LauncherActivity.this, PinballWallpaper.class));
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
}
