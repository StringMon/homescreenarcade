package com.homescreenarcade;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;

/**
 * Common ancestor for widgets that control gameplay.
 * 
 * Created by Sterling on 2017-03-09.
 */

abstract class GameControlWidget extends AppWidgetProvider {
    protected abstract void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                   int appWidgetId);
    
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Register existence when the first widget is created
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                .edit()
                .putBoolean(getClass().getSimpleName(), true)
                .apply();
    }

    @Override
    public void onDisabled(Context context) {
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                .edit()
                .putBoolean(getClass().getSimpleName(), false)
                .apply();
    }
}
