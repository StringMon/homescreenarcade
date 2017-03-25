package com.homescreenarcade;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;


/**
 * Two-button widget for horizontal actions.
 */
public class LeftRightWidget extends GameControlWidget {
    protected void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.left_right_widget);
        views.setOnClickPendingIntent(R.id.left_btn,
                PendingIntent.getBroadcast(context, 0, new Intent(ArcadeCommon.ACTION_LEFT), 0));
        views.setOnClickPendingIntent(R.id.right_btn,
                PendingIntent.getBroadcast(context, 0, new Intent(ArcadeCommon.ACTION_RIGHT), 0));
                
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }
}

