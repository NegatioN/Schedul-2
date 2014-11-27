package main.schedul.joakim.schedul2;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import main.schedul.joakim.Databases.DBHelper;
import main.schedul.joakim.information.Chain;

/**
 * Created by NegatioN on 26.11.2014.
 */
public class WidgetBroadcaster extends AppWidgetProvider {


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Log.d("WidgetBroadcast", "OnUpdate called");


        //TODO intent.getextra().getint() db.getchain(int)
        ComponentName thisWidget = new ComponentName(context, WidgetBroadcaster.class);


        DBHelper db = new DBHelper(context);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        Intent buttonIntent = new Intent(context,WidgetBroadcaster.class);
        buttonIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        buttonIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

        PendingIntent pi = PendingIntent.getBroadcast(context, 0, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        for (int widgetId : allWidgetIds) {
            Chain chain = db.getWidgetChain(widgetId);
            if(chain == null) {
                Log.d("WidgetBroadcast.onUpdate", "Widget: " + widgetId + " == null");

            }else {

                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

                // Set the text
                remoteViews.setTextViewText(R.id.tv_widget, chain.getName());

                remoteViews.setInt(R.id.tv_widget, "setBackgroundColor", chain.getDisplayColor());
                remoteViews.setOnClickPendingIntent(R.id.tv_widget, pi);
                //TODO intent run dialogfragment --> in dialog run update appwidgetManager(widgetId, remoteViews);


                appWidgetManager.updateAppWidget(widgetId, remoteViews);

            }


        }

    }



}
