package be.li.android.chargeness;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

public class Chargeness extends BroadcastReceiver {

	private int previousBrightness = 1;

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals(
				"android.intent.action.ACTION_POWER_CONNECTED")) {
			Log.v("Chargeness", "Plugged in, setting to full brightness...");

			// save current brightness
			try {
				previousBrightness = android.provider.Settings.System.getInt(
						context.getContentResolver(),
						Settings.System.SCREEN_BRIGHTNESS);
			} catch (SettingNotFoundException e) {
				// ignore
			}
			android.provider.Settings.System.putInt(
					context.getContentResolver(),
					android.provider.Settings.System.SCREEN_BRIGHTNESS, 255);
		} else {
			
			Log.v("Chargeness", "Unplugged, restoring previous brightness...");

			android.provider.Settings.System.putInt(
					context.getContentResolver(),
					android.provider.Settings.System.SCREEN_BRIGHTNESS,
					previousBrightness);
		}

	}

}
