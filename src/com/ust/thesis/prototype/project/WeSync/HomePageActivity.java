package com.ust.thesis.prototype.project.WeSync;

import com.ust.thesis.prototype.project.WeSync.chord.ChordConnectionManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


public class HomePageActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {

        Button create, join;
        
        
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
       getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.home_page_activity);

        join = (Button) findViewById(R.id.join);
        create = (Button) findViewById(R.id.create);
        TextView txt = (TextView) findViewById(R.id.join);
        TextView txta = (TextView) findViewById(R.id.create);
        Typeface font = Typeface.createFromAsset(getAssets(), "BuxtonSketch.ttf");
        txt.setTypeface(font);
        txta.setTypeface(font);

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
       
            	ChordConnectionManager.getInstance().isHost = false;
                Intent insertPass = new Intent(getApplicationContext(),
                        InsertPasswordActivity.class);
                startActivity(insertPass);
                finish();
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	ChordConnectionManager.getInstance().isHost = true;
            	
                Intent createPass = new Intent(getApplicationContext(),
                        CreatePasswordActivity.class);
                startActivity(createPass);
                finish();
            }
        });
    }
}