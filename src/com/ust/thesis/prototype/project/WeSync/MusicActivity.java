package com.ust.thesis.prototype.project.WeSync;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.Toast;
import android.widget.VideoView;

import com.ust.thesis.prototype.project.WeSync.chord.ChordConnectionManager;
import com.ust.thesis.prototype.project.WeSync.chord.ChordMessageType;

public class MusicActivity extends ChordActivity {
	Button button1;
	static WeSyncVideoView mVideoView;
	ImageView playmusic;
	String[] musicname;
	String[] musicpath;
	String mname = "", mpath = "";
	int chosen;
	AlertDialog levelDialog;
	int countfile = 0;

	static String filePath;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.music_play_activity);

		mVideoView = (WeSyncVideoView) findViewById(R.id.videoview);

		if (ChordConnectionManager.getInstance().isHost) {
			MediaController mVidCtrlr = new MediaController(this);
			mVidCtrlr.setAnchorView(mVideoView);
			mVideoView.setMediaController(mVidCtrlr);

			mVideoView.setListener(new IWeSyncVideoView() {

				@Override
				public void seekTo(int msec) {
					byte[][] payload = new byte[1][1];
					payload[0] = String.valueOf(msec).getBytes();
					ChordConnectionManager.getInstance().sendData(payload,
							ChordMessageType.MUSIC_PLAY_SKIP_TO);
				}

				@Override
				public void play() {
					byte[][] payload = new byte[1][1];
					ChordConnectionManager.getInstance().sendData(payload,
							ChordMessageType.MUSIC_PLAY_CONTINUE);

				}

				@Override
				public void pause() {
					
					
					byte[][] payload = new byte[1][1];
					ChordConnectionManager.getInstance().sendData(payload,
							ChordMessageType.MUSIC_PLAY_PAUSE);
				}
			});
		}

		button1 = (Button) findViewById(R.id.button1);

		walkdir(Environment.getExternalStorageDirectory());

		button1.setOnClickListener(new OnClickListener() {
			@SuppressLint("WrongCall")
			@Override
			public void onClick(View v) {

				String[] tempname = mname.split("%%%%%%");
				String[] temppath = mpath.split("%%%%%%");

				musicname = new String[tempname.length - 1];
				musicpath = new String[tempname.length - 1];

				for (int i = 0; i < tempname.length - 1; i++) {
					musicname[i] = tempname[i + 1];
					musicpath[i] = temppath[i + 1];
				}

				if (tempname.length > 1) {

					// Creating and Building the Dialog
					AlertDialog.Builder builder = new AlertDialog.Builder(
							MusicActivity.this);
					builder.setTitle("Choose music file");
					builder.setSingleChoiceItems(musicname, -1,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int item) {
									button1.setText(musicname[item] + "");
									chosen = item;
									levelDialog.dismiss();
									if(ChordConnectionManager.getInstance().nodes.size() > 1)
										sendSelectedMusic();
									else
										playSelectedMusic( musicpath[chosen]);
								}
							});
					levelDialog = builder.create();
					levelDialog.show();

				} else {
					Toast.makeText(MusicActivity.this, "NO MUSIC FOUND",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

	}

	public void sendSelectedMusic() {
		// Play audio
		File file = new File(musicpath[chosen]);

		filePath = musicpath[chosen];
		ChordConnectionManager.getInstance().resetCallBack();

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {

			InputStream is = new FileInputStream(file);
			byte[] temp = new byte[1024];
			int read;

			while ((read = is.read(temp)) >= 0) {
				buffer.write(temp, 0, read);
			}
			is.close();
			byte[] data = buffer.toByteArray();

			byte[][] payload = new byte[1][];
			payload[0] = data;

			byteArrayMsg = data;

			ChordConnectionManager.getInstance().sendData(payload,
					ChordMessageType.MUSIC_PLAY);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Error : " + e);
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error 1: " + e);
		}
	}

	public void walkdir(File dir) {

		String pdfPattern = ".mp3";

		File[] listFile = dir.listFiles();

		if (listFile != null) {
			for (int i = 0; i < listFile.length; i++) {

				if (listFile[i].isDirectory()) {
					walkdir(listFile[i]);
					// textView1.setText("Directory: "+listFile[i]);
				} else {
					if (listFile[i].getName().endsWith(pdfPattern)) {
						// Do what ever u want
						mname = mname + "%%%%%%" + listFile[i].getName();
						mpath = mpath + "%%%%%%" + listFile[i].getPath();
						System.out.println(mname);
						System.out.println(mpath);
						System.out.println("Found : " + listFile[i].getName());
						System.out.println("Found path : "
								+ listFile[i].getPath());

					}
				}
			}
		}

	}

	@Override
	public void onBackPressed() {
		// code here to show dialog
		super.onBackPressed();
		finish();
	}

	static byte[] byteArrayMsg;
	static byte[] byteArrayMsgMusic;

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		switch (currentType) {
		case MUSIC_PLAY_PLAY:
		case MUSIC_PLAY_SKIP_TO:

			if (byteArrayMsgMusic != null && !mVideoView.isPlaying()) {
				showMusic(byteArrayMsgMusic);
			}

			MusicActivity.skipTo(currMs);
			break;
		}

	}

	public static void showMusic(byte[] byteArray) {

		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Android/temp.mp3";

		File file = new File(path);
		// "/sdcard/SHELLA2.mp4");
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			fos.write(byteArray);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			// handle exception
		} catch (IOException e) {
			// handle exception
		}
		playSelectedMusic(path);
	}

	public static void playSelectedMusic(String path) {

		File file = new File(path);
		String SrcPath = (path);
		if (file.exists())
			System.out.println("exist");
		else
			System.out.println("do not exist : " + path);

		mVideoView.setVideoPath(SrcPath);

		if (ChordConnectionManager.getInstance().isHost)
			new MusicAsyncTask().execute();

		// mVideoView.requestFocus();
		// mVideoView.start();
	}

	public static int currMs = 0;

	public static void skipTo(int ms) {

		currMs = ms;

		if (mVideoView != null) {
			mVideoView.seekTo(ms);
		}
	}

	public static void playPauseMusic(ChordMessageType type) {

		switch (type) {
		case MUSIC_PLAY_CONTINUE:
			if (ChordConnectionManager.getInstance().isHost)
				new MusicAsyncTask().execute();
			else
				mVideoView.start();
			break;
		case MUSIC_PLAY_PAUSE:
			mVideoView.pause();
			break;
		}

	}

	private static class MusicAsyncTask extends AsyncTask<Void, Integer, Void> {
		int duration = 0;
		int current = 0;

		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			Log.d("MusicAsyncTask", "Pre executing!");
			if (mVideoView.isPlaying())
				mVideoView.stopPlayback();
			
			mVideoView.setOnPreparedListener(new OnPreparedListener() {

				public void onPrepared(MediaPlayer mp) {
					duration = mVideoView.getDuration();
				}
			});
			
		}
		
		
		@Override
		protected Void doInBackground(Void... params) {
			
			mVideoView.requestFocus();
			mVideoView.start();

		/*	do {
				//Log.d("MusicAsyncTask", "Still inside while loop");
				current = mVideoView.getCurrentPosition();

				byte[][] payload = new byte[1][1];

				Log.d("MusicAsyncTask", "sending current ms:" + current);
				payload[0] = String.valueOf(current).getBytes();
				
				ChordConnectionManager.getInstance().sendData(payload,
						ChordMessageType.MUSIC_PLAY_SKIP_TO);

				// publishProgress((int) (current * 100 / duration));

			} while (mVideoView.isPlaying());*/

			return null;
		}
		
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			Log.d("MusicAsyncTask", "Finishing asynctask");
		}

	}

	static ChordMessageType currentType = ChordMessageType.MUSIC_PLAY;

	public static void playMusic(byte[][] payload, ChordMessageType type) {

		currentType = type;
		switch (type) {
		case MUSIC_PLAY:
			byteArrayMsgMusic = payload[0];
			break;
		case MUSIC_PLAY_DATA_COMPLETE:
			ChordConnectionManager.getInstance().sendData(payload,
					ChordMessageType.MUSIC_PLAY_PLAY);
			if (ChordConnectionManager.getInstance().isHost) {
				playSelectedMusic(filePath);
			}
			break;
		case MUSIC_PLAY_PLAY:
			if(MusicActivity.isRunning())
				showMusic(byteArrayMsgMusic);
			break;
		}

	}

}