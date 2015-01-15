package com.ust.thesis.prototype.project.WeSync;

import android.app.Activity;
import android.os.Bundle;

public class ChordActivity extends Activity{

	static boolean hasInstance = false;
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		hasInstance = true;
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		hasInstance = false;
	}
	
	public static boolean isRunning(){
		return hasInstance;
	}
	
	
}
