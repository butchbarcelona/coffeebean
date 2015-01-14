package com.ust.thesis.prototype.project.WeSync.chord;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;

public class WifiChangeStateReceiver extends BroadcastReceiver implements LoadingDialogInterface{
	
	private void onConnected(Context context){
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (mWifi.isConnected()) {
			ChordConnectionManager.getInstance().setupChord();
		}
	}
	
	private void onDisconnect(Context context){
		ChordConnectionManager.getInstance().stopChord();
	}
	
	public void startWifiConnect(Context ctx){
		
		new LoadingDialog(ctx,"Enabling Wifi", this).execute();
	}
	
	
	@Override 
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		String action = intent.getAction();
		if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION .equals(action)) {
			SupplicantState state = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
			if (SupplicantState.isValidState(state) 
					&& state == SupplicantState.COMPLETED) {

				onConnected(context);
			}
		}else{


			int extraWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE ,
					WifiManager.WIFI_STATE_UNKNOWN);

			switch(extraWifiState){
			case WifiManager.WIFI_STATE_DISABLED:
			case WifiManager.WIFI_STATE_DISABLING:
				onDisconnect(context);
				break;
			case WifiManager.WIFI_STATE_ENABLED:
				
				break;
			case WifiManager.WIFI_STATE_ENABLING: 
				
				break;
			case WifiManager.WIFI_STATE_UNKNOWN:
				break;
			}
		}

	}

	@Override
	public void onStartLoading() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoading() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFinishLoading() {
		// TODO Auto-generated method stub
		
	}

}
