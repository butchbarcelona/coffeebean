package com.ust.thesis.prototype.project.WeSync.chord;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class CWifiConnectivity implements IWifiConnectivity{

	private Context context;

	public CWifiConnectivity(Context context) {
		this.context = context;
	}

	public void turnOnOffHotspot(boolean isTurnToOn) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiApControl apControl = WifiApControl.getApControl(wifiManager);
		if (apControl != null) {

			if (wifiManager.isWifiEnabled() && isTurnToOn) {
				turnOnOffWifi(false);
			}

			if (!apControl.isWifiApEnabled()) {
				Toast.makeText(context, "Turning On WiFi HotSpot",
						Toast.LENGTH_LONG).show();
			}

			apControl.setWifiApEnabled(apControl.getWifiApConfiguration(),
					isTurnToOn);
		}
	}

	public void turnOnOffWifi(boolean isTurnToOn) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(isTurnToOn);
	}

}
