package com.ust.thesis.prototype.project.WeSync.chord;

import android.content.Context;
import android.content.Intent;

public interface WifiChangeStateListener {
	
	public void onWifiEnabled(Context context, Intent intent);
	public void onWifiConnected(Context context, Intent intent);
	public void onWifiDisabled(Context context, Intent intent);

}
