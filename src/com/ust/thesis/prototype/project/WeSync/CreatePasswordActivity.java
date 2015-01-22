package com.ust.thesis.prototype.project.WeSync;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ust.thesis.prototype.project.WeSync.chord.CWifiConnectivity;
import com.ust.thesis.prototype.project.WeSync.chord.ChordConnectionManager;

/**
 * Created by robertjordanebdalin on 8/28/14.
 */
public class CreatePasswordActivity extends Activity {
	
    Button host;
   EditText pass;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      requestWindowFeature(Window.FEATURE_NO_TITLE);
       getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.create_password_activity);

        host = (Button) findViewById(R.id.host);
        TextView txt = (TextView) findViewById(R.id.textView);
        TextView txta = (TextView) findViewById(R.id.host);
        Typeface font = Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf");
        txt.setTypeface(font);
        txta.setTypeface(font);


        pass = (EditText) findViewById(R.id.createPass);

		CWifiConnectivity wifiConnect = new CWifiConnectivity(
				this.getApplicationContext());

		wifiConnect.turnOnOffHotspot(true);
        
        host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	ChordConnectionManager.getInstance().setChannelPass(pass.getText().toString());
                Intent shareHost = new Intent (getApplicationContext(), HostOptionsActivity.class);
                startActivity(shareHost);
                finish();
            }
        });

      
        
    }
    
    
}