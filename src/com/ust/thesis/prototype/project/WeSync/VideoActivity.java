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
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.ust.thesis.prototype.project.WeSync.chord.ChordConnectionManager;
import com.ust.thesis.prototype.project.WeSync.chord.ChordMessageType;

/**
 * Created by robertjordanebdalin on 8/28/14.
 */
public class VideoActivity extends ChordActivity {
	Button button1;
	static VideoView mVideoView;
	ImageView playmusic;
	String[] musicname;
	String[] musicpath;
	String mname = "", mpath = "";
	int chosen;
	AlertDialog levelDialog;
	int countfile = 0;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.video_activity);

		mVideoView = (VideoView) findViewById(R.id.videoview);

		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		MediaController mVidCtrlr = new MediaController(this);
		mVidCtrlr.setAnchorView(mVideoView);
		mVideoView.setMediaController(mVidCtrlr);
		
		
		button1 = (Button) findViewById(R.id.button1);
		/*playmusic = (ImageView) findViewById(R.id.playmusic);*/

		walkdir(Environment.getExternalStorageDirectory());

		/*playmusic.setOnClickListener(new OnClickListener() {
			@SuppressLint("WrongCall")
			@Override
			public void onClick(View v) {
				if (!(button1.getText().equals("Select Video"))) {
					

				} else
					Toast.makeText(VideoActivity.this, "NO VIDEO SELECTED",
							Toast.LENGTH_SHORT).show();

			}
		});*/

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
							VideoActivity.this);
					builder.setTitle("Choose video file");
					builder.setSingleChoiceItems(musicname, -1,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int item) {
									button1.setText(musicname[item] + "");
									chosen = item;
									levelDialog.dismiss();
									playSelectedVid();
								}
							});
					levelDialog = builder.create();
					levelDialog.show();
					

				} else {
					Toast.makeText(VideoActivity.this, "NO VIDEO FOUND",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		
	}
	
	public void playSelectedVid(){
		// Play audio
		File file = new File(musicpath[chosen]);
		String SrcPath = (musicpath[chosen]);
		if (file.exists())
			System.out.println("exist");
		else
			System.out.println("do not exist : "
					+ musicpath[chosen]);

		mVideoView.setVideoPath(SrcPath);
		
		
		mVideoView.requestFocus();
		mVideoView.start();

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
					ChordMessageType.VIDEO_PLAY);
			

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

		String pdfPattern = ".mp4";

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

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		if(byteArrayMsg != null){
			showVideo(byteArrayMsg);
		}
	}
	
	public static void showVideo(byte[] byteArray){

		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Android/temp.mp4";

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

		// String SrcPath = path;//"/sdcard/SHELLA2.mp4";
		mVideoView.setVideoPath(path);
		mVideoView.requestFocus();
		mVideoView.start();
	}
	
	
	public static void playVideo(byte[][] payload) {

		if (VideoActivity.isRunning()) {
			byteArrayMsg = null;
			showVideo(payload[0]);
		} else
			byteArrayMsg = payload[0];
	}

}