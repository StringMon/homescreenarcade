package com.homescreenarcade;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Two-button widget for vertical actions.
 */
public class UpDownWidget extends GameControlWidget {
    protected void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.up_down_widget);
        views.setOnClickPendingIntent(R.id.up_btn,
                PendingIntent.getBroadcast(context, 0, new Intent(ArcadeCommon.ACTION_UP), 0));
        views.setOnClickPendingIntent(R.id.down_btn,
                PendingIntent.getBroadcast(context, 0, new Intent(ArcadeCommon.ACTION_DOWN), 0));
                
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

