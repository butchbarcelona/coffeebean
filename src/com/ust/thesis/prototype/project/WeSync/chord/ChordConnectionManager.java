package com.ust.thesis.prototype.project.WeSync.chord;

import java.util.HashMap;
import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.chord.InvalidInterfaceException;
import com.samsung.android.sdk.chord.Schord;
import com.samsung.android.sdk.chord.SchordChannel;
import com.samsung.android.sdk.chord.SchordManager;
import com.samsung.android.sdk.chord.SchordManager.NetworkListener;
import com.ust.thesis.prototype.project.WeSync.DocumentActivity;
import com.ust.thesis.prototype.project.WeSync.Global;
import com.ust.thesis.prototype.project.WeSync.HostOptionsActivity;
import com.ust.thesis.prototype.project.WeSync.MusicActivity;
import com.ust.thesis.prototype.project.WeSync.PictureActivity;
import com.ust.thesis.prototype.project.WeSync.SurveyActivity;
import com.ust.thesis.prototype.project.WeSync.VideoActivity;
import com.ust.thesis.prototype.project.WeSync.WhiteboardActivity;

public class ChordConnectionManager {

	private static ChordConnectionManager instance = null;
	public static ChordManagerState chordState = ChordManagerState.STOP;
	public HashMap<String, RoomType> members;
	public HashMap<String, String> nodes;
	public HashMap<String, Boolean> receivedCallback;

	public boolean isHost = false;

	private ChordConnectionManager() {
		members = new HashMap<String, RoomType>();
		nodes = new HashMap<String, String>();

	}

	public static ChordConnectionManager getInstance() {
		if (instance == null)
			instance = new ChordConnectionManager();

		return instance;
	}

	public static SchordManager mChordManager = null;
	private int mSelectedInterface = -1;
	private WifiManager wifiManager;

	public HashMap<String, RoomType> getMembersRooms() {
		return members;
	}

	public String[] getMembersNames() {
		return (String[]) nodes.entrySet().toArray();
	}

	// Chord specific code, copied from BasicChordSample (Samsung Mobile SDK
	// 1.0.3)
	private static final String CHORD_HELLO_TEST_CHANNEL = "test channel"; // TODO:
																			// change
																			// to
																			// password
																			// of
																			// server
	private Context ctx;

	private static String channelPass = CHORD_HELLO_TEST_CHANNEL;

	public void sendData(byte[][] payload, ChordMessageType type) {
		try {
			SchordChannel channel = mChordManager
					.getJoinedChannel(getChannelPass());
			channel.sendDataToAll(type.getString(), payload);
		} catch (NullPointerException e) {
			Log.d("coffeebean", "error null");
		}
	}

	public void sendData(String fromNode, byte[][] payload,
			ChordMessageType type) {
		try {
			SchordChannel channel = mChordManager
					.getJoinedChannel(getChannelPass());
			channel.sendData(fromNode, type.getString(), payload);
		} catch (NullPointerException e) {
			Log.d("coffeebean", "error null");
		}
	}

	public void initChord(final Context ctx) {
		this.ctx = ctx;

		chordState = ChordManagerState.WIFI_SCAN;

		// TODO: manage wifi connection ?
		setupChord();

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

	public void setupChord() {
		chordState = ChordManagerState.SETUP;

		Log.d("coffeebean", "INIT CHORD");
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
		mChordManager.setTempDirectory(Environment
				.getExternalStorageDirectory() + "/RemoteFileExplorer");

		/****************************************************
		 * 2. Set some values before start If you want to use secured channel,
		 * you should enable SecureMode. Please refer
		 * UseSecureChannelFragment.java mChordManager.enableSecureMode(true);
		 * 
		 *
		 * Once you will use sendFile or sendMultiFiles, you have to call
		 * setTempDirectory mChordManager.setTempDirectory(Environment.
		 * getExternalStorageDirectory().getAbsolutePath() + "/Chord");
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
					/*
					 * Toast.makeText(ctx, getInterfaceName(interfaceType) +
					 * " is disconnected", Toast.LENGTH_SHORT).show();
					 */
				}
			}

