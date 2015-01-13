package com.ust.thesis.prototype.project.WeSync.chord;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.chord.InvalidInterfaceException;
import com.samsung.android.sdk.chord.Schord;
import com.samsung.android.sdk.chord.SchordChannel;
import com.samsung.android.sdk.chord.SchordManager;
import com.samsung.android.sdk.chord.SchordManager.NetworkListener;
import com.ust.thesis.prototype.project.WeSync.MusicActivity;
import com.ust.thesis.prototype.project.WeSync.PictureActivity;
import com.ust.thesis.prototype.project.WeSync.VideoActivity;
import com.ust.thesis.prototype.project.WeSync.WhiteboardActivity;

public class ChordConnectionManager {

	private static ChordConnectionManager instance = null;

	private ChordConnectionManager() {
	}

	public static ChordConnectionManager getInstance(){	
		if(instance == null)
			instance = new ChordConnectionManager();

		return instance;
	}

	public static SchordManager mChordManager = null;
	private int mSelectedInterface = -1;

	//Chord specific code, copied from BasicChordSample (Samsung Mobile SDK 1.0.3)
	private static final String CHORD_HELLO_TEST_CHANNEL = "com.samsung.android.sdk.chord.example.HELLOTESTCHANNEL"; //TODO: change to password of server
	private static final String CHORD_SAMPLE_MESSAGE_TYPE = "com.samsung.android.sdk.chord.example.MESSAGE_TYPE";
	private Context ctx;


	public void sendData(byte[][] payload, SyncMessageType type){
		try{
		SchordChannel channel = mChordManager.getJoinedChannel(CHORD_HELLO_TEST_CHANNEL);
		channel.sendDataToAll(type.getString(), payload);
		}catch(NullPointerException e){
			Log.d("coffeebean", e.getLocalizedMessage());
		}
	}

