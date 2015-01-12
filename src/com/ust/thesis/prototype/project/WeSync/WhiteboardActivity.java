package com.ust.thesis.prototype.project.WeSync;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.chord.InvalidInterfaceException;
import com.samsung.android.sdk.chord.Schord;
import com.samsung.android.sdk.chord.SchordChannel;
import com.samsung.android.sdk.chord.SchordManager;
import com.samsung.android.sdk.chord.SchordManager.NetworkListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Cap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by robertjordanebdalin on 8/28/14.
 */
public class WhiteboardActivity extends Activity implements OnTouchListener{
	  LinearLayout linlay;
	  private Paint paint;
	     private ArrayList points;
	     private ArrayList strokes;
	     private ImageButton currPaint, pen, eraser, newBtn, saveBtn;
	     
	   	     private float smallBrush, mediumBrush, largeBrush;
	  //for doodle
	  private float x;
	    private float y;
	    DrawPanel drawingPanel;
	    Canvas c;
	    
	    @Override
	    public void onBackPressed()
	    {
	         // code here to show dialog
	         super.onBackPressed();  // optional depending on your needs
	         stopChord();
	         Intent whiteboard = new Intent(getApplicationContext(),
                     HostOptionsActivity.class);
             startActivity(whiteboard);
             finish();
	    }
	  
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
       
        Global.setColor("Red");
        setContentView(R.layout.whiteboard_activity);
        linlay = (LinearLayout) findViewById(R.id.linlay);
        
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(WhiteboardActivity.this);
 	   SharedPreferences.Editor editor = preferences.edit();
 	   editor.putString("penColor","Black");
        
