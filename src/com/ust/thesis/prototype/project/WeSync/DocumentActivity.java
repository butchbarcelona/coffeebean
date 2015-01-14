package com.ust.thesis.prototype.project.WeSync;

import com.ust.thesis.prototype.project.WeSync.chord.ChordConnectionManager;
import com.ust.thesis.prototype.project.WeSync.chord.ChordMessageType;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by robertjordanebdalin on 8/28/14.
 */
public class DocumentActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      requestWindowFeature(Window.FEATURE_NO_TITLE);
       getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.document_activity);
        
        
        

        //TODO: chord
       //ChordConnectionManager.getInstance().sendData(payload, SyncMessageType.SHOW_DOCUMENT);
    }
}