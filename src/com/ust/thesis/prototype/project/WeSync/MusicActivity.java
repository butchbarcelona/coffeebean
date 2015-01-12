package com.ust.thesis.prototype.project.WeSync;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Window;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.chord.InvalidInterfaceException;
import com.samsung.android.sdk.chord.Schord;
import com.samsung.android.sdk.chord.SchordChannel;
import com.samsung.android.sdk.chord.SchordManager;
import com.samsung.android.sdk.chord.SchordManager.NetworkListener;
import com.ust.thesis.prototype.project.WeSync.ExplorerChordMessage.MessageType;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Cap;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by robertjordanebdalin on 8/28/14.
 */
public class MusicActivity extends Activity {
	 Button button1;
	 ImageView playmusic;
	 MediaPlayer mp;
	 String[] musicname;
	 String[] musicpath;
	 String mname="",mpath="";
	 int chosen;
	 AlertDialog levelDialog;
	 int countfile=0;
	 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.music_activity);
        
        walkdir(Environment.getExternalStorageDirectory());
        
                button1 = (Button)findViewById(R.id.button1);
                playmusic = (ImageView)findViewById(R.id.playmusic);
                
                playmusic.setOnClickListener( new OnClickListener() {
            		@SuppressLint("WrongCall")
        			@Override
                    public void onClick(View v) {
            			 // Play audio
            			if (!(button1.getText().equals("Select Music"))){
            			mp = new MediaPlayer();
                        
                        
                     // Set data source -
                     try {
                    	 String filePath = musicpath[chosen];
                    	 File file = new File(filePath);
                    	 if (file.exists()) System.out.println("EXIST");
                    	 else System.out.println("DO NOT EXIST : "+musicpath[chosen]);
                    	 mp.setDataSource(filePath);
        				mp.prepare();
        				mp.start();
        				
        				 
                        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                        
                        InputStream is = new FileInputStream(file);
                        byte[] temp = new byte[1024];
                        int read;

                        while((read = is.read(temp)) >= 0){
                           buffer.write(temp, 0, read);
                        }
                                   
                        byte[] data = buffer.toByteArray();
                        
            			 byte[][] payload = new byte[1][];
            	        payload[0] = data;

            	        SchordChannel channel = mChordManager.getJoinedChannel(CHORD_HELLO_TEST_CHANNEL);
            	        channel.sendDataToAll(CHORD_SAMPLE_MESSAGE_TYPE, payload);
            	        
        			
        			} catch (IllegalArgumentException e) {
        				System.out.println("Error : "+e);
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			} catch (SecurityException e) {
        				System.out.println("Error 1: "+e);
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			} catch (IllegalStateException e) {
        				System.out.println("Error 2: "+e);
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			} catch (IOException e) {
        				System.out.println("Error 3: "+e);
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}  
            		
            		}else Toast.makeText(MusicActivity.this,"NO MUSIC SELECTED",Toast.LENGTH_SHORT).show();
            		}});

            
    
                
                button1.setOnClickListener( new OnClickListener() {
            		@SuppressLint("WrongCall")
        			@Override
                    public void onClick(View v) {
            		
            			
            			String[] tempname = mname.split("%%%%%%");
            			String[] temppath = mpath.split("%%%%%%");
            			
            			musicname= new String [tempname.length-1];
            			musicpath= new String [tempname.length-1];
            			
            			for(int i = 0;i<tempname.length-1;i++){
            				musicname[i] = tempname[i+1];
            				musicpath[i] = temppath[i+1];
            			}
            			
            			if (tempname.length>1){
            			            
            			                // Creating and Building the Dialog 
            			                AlertDialog.Builder builder = new AlertDialog.Builder(MusicActivity.this);
            			                builder.setTitle("Choose music file");
            			                builder.setSingleChoiceItems(musicname, -1, new DialogInterface.OnClickListener() {
            			                public void onClick(DialogInterface dialog, int item) {
            			                   button1.setText(musicname[item]);
            			                   chosen = item;
            			                    levelDialog.dismiss();    
            			                    }
            			                });
            			                levelDialog = builder.create();
            			                levelDialog.show();

            			}else {
            				Toast.makeText(MusicActivity.this,"NO MUSIC FOUND",
                                    Toast.LENGTH_SHORT).show();
            			}
            		}});

            }
    
    public void walkdir(File dir) {
    	
        String pdfPattern = ".mp3";

        File[] listFile = dir.listFiles();
        
        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    walkdir(listFile[i]);
                	//textView1.setText("Directory: "+listFile[i]);
                } else {
                  if (listFile[i].getName().endsWith(pdfPattern)){
                                  //Do what ever u want
                	  mname = mname+"%%%%%%"+listFile[i].getName();
                	  mpath = mpath+"%%%%%%"+listFile[i].getPath();
                	  System.out.println(mname);
                	  System.out.println(mpath);
                	  System.out.println("Found : "+listFile[i].getName());
                	  System.out.println("Found path : "+listFile[i].getPath());
                	  
                   }
                }
            }
        }

            
        
      
    }
          

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
            
            private void sendMessage(String nodeName, ExplorerChordMessage message) {
            	SchordChannel channel = mChordManager.getJoinedChannel(CHORD_HELLO_TEST_CHANNEL);
                
                channel.sendData(nodeName, CHORD_SAMPLE_MESSAGE_TYPE, new byte[][] { message.getBytes() });
                
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
                mChordManager.setTempDirectory(Environment.getExternalStorageDirectory() + "/RemoteFileExplorer");
               
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
                            Toast.makeText(MusicActivity.this,
                                    getInterfaceName(interfaceType) + " is disconnected",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onConnected(int interfaceType) {
                        if (interfaceType == mSelectedInterface) {
                            Toast.makeText(MusicActivity.this,getInterfaceName(interfaceType) + " is connected",
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
                    Toast.makeText(MusicActivity.this,"Connected",Toast.LENGTH_SHORT).show();
                    
                    
                }
                
               

                //Called when the data message received from the node.
                 
                @Override
                public void onDataReceived(String fromNode, String fromChannel, String payloadType,
                        byte[][] payload) {
                    //6. Received data from other node
                     byte[] byteArray = payload[0];
                     Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                	String message = new String(payload[0]);
                	System.out.println("    >onDataReceived(" + fromNode + ", " + message + ")");
                    if(!payloadType.equals(CHORD_SAMPLE_MESSAGE_TYPE)){
                        return;
                    }
                    playMp3(byteArray);
                    // imageView1.setImageBitmap(bmp);
                	//10-07 23:48:59.377: I/System.out(17328):     >onDataReceived(hWvaa*7avw#3F36A, Sampletext to send)
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
                	 Toast.makeText(MusicActivity.this, "File :" + fileName + "successfully sent", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFileReceived(String fromNode, String fromChannel, String fileName,
                        String hash, String fileType, String exchangeId, long fileSize, String tmpFilePath) {
                	 Toast.makeText(MusicActivity.this, "File :" + fileName + "successfully received", Toast.LENGTH_LONG).show();
                     
                }

                @Override
                public void onFileFailed(String node, String channel, String fileName, String hash,
                        String exchangeId, int reason) {
                	 Toast.makeText(MusicActivity.this, "File transfer failed.", Toast.LENGTH_LONG).show();
                     
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
            
            private void playMp3(byte[] mp3SoundByteArray) {
                try {
                    // create temp file that will hold byte array
                    File tempMp3 = File.createTempFile("kurchina", "mp3", getCacheDir());
                    tempMp3.deleteOnExit();
                    FileOutputStream fos = new FileOutputStream(tempMp3);
                    fos.write(mp3SoundByteArray);
                    fos.close();

                    // Tried reusing instance of media player
                    // but that resulted in system crashes...  
                    MediaPlayer mediaPlayer = new MediaPlayer();

                    // Tried passing path directly, but kept getting 
                    // "Prepare failed.: status=0x1"
                    // so using file descriptor instead
                    FileInputStream fis = new FileInputStream(tempMp3);
                    mediaPlayer.setDataSource(fis.getFD());

                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException ex) {
                    String s = ex.toString();
                    ex.printStackTrace();
                }
            }
            
         
            
        }