        linlay = (LinearLayout)findViewById(R.id.linlay);
        drawingPanel = new DrawPanel(getApplicationContext());
        linlay.addView(drawingPanel);
        drawingPanel.setOnTouchListener(this);
        pen = (ImageButton)findViewById(R.id.pen);
        eraser = (ImageButton)findViewById(R.id.eraser);
        saveBtn = (ImageButton)findViewById(R.id.save_btn);
        

        
        pen.setOnClickListener( new OnClickListener() {
    		@SuppressLint("WrongCall")
			@Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
    			drawingPanel.setErase(false);
    			final String[] color = {"Red","Magenta","Yellow", "Green", "Blue","Cyan"};
    			AlertDialog.Builder builder = new AlertDialog.Builder(WhiteboardActivity.this);
    		    builder.setTitle("Pick pen color")
    		           .setItems(color, new DialogInterface.OnClickListener() {
    		               public void onClick(DialogInterface dialog, int which) {
    		               // The 'which' argument contains the index position
    		               // of the selected item
    		            	/*   SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(WhiteboardActivity.this);
    		            	   SharedPreferences.Editor editor = preferences.edit();
    		            	   editor.putString("penColor",color[which]);
    		            	   editor.apply();
    		            	   */
    		            	   Global.setColor(color[which]);
    		            	   System.out.println("Pen Color : "+color[which]);
    		            		   
    		           }
    		    });
    		    builder.show();
    		}
        });
        eraser.setOnClickListener( new OnClickListener() {
    		@SuppressLint("WrongCall")
			@Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
    			
    					drawingPanel.setErase(true);
    				
    					  		         
    		}
        });
        saveBtn.setOnClickListener( new OnClickListener() {
        	@SuppressLint("WrongCall")
			@Override
			public void onClick(View v) {
        	//else if(view.getId()==R.id.save_btn){
            //save drawing
    		AlertDialog.Builder saveDialog = new AlertDialog.Builder(WhiteboardActivity.this);
    		saveDialog.setTitle("Save drawing");
    		saveDialog.setMessage("Save drawing to device Gallery?");
    		saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
    		    public void onClick(DialogInterface dialog, int which){
    		        //save drawing
    		    	linlay.setDrawingCacheEnabled(true);
    		    	String imgSaved = MediaStore.Images.Media.insertImage(
    		    		    getContentResolver(), linlay.getDrawingCache(),
    		    		    UUID.randomUUID().toString()+".png", "drawing");
    		    	if(imgSaved!=null){
    		    	    Toast savedToast = Toast.makeText(getApplicationContext(), 
    		    	        "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
    		    	    savedToast.show();
    		    	}
    		    	else{
    		    	    Toast unsavedToast = Toast.makeText(getApplicationContext(), 
    		    	        "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
    		    	    unsavedToast.show();
    		    	}
    		    	linlay.destroyDrawingCache();
    		    }
    		});
    		saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
    		    public void onClick(DialogInterface dialog, int which){
    		        dialog.cancel();
    		    }
    		
    		});
    		saveDialog.show();
        	
        	
    }});
        
        
        
        
    }
    @Override
    public boolean onTouch(View v, MotionEvent mev) {
        int width = v.getWidth();
        int height = v.getHeight();
        float x = mev.getX();
        float y = mev.getY();
        String msg;
        if (x < width / 2) {
            if (y < height / 2)
                msg = "Top left quarter";
            else
                msg = "Bottom left quarter";

        } else {
            if (y < height / 2)
                msg = "Top right quarter";
            else
                msg = "Bottom right quarter";
        }
        String globalPen = Global.getColor()+"";
        double xint = (double) x / width;
        double yint = (double) y/ height;
        String shella = globalPen+","+xint+","+yint;
    	 byte[][] payload = new byte[1][];
        payload[0] = shella.getBytes();
        
        
        List<Integer> infList = mChordManager.getAvailableInterfaceTypes();
        if(infList.isEmpty()){
            System.out.println("    There is no available connection.");
            
        }else{
        
        SchordChannel channel = mChordManager.getJoinedChannel(CHORD_HELLO_TEST_CHANNEL);
        channel.sendDataToAll(CHORD_SAMPLE_MESSAGE_TYPE, payload);
        
        if(mev.getActionMasked() == MotionEvent.ACTION_UP){
        	String end = "end";
        	 payload = new byte[1][];
             payload[0] = end.getBytes();

             channel = mChordManager.getJoinedChannel(CHORD_HELLO_TEST_CHANNEL);
             channel.sendDataToAll(CHORD_SAMPLE_MESSAGE_TYPE, payload);
             	  
         }
        }
        //Toast.makeText(this, msg+"\n X - "+x+"\nY - "+y, Toast.LENGTH_SHORT).show();
        return false;
    }
    
    
    
    @Override
    public void onResume() {
        super.onResume();

        /**
         * [A] Initialize Chord!
         */
        if (mChordManager == null) {
            initChord();
        }
    }
    
    //Chord specific code, copied from BasicChordSample (Samsung Mobile SDK 1.0.3)
    private static final String CHORD_HELLO_TEST_CHANNEL = "com.samsung.android.sdk.chord.example.HELLOTESTCHANNEL";
	private static final String CHORD_SAMPLE_MESSAGE_TYPE = "com.samsung.android.sdk.chord.example.MESSAGE_TYPE";
	private SchordManager mChordManager = null;
	private int mSelectedInterface = -1;
	
    private void initChord() {

    	System.out.print("INIT CHORD");
        /****************************************************
         * 1. GetInstance
         ****************************************************/
        
        Schord chord = new Schord();        
        try {
            chord.initialize(this);
        } catch (SsdkUnsupportedException e) {
            if (e.getType() == SsdkUnsupportedException.VENDOR_NOT_SUPPORTED) {
                // Vendor is not SAMSUNG
                return;
            }
        }        
        mChordManager = new SchordManager(this);
       
        /****************************************************
         * 2. Set some values before start If you want to use secured channel,
         * you should enable SecureMode. Please refer
         * UseSecureChannelFragment.java mChordManager.enableSecureMode(true);
         * 
         *
         * Once you will use sendFile or sendMultiFiles, you have to call setTempDirectory  
         * mChordManager.setTempDirectory(Environment.getExternalStorageDirectory().getAbsolutePath()
         *       + "/Chord");
         ****************************************************/
        mChordManager.setLooper(this.getMainLooper());

        /**
         * Optional. If you need listening network changed, you can set callback
         * before starting chord.
         */
        mChordManager.setNetworkListener(new NetworkListener() {

            @Override
            public void onDisconnected(int interfaceType) {
                if (interfaceType == mSelectedInterface) {
                    Toast.makeText(WhiteboardActivity.this,
                            getInterfaceName(interfaceType) + " is disconnected",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onConnected(int interfaceType) {
                if (interfaceType == mSelectedInterface) {
                    Toast.makeText(WhiteboardActivity.this,getInterfaceName(interfaceType) + " is connected",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        //auto start connection
        startChord();

    }
    
    private String getInterfaceName(int interfaceType) {
        if (SchordManager.INTERFACE_TYPE_WIFI == interfaceType)
            return "Wi-Fi";
        else if (SchordManager.INTERFACE_TYPE_WIFI_AP == interfaceType)
            return "Mobile AP";
        else if (SchordManager.INTERFACE_TYPE_WIFI_P2P == interfaceType)
            return "Wi-Fi Direct";

        return "UNKNOWN";
    }
    
    
    private void startChord() {
        
         // 3. Start Chord using the first interface in the list of available
        // interfaces.
        
        List<Integer> infList = mChordManager.getAvailableInterfaceTypes();
        if(infList.isEmpty()){
            System.out.println("    There is no available connection.");
            return;
        }
        
        int interfaceType = 0;
        for (int interfaceValue : mChordManager.getAvailableInterfaceTypes()) {
            System.out.println("Available interface : " + interfaceValue);
            if (interfaceValue == SchordManager.INTERFACE_TYPE_WIFI) {
            	interfaceType = SchordManager.INTERFACE_TYPE_WIFI;
            	System.out.println("ChordManager.INTERFACE_TYPE_WIFI");
            	break;
            } else if (interfaceValue == SchordManager.INTERFACE_TYPE_WIFI_AP) {
            	interfaceType = SchordManager.INTERFACE_TYPE_WIFI_AP;
            	System.out.println("ChordManager.INTERFACE_TYPE_WIFI_AP");
            	break;
            } else if (interfaceValue == SchordManager.INTERFACE_TYPE_WIFI_P2P) {
            	interfaceType = SchordManager.INTERFACE_TYPE_WIFI_P2P;
            	System.out.println("ChordManager.INTERFACE_TYPE_WIFI_P2P");
            	break;
            }
        }
        
        try {
            mChordManager.start(interfaceType, mManagerListener);
            mSelectedInterface = interfaceType;
            System.out.println("    start(" + getInterfaceName(interfaceType) + ")");
        } catch (IllegalArgumentException e) {
            System.out.println("    Fail to start -" + e.getMessage());
        } catch (InvalidInterfaceException e) {
            System.out.println("    There is no such a connection.");
        } catch (Exception e) {
            System.out.println("    Fail to start -" + e.getMessage());
        }
    }
    
    /**
     * ChordManagerListener
     */
    
    SchordManager.StatusListener mManagerListener = new SchordManager.StatusListener() {

        @Override
        public void onStarted(String nodeName, int reason) {
            
            // 4. Chord has started successfully
             
            if (reason == STARTED_BY_USER) {
                // Success to start by calling start() method
                System.out.println("    >onStarted(" + nodeName + ", STARTED_BY_USER)");
                joinTestChannel();
            } else if (reason == STARTED_BY_RECONNECTION) {
                // Re-start by network re-connection.
                System.out.println("    >onStarted(" + nodeName + ", STARTED_BY_RECONNECTION)");
            }

        }

        @Override
        public void onStopped(int reason) {
            
            //  8. Chord has stopped successfully
             
            if (STOPPED_BY_USER == reason) {
                // Success to stop by calling stop() method
                System.out.println("    >onStopped(STOPPED_BY_USER)");
            } else if (NETWORK_DISCONNECTED == reason) {
                // Stopped by network disconnected
                System.out.println("    >onStopped(NETWORK_DISCONNECTED)");
            }
        }
    };
    
    private void joinTestChannel() {
        SchordChannel channel = null;
        
       //   5. Join my channel
         
        System.out.println("    joinChannel");
        channel = mChordManager.joinChannel(CHORD_HELLO_TEST_CHANNEL, mChannelListener);

        if (channel == null) {
            System.out.println("    Fail to joinChannel");
        }
    }

    private void stopChord() {
        if (mChordManager == null)
            return;

        
         // If you registered NetworkListener, you should unregister it.
         
        mChordManager.setNetworkListener(null);

        
         // 7. Stop Chord. You can call leaveChannel explicitly.
         // mChordManager.leaveChannel(CHORD_HELLO_TEST_CHANNEL);
         
        System.out.println("    stop");
        mChordManager.stop();
    }

    // ***************************************************
    // ChordChannelListener
    // ***************************************************
    private SchordChannel.StatusListener mChannelListener = new SchordChannel.StatusListener() {

        //Called when a node leave event is raised on the channel.
        
        @Override
        public void onNodeLeft(String fromNode, String fromChannel) {
            System.out.println("    >onNodeLeft(" + fromNode + ")");
        }

        //Called when a node join event is raised on the channel
        
        @Override
        public void onNodeJoined(String fromNode, String fromChannel) {
            System.out.println("    >onNodeJoined(" + fromNode + ")");

            //6. Send data to joined node
           
            System.out.println( "onNodeJoined node="+fromNode+" channel="+fromChannel+" bJoined=true");
            Toast.makeText(WhiteboardActivity.this,"Connected",Toast.LENGTH_SHORT).show();
            //Toast.makeText(SurveyActivity.this,"onNodeJoined node="+fromNode+" channel="+fromChannel+" bJoined=true",Toast.LENGTH_SHORT).show();
    		
			//sendState(mIamReadyCheckbox.isChecked());
			//sendState(true);
		
        }

        //Called when the data message received from the node.
         
        @Override
        public void onDataReceived(String fromNode, String fromChannel, String payloadType,
                byte[][] payload) {
            //6. Received data from other node
             
        	String message = new String(payload[0]);
        	System.out.println("    >onDataReceived(" + fromNode + ", " + message + ")");
            if(!payloadType.equals(CHORD_SAMPLE_MESSAGE_TYPE)){
                return;
            }
        	//10-07 23:48:59.377: I/System.out(17328):     >onDataReceived(hWvaa*7avw#3F36A, Sampletext to send)

           if (message.equals("end")){
        	   drawingPanel.EndDrawFromOther();
           }
            else{
            	String[] msg = message.split(",");
            	Display v = getWindowManager().getDefaultDisplay();
            	int width = v.getWidth();
                int height = v.getHeight();
               
                drawingPanel.DrawFromOther(msg[0],Double.parseDouble(msg[1])*width,Double.parseDouble(msg[2])*height);
            }
 
        }
        
       // The following callBacks are not used in this Fragment. Please refer
         // to the SendFilesFragment.java
         
        @Override
        public void onMultiFilesWillReceive(String fromNode, String fromChannel, String fileName,
                String taskId, int totalCount, String fileType, long fileSize) {

        }

        @Override
        public void onMultiFilesSent(String toNode, String toChannel, String fileName,
                String taskId, int index, String fileType) {

        }

        @Override
        public void onMultiFilesReceived(String fromNode, String fromChannel, String fileName,
                String taskId, int index, String fileType, long fileSize, String tmpFilePath) {

        }

        @Override
        public void onMultiFilesFinished(String node, String channel, String taskId, int reason) {

        }

        @Override
        public void onMultiFilesFailed(String node, String channel, String fileName, String taskId,
                int index, int reason) {

        }

        @Override
        public void onMultiFilesChunkSent(String toNode, String toChannel, String fileName,
                String taskId, int index, String fileType, long fileSize, long offset,
                long chunkSize) {

        }

        @Override
        public void onMultiFilesChunkReceived(String fromNode, String fromChannel, String fileName,
                String taskId, int index, String fileType, long fileSize, long offset) {

        }

        @Override
        public void onFileWillReceive(String fromNode, String fromChannel, String fileName,
                String hash, String fileType, String exchangeId, long fileSize) {

        }

        @Override
        public void onFileSent(String toNode, String toChannel, String fileName, String hash,
                String fileType, String exchangeId) {

        }

        @Override
        public void onFileReceived(String fromNode, String fromChannel, String fileName,
                String hash, String fileType, String exchangeId, long fileSize, String tmpFilePath) {

        }

        @Override
        public void onFileFailed(String node, String channel, String fileName, String hash,
                String exchangeId, int reason) {

        }

        @Override
        public void onFileChunkSent(String toNode, String toChannel, String fileName, String hash,
                String fileType, String exchangeId, long fileSize, long offset, long chunkSize) {

        }

        @Override
        public void onFileChunkReceived(String fromNode, String fromChannel, String fileName,
                String hash, String fileType, String exchangeId, long fileSize, long offset) {

        }

    };
    
    
}