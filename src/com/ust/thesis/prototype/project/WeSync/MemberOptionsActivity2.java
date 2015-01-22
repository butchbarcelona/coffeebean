package com.ust.thesis.prototype.project.WeSync;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

/**
 * Created by robertjordanebdalin on 8/28/14.
 */
public class MemberOptionsActivity2 extends Activity {

    Button whiteboard, picture, video, document, music, survey;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN); 
        setContentView(R.layout.member_options_activity);

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
                        MusicActivity___.class);
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
    }
}