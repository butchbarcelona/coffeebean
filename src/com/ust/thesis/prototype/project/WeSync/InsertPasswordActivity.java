package com.ust.thesis.prototype.project.WeSync;

import java.util.List;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.chord.InvalidInterfaceException;
import com.samsung.android.sdk.chord.Schord;
import com.samsung.android.sdk.chord.SchordChannel;
import com.samsung.android.sdk.chord.SchordManager;
import com.samsung.android.sdk.chord.SchordManager.NetworkListener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by robertjordanebdalin on 8/28/14.
 */
public class InsertPasswordActivity extends Activity {

    Button member;
    EditText player;
    TextView playertextview;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       requestWindowFeature(Window.FEATURE_NO_TITLE);
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.insert_password_activity);

        member = (Button) findViewById(R.id.member);
        player = (EditText) findViewById(R.id.player);
        playertextview = (TextView)findViewById(R.id.playertextview);
        
        player.setVisibility(View.VISIBLE);
        playertextview.setVisibility(View.VISIBLE);
        TextView txt = (TextView) findViewById(R.id.member);
        TextView txta = (TextView) findViewById(R.id.player);
        TextView txtb = (TextView) findViewById(R.id.playertextview);
        TextView txtc = (TextView) findViewById(R.id.textView);
        Typeface font = Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf");
        txt.setTypeface(font);
        txta.setTypeface(font);
        txtb.setTypeface(font);
        txtc.setTypeface(font);
        
        
        member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	Global.setPlayerName(player.getText().toString());
                Intent shareHost = new Intent (getApplicationContext(), HostOptionsActivity.class);
               startActivity(shareHost);
                finish();
            }
        });


    }
    
    
}