	public void initChord(final Context ctx) {
		this.ctx = ctx;

		Log.d("coffeebean","INIT CHORD");
		/****************************************************
		 * 1. GetInstance
		 ****************************************************/

		Schord chord = new Schord();        
		try {
			chord.initialize(ctx);

		} catch (SsdkUnsupportedException e) {
			if (e.getType() == SsdkUnsupportedException.VENDOR_NOT_SUPPORTED) {
				// Vendor is not SAMSUNG
				return;
			}
		}        
		mChordManager = new SchordManager(ctx);
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
		mChordManager.setLooper(ctx.getMainLooper());

		/**
		 * Optional. If you need listening network changed, you can set callback
		 * before starting chord.
		 */
		mChordManager.setNetworkListener(new NetworkListener() {

			@Override
			public void onDisconnected(int interfaceType) {
				if (interfaceType == mSelectedInterface) {
					Toast.makeText(ctx,
							getInterfaceName(interfaceType) + " is disconnected",
							Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onConnected(int interfaceType) {
				if (interfaceType == mSelectedInterface) {
					Toast.makeText(ctx,getInterfaceName(interfaceType) + " is connected",
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
			Log.d("butch","ChordConnectionManager:"+"    There is no available connection.");
			return;
		}

		int interfaceType = 0;
		for (int interfaceValue : mChordManager.getAvailableInterfaceTypes()) {
			Log.d("butch","ChordConnectionManager:"+"Available interface : " + interfaceValue);
			if (interfaceValue == SchordManager.INTERFACE_TYPE_WIFI) {
				interfaceType = SchordManager.INTERFACE_TYPE_WIFI;
				Log.d("butch","ChordConnectionManager:"+"ChordManager.INTERFACE_TYPE_WIFI");
				break;
			} else if (interfaceValue == SchordManager.INTERFACE_TYPE_WIFI_AP) {
				interfaceType = SchordManager.INTERFACE_TYPE_WIFI_AP;
				Log.d("butch","ChordConnectionManager:"+"ChordManager.INTERFACE_TYPE_WIFI_AP");
				break;
			} else if (interfaceValue == SchordManager.INTERFACE_TYPE_WIFI_P2P) {
				interfaceType = SchordManager.INTERFACE_TYPE_WIFI_P2P;
				Log.d("butch","ChordConnectionManager:"+"ChordManager.INTERFACE_TYPE_WIFI_P2P");
				break;
			}
		}

		try {
			mChordManager.start(interfaceType, mManagerListener);
			mSelectedInterface = interfaceType;
			Log.d("butch","ChordConnectionManager:"+"    start(" + getInterfaceName(interfaceType) + ")");
		} catch (IllegalArgumentException e) {
			Log.d("butch","ChordConnectionManager:"+"    Fail to start -" + e.getMessage());
		} catch (InvalidInterfaceException e) {
			Log.d("butch","ChordConnectionManager:"+"    There is no such a connection.");
		} catch (Exception e) {
			Log.d("butch","ChordConnectionManager:"+"    Fail to start -" + e.getMessage());
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
				Log.d("butch","ChordConnectionManager:"+"    >onStarted(" + nodeName + ", STARTED_BY_USER)");
				joinTestChannel();
			} else if (reason == STARTED_BY_RECONNECTION) {
				// Re-start by network re-connection.
				Log.d("butch","ChordConnectionManager:"+"    >onStarted(" + nodeName + ", STARTED_BY_RECONNECTION)");
			}


		}

		@Override
		public void onStopped(int reason) {

			//  8. Chord has stopped successfully

			if (STOPPED_BY_USER == reason) {
				// Success to stop by calling stop() method
				Log.d("butch","ChordConnectionManager:"+"    >onStopped(STOPPED_BY_USER)");
			} else if (NETWORK_DISCONNECTED == reason) {
				// Stopped by network disconnected
				Log.d("butch","ChordConnectionManager:"+"    >onStopped(NETWORK_DISCONNECTED)");
			}
		}
	};

	private void joinTestChannel() {
		SchordChannel channel = null;

		//   5. Join my channel

		Log.d("butch","ChordConnectionManager:"+"    joinChannel");
		channel = mChordManager.joinChannel(CHORD_HELLO_TEST_CHANNEL, mChannelListener);

		if (channel == null) {
			Log.d("butch","ChordConnectionManager:"+"    Fail to joinChannel");
		}
	}

	public void stopChord() {
		if (mChordManager == null)
			return;


		// If you registered NetworkListener, you should unregister it.

		mChordManager.setNetworkListener(null);


		// 7. Stop Chord. You can call leaveChannel explicitly.
		// mChordManager.leaveChannel(CHORD_HELLO_TEST_CHANNEL);

		Log.d("butch","ChordConnectionManager:"+"    stop");
		mChordManager.stop();
	}

	// ***************************************************
	// ChordChannelListener
	// ***************************************************
	private SchordChannel.StatusListener mChannelListener = new SchordChannel.StatusListener() {

		//Called when a node leave event is raised on the channel.

		@Override
		public void onNodeLeft(String fromNode, String fromChannel) {
			Log.d("butch","ChordConnectionManager:"+"    >onNodeLeft(" + fromNode + ")");
		}

		//Called when a node join event is raised on the channel

		@Override
		public void onNodeJoined(String fromNode, String fromChannel) {
			Log.d("butch","ChordConnectionManager:"+"    >onNodeJoined(" + fromNode + ")");

			//6. Send data to joined node

			Log.d("butch","ChordConnectionManager:"+ "onNodeJoined node="+fromNode+" channel="+fromChannel+" bJoined=true");
			Toast.makeText(ctx,"Connected",Toast.LENGTH_SHORT).show();


		}


		@Override
		public void onDataReceived(String fromNode, String fromChannel, String payloadType,
				byte[][] payload) {
		
			
			switch(SyncMessageType.getSyncType(payloadType)){
				case CHORD_HELLO_TEST_CHANNEL:
					break;
					
				case CHORD_SAMPLE_MESSAGE_TYPE:
					break;
				case MUSIC_PLAY:
					Toast.makeText(ctx, "MUSIC!", Toast.LENGTH_SHORT).show();
					MusicActivity.playMp3(payload[0]);
					break;
				case SHOW_DOCUMENT:
					break;
				case SHOW_PICTURE:
					Toast.makeText(ctx, "PICTURE!", Toast.LENGTH_SHORT).show();
					Bitmap bmp = BitmapFactory.decodeByteArray(payload[0], 0, payload[0].length);
					PictureActivity.setImage(bmp);
					break;
				case VIDEO_PLAY:
					Toast.makeText(ctx, "VIDEO!", Toast.LENGTH_SHORT).show();
					VideoActivity.playVideo(payload);
					break;
				case WHITEBOARD:
					Toast.makeText(ctx, "WHITEBOARD!", Toast.LENGTH_SHORT).show();
					WhiteboardActivity.drawBoard(payload);
					break;
				default:
					break;
			
			}
		}


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
			Toast.makeText(ctx, "File :" + fileName + "successfully sent", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onFileReceived(String fromNode, String fromChannel, String fileName,
				String hash, String fileType, String exchangeId, long fileSize, String tmpFilePath) {
			Toast.makeText(ctx, "File :" + fileName + "successfully received", Toast.LENGTH_LONG).show();

		}

		@Override
		public void onFileFailed(String node, String channel, String fileName, String hash,
				String exchangeId, int reason) {
			Toast.makeText(ctx, "File transfer failed.", Toast.LENGTH_LONG).show();

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
