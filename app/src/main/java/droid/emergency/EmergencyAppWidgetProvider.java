package droid.emergency;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

/**
 * EmergencyAppWidgetProvider
 * @author yasupong
 */
public class EmergencyAppWidgetProvider extends AppWidgetProvider {
	// 更新時に呼ばれる
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Intent intent = new Intent(context, EmergencyService.class);
		context.startService(intent);
	}
}
