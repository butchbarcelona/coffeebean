package com.ust.thesis.prototype.project.WeSync;

import java.util.List;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.chord.InvalidInterfaceException;
import com.samsung.android.sdk.chord.Schord;
import com.samsung.android.sdk.chord.SchordChannel;
import com.samsung.android.sdk.chord.SchordManager;
import com.samsung.android.sdk.chord.SchordManager.NetworkListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by robertjordanebdalin on 8/28/14.
 */
public class CreatePasswordActivity extends Activity {
	
    Button host;
   

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

        host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	
                Intent shareHost = new Intent (getApplicationContext(), HostOptionsActivity.class);
                startActivity(shareHost);
            }
        });

      
        
    }
    
    
}