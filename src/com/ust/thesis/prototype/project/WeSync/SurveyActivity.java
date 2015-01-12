package com.ust.thesis.prototype.project.WeSync;

import java.util.ArrayList;
import java.util.List;

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
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by robertjordanebdalin on 8/28/14.
 */
public class SurveyActivity extends Activity {
	EditText questions;
	TextView questiontextView;
	TextView deviceanswer,devicename;
	Button addPolls,broadcast;
	ListView lv;
	String clickanser="";
	String answers;
    ArrayList<String> al;
    ArrayAdapter<String> aa;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN); 

        setContentView(R.layout.survey_activity);
        questions = (EditText)findViewById(R.id.question);
        questiontextView = (TextView)findViewById(R.id.questiontextView);
        devicename = (TextView)findViewById(R.id.devicename);
        deviceanswer = (TextView)findViewById(R.id.deviceanswer);
        
        questions.setVisibility(View.VISIBLE);
        questiontextView.setVisibility(View.GONE); 
        devicename.setVisibility(View.GONE);
        deviceanswer.setVisibility(View.GONE);
        
        TextView txt = (TextView) findViewById(R.id.addPolls);
        TextView txta = (TextView) findViewById(R.id.broadcast);
        Typeface font = Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf");
        txt.setTypeface(font);
        txta.setTypeface(font);
        
       
        addPolls = (Button)findViewById(R.id.addPolls);
        broadcast = (Button)findViewById(R.id.broadcast);
        broadcast.setOnClickListener( new OnClickListener() {
            		@SuppressLint("WrongCall")
        			@Override
                    public void onClick(View v) {
            			List<Integer> infList = mChordManager.getAvailableInterfaceTypes();
            	        if(infList.isEmpty()){
            	            System.out.println("    There is no available connection.");
            	            Toast.makeText(SurveyActivity.this,"NO CONNECTION AVAILABLE",Toast.LENGTH_SHORT).show();
            	        }else{
            			if(addPolls.getText().toString().equals("Add Polls")){
            				broadcast.setVisibility(View.GONE);
                			addPolls.setVisibility(View.GONE);
                			lv.setVisibility(View.GONE);
                			questions.setVisibility(View.GONE);
                			questiontextView.setVisibility(View.VISIBLE);
                			questiontextView.setText("Waiting for answers. Please wait...");
            			addPolls.setText("Waiting for answers");
            			addPolls.setClickable(false);
            			 String ques = questions.getText().toString();
            			 String ans = answers.replace("null^", "");
            			 ques = ques+"^^^"+ans;
            	    	 byte[][] payload = new byte[1][];
            	        payload[0] = ques.getBytes();

            	        SchordChannel channel = mChordManager.getJoinedChannel(CHORD_HELLO_TEST_CHANNEL);
            	        channel.sendDataToAll(CHORD_SAMPLE_MESSAGE_TYPE, payload);
            	      
            			}
            		else{
            			broadcast.setVisibility(View.GONE);
            			addPolls.setVisibility(View.GONE);
            			lv.setVisibility(View.GONE);
            			questions.setVisibility(View.GONE);
            			questiontextView.setVisibility(View.VISIBLE);
            			questiontextView.setText("Successfully submitted your answer.");
            			 String devinfo = Global.getPlayerName()+"<>"+clickanser;
            			 byte[][] payload = new byte[1][];
            	        payload[0] = devinfo.getBytes();

            	        SchordChannel channel = mChordManager.getJoinedChannel(CHORD_HELLO_TEST_CHANNEL);
            	        channel.sendDataToAll(CHORD_SAMPLE_MESSAGE_TYPE, payload);
            		}
            	        }
            	}
         });
        
        lv = (ListView) findViewById(R.id.listView_items);
        al = new ArrayList<String>();//initialize array list
        al = new ArrayList<String>();
        al.clear();
        
        aa = new ArrayAdapter<String>(this, 
                android.R.layout.simple_list_item_1, 
                al);//step4 : establish communication bw arraylist and adapter
        //step5 : establish communication bw adapter and dest (listview)
        lv.setAdapter(aa);
        
        addPolls.setOnClickListener( new OnClickListener() {
    		@SuppressLint("WrongCall")
			@Override
            public void onClick(View v) {
    			
    			AlertDialog.Builder alert = new AlertDialog.Builder(SurveyActivity.this);

    			alert.setTitle("Survey");
    			alert.setMessage("Write Option: ");

    			// Set an EditText view to get user input 
    			final EditText input = new EditText(SurveyActivity.this);
    			alert.setView(input);

    			alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
    			    public void onClick(DialogInterface dialog, int whichButton) {
    			        Editable value = input.getText();
    			        // Do something with value!
    			        
    	                al.add(0, value.toString());
    	                //step ii: notify to adapter
    	                aa.notifyDataSetChanged();
    	                //step iii: clr edit text
    	                answers=answers+"^"+value;
    	                System.out.println("Answers : "+answers);
    			    }
    			});

    			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    			    public void onClick(DialogInterface dialog, int whichButton) {
    			    // Canceled.
    			    }
    			});

    			alert.show();
    			
    	        
    			
               }
 });

        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
            	Object o = lv.getItemAtPosition(position);
            	if(addPolls.getText().toString().equals("Add Polls")){
                    
                }else {
                	clickanser = o.toString();
                	Toast.makeText(SurveyActivity.this,"You answered "+clickanser,Toast.LENGTH_SHORT).show();
                	broadcast.setText("Send Answer : "+clickanser);
                	
                }
            }
        });
        
        
           
       
        
        
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
private static final String CHORD_HELLO_TEST_CHANNEL = "com.samsung.android.sdk.chord.example.SURVEYTESTCHANNEL";
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
                Toast.makeText(SurveyActivity.this,
                        getInterfaceName(interfaceType) + " is disconnected",
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onConnected(int interfaceType) {
            if (interfaceType == mSelectedInterface) {
                Toast.makeText(SurveyActivity.this,getInterfaceName(interfaceType) + " is connected",
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
        Toast.makeText(SurveyActivity.this,"Connected",Toast.LENGTH_SHORT).show();
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
        //eto tanong ko^^^una^pang2^0pgff)

        if(addPolls.getText().toString().equals("Add Polls")){
        message = message.replace("^^^", "<>");
        String[] splitmessage = message.split("<>");
        
        splitmessage[1] = splitmessage[1].replace("^","<>");
        String[] splitans = splitmessage[1].split("<>");
        for(int i = 0;i<splitans.length;i++){
        	al.add(0, splitans[i]);
            //step ii: notify to adapter
            aa.notifyDataSetChanged();
        }
   
       addPolls.setVisibility(View.GONE);
       questiontextView.setVisibility(View.VISIBLE);
       questiontextView.setText(splitmessage[0]);
       questions.setVisibility(View.GONE); 
       broadcast.setText("Send Answer");
       addPolls.setText("Send ");
       }else{// if(addPolls.getText().toString().equals("Send Answer")){
    	 String newstring = devicename.getText().toString().replace("Player Name\n", "");
    	 String newans = deviceanswer.getText().toString().replace("Player Answer\n", "");
    	 devicename.setText(newstring);
    	 deviceanswer.setText(newans);
    	 deviceanswer.setVisibility(View.VISIBLE);
    	 devicename.setVisibility(View.VISIBLE);
    	 broadcast.setVisibility(View.GONE);
    	 addPolls.setVisibility(View.GONE); 
    	 questions.setVisibility(View.GONE);
    	 String[] splitans = message.split("<>");
    	 devicename.setText("Player Name\n"+devicename.getText().toString()+"\n"+splitans[0]);
    	 deviceanswer.setText("Player Answer\n"+deviceanswer.getText().toString()+"\n"+splitans[1]);
    	 lv.setVisibility(View.GONE);
    	 questiontextView.setVisibility(View.GONE);
    	 
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
