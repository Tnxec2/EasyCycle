package com.kontranik.cycleora.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.view.View
import android.widget.RemoteViews
import com.kontranik.cycleora.R
import com.kontranik.cycleora.helper.PhasesHelper
import com.kontranik.cycleora.storage.SettingsService
import android.app.PendingIntent

import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.kontranik.cycleora.MainActivity


/**
 * Implementation of App Widget functionality.
 */
class DayInfoWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }


    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {

        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.day_info_widget)

        views.setOnClickPendingIntent(R.id.appwidget_widgetRoot,
            PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), PendingIntent.FLAG_MUTABLE))

        val lastCycle = SettingsService.loadLastCycleStart(context)
        if (lastCycle != null) {
            val result = PhasesHelper.getDaysInfo(context, 1, lastCycle)
            if (result.isEmpty()) {
                views.setTextViewText(
                    R.id.appwidget_title,
                    context.getString(R.string.appwidget_title_nodata)
                )
                views.setTextViewText(
                    R.id.appwidget_description,
                    context.getString(R.string.appwidget_title_nodata)
                )
            } else {
                views.setTextViewText(
                    R.id.appwidget_title,
                    context.getString(R.string.appwidget_title, result[0].cyclesDay)
                )
                var desc = ""
                result[0].phases.forEach {
                    desc = desc + it.desc + "\n"
                }
                views.setTextViewText(
                    R.id.appwidget_description,
                    if (desc.isNotEmpty()) desc else context.getString(R.string.appwidget_description_nodata)
                )
            }
        } else {
            views.setTextViewText(
                R.id.appwidget_title,
                context.getString(R.string.appwidget_title_nodata)
            )
            views.setTextViewText(
                R.id.appwidget_description,
                context.getString(R.string.appwidget_title_nodata)
            )
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private val UPDATE_WIDGET = "UPDATE_WIDGET"
}
