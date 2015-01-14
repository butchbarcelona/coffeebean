package com.ust.thesis.prototype.project.WeSync;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ust.thesis.prototype.project.WeSync.chord.ChordConnectionManager;

/**
 * Created by robertjordanebdalin on 8/28/14.
 */
public class HostOptionsActivity extends Activity implements OnClickListener{

    Button whiteboard, picture, video, document, music, survey;
    ListView mDrawerList;
    static ArrayAdapter<String> drawerListAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
       getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
              WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.host_options_activity);
       
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        drawerListAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1
                , ChordConnectionManager.getInstance().getArrMembers() );
        // Set the adapter for the list view
        mDrawerList.setAdapter(drawerListAdapter);
        
        TextView txt1 = (TextView) findViewById(R.id.whiteboard);
        TextView txt2 = (TextView) findViewById(R.id.picture);
        TextView txt3 = (TextView) findViewById(R.id.music);
        TextView txt4 = (TextView) findViewById(R.id.video);
        TextView txt5 = (TextView) findViewById(R.id.document);
        TextView txt6 = (TextView) findViewById(R.id.survey);
        
        Typeface font = Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf");
       
        txt1.setTypeface(font);
        txt2.setTypeface(font);
        txt3.setTypeface(font);
        txt4.setTypeface(font);
        txt5.setTypeface(font);
        txt6.setTypeface(font);
        
        whiteboard = (Button) findViewById(R.id.whiteboard);
        picture = (Button) findViewById(R.id.picture);
        video = (Button) findViewById(R.id.video);
        document = (Button) findViewById(R.id.document);
        music = (Button) findViewById(R.id.music);
        survey = (Button) findViewById(R.id.survey);

        whiteboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent whiteboard = new Intent(getApplicationContext(),
                        WhiteboardActivity.class);
                startActivity(whiteboard);
            }
        });

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picture = new Intent(getApplicationContext(),
                        PictureActivity.class);
                startActivity(picture);
            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent video = new Intent(getApplicationContext(),
                        VideoActivity.class);
                startActivity(video);
            }
        });

        document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent document = new Intent(getApplicationContext(),
                        DocumentActivity.class);
                startActivity(document);
            }
        });

        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent music = new Intent(getApplicationContext(),
                        MusicActivity.class);
                startActivity(music);
            }
        });

        survey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent survey = new Intent(getApplicationContext(),
                        SurveyActivity.class);
                startActivity(survey);
            }
        });
        
        
        ChordConnectionManager.getInstance().initChord(this); 
    }
    
    
    public static void refreshMemberList(String member){
    	drawerListAdapter.add(member);
    	drawerListAdapter.notifyDataSetChanged();
    }
    
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	ChordConnectionManager.getInstance().stopChord();
    
    }


	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		
		}
		
	}
    
}