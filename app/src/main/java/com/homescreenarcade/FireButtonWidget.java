package com.homescreenarcade;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Simple one-button widget for "Fire" functionality.
 */
public class FireButtonWidget extends GameControlWidget {
    protected void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.fire_button_widget);
        views.setOnClickPendingIntent(R.id.fire_btn,
                PendingIntent.getBroadcast(context, 0, new Intent(ArcadeCommon.ACTION_FIRE), 0));
                
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