			@Override
			public void onConnected(int interfaceType) {
				if (interfaceType == mSelectedInterface) {
					/*
					 * Toast.makeText(ctx, getInterfaceName(interfaceType) +
					 * " is connected", Toast.LENGTH_SHORT).show();
					 */
				}
			}
		});

		// auto start connection
		startChord();
	}

	public void startChord() {
		chordState = ChordManagerState.START;

		// 3. Start Chord using the first interface in the list of available
		// interfaces.

		List<Integer> infList = mChordManager.getAvailableInterfaceTypes();
		if (infList.isEmpty()) {
			Log.d("coffeebean", "ChordConnectionManager:"
					+ "    There is no available connection.");
			return;
		}

		int interfaceType = 0;
		for (int interfaceValue : mChordManager.getAvailableInterfaceTypes()) {
			Log.d("coffeebean", "ChordConnectionManager:"
					+ "Available interface : " + interfaceValue);
			if (interfaceValue == SchordManager.INTERFACE_TYPE_WIFI) {
				interfaceType = SchordManager.INTERFACE_TYPE_WIFI;
				Log.d("coffeebean", "ChordConnectionManager:"
						+ "ChordManager.INTERFACE_TYPE_WIFI");
				break;
			} else if (interfaceValue == SchordManager.INTERFACE_TYPE_WIFI_AP) {
				interfaceType = SchordManager.INTERFACE_TYPE_WIFI_AP;
				Log.d("coffeebean", "ChordConnectionManager:"
						+ "ChordManager.INTERFACE_TYPE_WIFI_AP");
				break;
			} else if (interfaceValue == SchordManager.INTERFACE_TYPE_WIFI_P2P) {
				interfaceType = SchordManager.INTERFACE_TYPE_WIFI_P2P;
				Log.d("coffeebean", "ChordConnectionManager:"
						+ "ChordManager.INTERFACE_TYPE_WIFI_P2P");
				break;
			}
		}

		try {
			mChordManager.start(interfaceType, mManagerListener);
			mSelectedInterface = interfaceType;

			// sendName();

		} catch (IllegalArgumentException e) {
			Log.d("coffeebean", "ChordConnectionManager:"
					+ "    Fail to start -" + e.getMessage());
		} catch (InvalidInterfaceException e) {
			Log.d("coffeebean", "ChordConnectionManager:"
					+ "    There is no such a connection.");
		} catch (Exception e) {
			Log.d("coffeebean", "ChordConnectionManager:"
					+ "    Fail to start -" + e.getMessage());
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
				Log.d("coffeebean", "ChordConnectionManager:"
						+ "    >onStarted(" + nodeName + ", STARTED_BY_USER)");
				joinTestChannel();
			} else if (reason == STARTED_BY_RECONNECTION) {
				// Re-start by network re-connection.
				Log.d("coffeebean", "ChordConnectionManager:"
						+ "    >onStarted(" + nodeName
						+ ", STARTED_BY_RECONNECTION)");
			}

		}

		@Override
		public void onStopped(int reason) {

			// 8. Chord has stopped successfully

			if (STOPPED_BY_USER == reason) {
				// Success to stop by calling stop() method
				Log.d("coffeebean", "ChordConnectionManager:"
						+ "    >onStopped(STOPPED_BY_USER)");
			} else if (NETWORK_DISCONNECTED == reason) {
				// Stopped by network disconnected
				Log.d("coffeebean", "ChordConnectionManager:"
						+ "    >onStopped(NETWORK_DISCONNECTED)");
			}
		}
	};

	public void sendName() {

		// send name of device
		String phoneName = (Global.getPlayerName().isEmpty()) ? BluetoothAdapter
				.getDefaultAdapter().getName() : Global.getPlayerName();
		byte[][] payload = new byte[1][];
		payload[0] = phoneName.getBytes();
		sendData(payload, ChordMessageType.SENDING_NAME);
	}

	public void joinTestChannel() {
		SchordChannel channel = null;

		// 5. Join my channel
		Log.d("coffeebean", "ChordConnectionManager:" + "    joinChannel");
		channel = mChordManager.joinChannel(getChannelPass(), mChannelListener);
		chordState = ChordManagerState.RUNNING;

		// adding current user
		String nodeName = mChordManager.getName();
		members.put(nodeName, RoomType.HOST);// add(BluetoothAdapter.getDefaultAdapter().getName());
		nodes.put(nodeName, Global.getPlayerName());

		HostOptionsActivity.addMemberToList(Global.getPlayerName());

		if (channel == null) {
			Log.d("coffeebean", "ChordConnectionManager:"
					+ "    Fail to joinChannel");
		}
	}

	public void stopChord() {

		chordState = ChordManagerState.STOP;
		if (mChordManager == null)
			return;

		// If you registered NetworkListener, you should unregister it.

		mChordManager.setNetworkListener(null);

		// 7. Stop Chord. You can call leaveChannel explicitly.
		// mChordManager.leaveChannel(channelPass);

		Log.d("coffeebean", "ChordConnectionManager:" + "    stop");
		mChordManager.stop();
	}

	public void resetCallBack() {
		if (receivedCallback == null)
			receivedCallback = new HashMap<String, Boolean>();

		receivedCallback.clear();

		for (String key : members.keySet()) {
			receivedCallback.put(key, false);
		}

		if (isHost)
			receivedCallback.put(mChordManager.getName(), true);
	}

	private boolean haveAllCalledBack() {

		for (Boolean isCallinBack : receivedCallback.values()) {
			if (!isCallinBack) {
				return false;
			}
		}

		return true;
	}

	public static String getChannelPass() {
		return channelPass;
	}

	public static void setChannelPass(String channelPass) {
		ChordConnectionManager.channelPass = (channelPass.isEmpty()) ? CHORD_HELLO_TEST_CHANNEL
				: channelPass;
	}

	// ***************************************************
	// ChordChannelListener
	// ***************************************************
	private SchordChannel.StatusListener mChannelListener = new SchordChannel.StatusListener() {

		// Called when a node leave event is raised on the channel.

		@Override
		public void onNodeLeft(String fromNode, String fromChannel) {
			Toast.makeText(ctx, nodes.get(fromNode) + " has left the " + fromChannel,
					Toast.LENGTH_SHORT).show();
			HostOptionsActivity.removeMemberFromList(nodes.get(fromNode));
		}

		// Called when a node join event is raised on the channel
		@Override
		public void onNodeJoined(String fromNode, String fromChannel) {
			sendName();
		}

		@Override
		public void onDataReceived(String fromNode, String fromChannel,
				String payloadType, byte[][] payload) {

			ChordMessageType type = ChordMessageType.getSyncType(payloadType);
			switch (type) {
			case CHANGING_ROOM:
				members.put(fromNode,
						RoomType.getRoomType(new String(payload[0])));
				HostOptionsActivity.changeIndicators();

				break;
			case SENDING_NAME:
				String name = new String(payload[0]);
				if (!name.isEmpty() && !members.containsKey(fromNode)) {
					members.put(fromNode, RoomType.HOST);
					nodes.put(fromNode, name);
					Toast.makeText(ctx, name + " has joined the " + channelPass,
							Toast.LENGTH_SHORT).show();
					HostOptionsActivity.addMemberToList(name);
				}
				break;

			/*case MUSIC_PLAY:
				Toast.makeText(ctx, "MUSIC!", Toast.LENGTH_SHORT).show();
				MusicActivity___.setMp3Bytes(payload[0], type);
				break;*/
			case SHOW_PICTURE:
				// Toast.makeText(ctx, "PICTURE!", Toast.LENGTH_SHORT).show();
				Bitmap bmp = BitmapFactory.decodeByteArray(payload[0], 0,
						payload[0].length);
				PictureActivity.setImage(bmp);
				break;
				
			case MUSIC_PLAY:
				MusicActivity.playMusic(payload, type);
				payload = new byte[1][1];
				sendData(fromNode, payload,
						ChordMessageType.MUSIC_PLAY_DATA_COMPLETE);
				break;
			case MUSIC_PLAY_DATA_COMPLETE:
				// TODO: check if all nodes have called back
				receivedCallback.put(fromNode, true);
				if (haveAllCalledBack()) {
					MusicActivity.playMusic(payload, type);
				}
				break;
			case MUSIC_PLAY_PLAY:
				MusicActivity.playMusic(payload, type);
				break;
			case MUSIC_PLAY_SKIP_TO:
				MusicActivity.skipTo(Integer.parseInt(new String(payload[0])));
				break;
			case MUSIC_PLAY_CONTINUE:
			case MUSIC_PLAY_PAUSE:
				MusicActivity.playPauseMusic(type);
				break;
				
			case VIDEO_PLAY:

				VideoActivity.playVideo(payload, type);
				payload = new byte[1][1];
				sendData(fromNode, payload,
						ChordMessageType.VIDEO_PLAY_DATA_COMPLETE);
				break;
			case VIDEO_PLAY_DATA_COMPLETE:

				// TODO: check if all nodes have called back
				receivedCallback.put(fromNode, true);
				if (haveAllCalledBack()) {
					VideoActivity.playVideo(payload, type);
				}

				break;
			case VIDEO_PLAY_PLAY:
				VideoActivity.playVideo(payload, type);
				break;
			case VIDEO_PLAY_SKIP_TO:
				VideoActivity.skipTo(Integer.parseInt(new String(payload[0])));
				break;

			case VIDEO_PLAY_CONTINUE:
			case VIDEO_PLAY_PAUSE:
				VideoActivity.playPauseVideo(type);
				break;
				
			case WHITEBOARD:
				WhiteboardActivity.drawBoard(fromNode, payload);
				break;
			case SHOW_DOCUMENT:
				Toast.makeText(ctx, "DOCUMENT!", Toast.LENGTH_SHORT).show();
				DocumentActivity.pdfLoadImages(ctx, payload);

				break;

			case SHOW_SURVEY:
				SurveyActivity.saveMsg(payload[0]);

				break;
			default:
				break;

			}

		}

		// unused interface funcs
		@Override
		public void onMultiFilesWillReceive(String fromNode,
				String fromChannel, String fileName, String taskId,
				int totalCount, String fileType, long fileSize) {
		}

		@Override
		public void onMultiFilesSent(String toNode, String toChannel,
				String fileName, String taskId, int index, String fileType) {
		}

		@Override
		public void onMultiFilesReceived(String fromNode, String fromChannel,
				String fileName, String taskId, int index, String fileType,
				long fileSize, String tmpFilePath) {
		}

		@Override
		public void onMultiFilesFinished(String node, String channel,
				String taskId, int reason) {
		}

		@Override
		public void onMultiFilesFailed(String node, String channel,
				String fileName, String taskId, int index, int reason) {
		}

		@Override
		public void onMultiFilesChunkSent(String toNode, String toChannel,
				String fileName, String taskId, int index, String fileType,
				long fileSize, long offset, long chunkSize) {
		}

		@Override
		public void onMultiFilesChunkReceived(String fromNode,
				String fromChannel, String fileName, String taskId, int index,
				String fileType, long fileSize, long offset) {
		}

		@Override
		public void onFileWillReceive(String fromNode, String fromChannel,
				String fileName, String hash, String fileType,
				String exchangeId, long fileSize) {
		}

		@Override
		public void onFileSent(String toNode, String toChannel,
				String fileName, String hash, String fileType, String exchangeId) {
		}

		@Override
		public void onFileReceived(String fromNode, String fromChannel,
				String fileName, String hash, String fileType,
				String exchangeId, long fileSize, String tmpFilePath) {
		}

		@Override
		public void onFileFailed(String node, String channel, String fileName,
				String hash, String exchangeId, int reason) {
		}

		@Override
		public void onFileChunkSent(String toNode, String toChannel,
				String fileName, String hash, String fileType,
				String exchangeId, long fileSize, long offset, long chunkSize) {
		}

		@Override
		public void onFileChunkReceived(String fromNode, String fromChannel,
				String fileName, String hash, String fileType,
				String exchangeId, long fileSize, long offset) {
		}
	};

}
