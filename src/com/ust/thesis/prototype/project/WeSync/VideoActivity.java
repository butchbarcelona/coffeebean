package com.ust.thesis.prototype.project.WeSync;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
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
import java.io.FileNotFoundException;
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
import com.ust.thesis.prototype.project.WeSync.chord.ChordConnectionManager;
import com.ust.thesis.prototype.project.WeSync.chord.ChordMessageType;

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
import android.widget.VideoView;

/**
 * Created by robertjordanebdalin on 8/28/14.
 */
public class VideoActivity extends Activity {
	Button button1;
	static VideoView mVideoView;
	ImageView playmusic;
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
		setContentView(R.layout.video_activity);

		mVideoView = (VideoView)findViewById(R.id.videoview);
		button1 = (Button)findViewById(R.id.button1);
		playmusic = (ImageView)findViewById(R.id.playmusic);


		walkdir(Environment.getExternalStorageDirectory());

		playmusic.setOnClickListener( new OnClickListener() {
			@SuppressLint("WrongCall")
			@Override
			public void onClick(View v) {
				if (!(button1.getText().equals("Select Video"))){
					// Play audio
					File file = new File(musicpath[chosen]);
					String SrcPath = (musicpath[chosen]);
					if (file.exists())System.out.println("exist");
					else System.out.println("do not exist : "+musicpath[chosen]);


					mVideoView.setVideoPath(SrcPath);
					mVideoView.requestFocus();
					mVideoView.start();

					ByteArrayOutputStream buffer = new ByteArrayOutputStream();
					try {

						InputStream is = new FileInputStream(file);
						byte[] temp = new byte[1024];
						int read;

						while((read = is.read(temp)) >= 0){
							buffer.write(temp, 0, read);
						}
						is.close();        
						byte[] data = buffer.toByteArray();

						byte[][] payload = new byte[1][];
						payload[0] = data;

						/*SchordChannel channel = mChordManager.getJoinedChannel(CHORD_HELLO_TEST_CHANNEL);
	            	        channel.sendDataToAll(CHORD_SAMPLE_MESSAGE_TYPE, payload);*/

	            	        ChordConnectionManager.getInstance().sendData(payload, ChordMessageType.VIDEO_PLAY);


					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						System.out.println("Error : "+e);
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("Error 1: "+e);
					}

				}else Toast.makeText(VideoActivity.this,"NO VIDEO SELECTED",Toast.LENGTH_SHORT).show();

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
					AlertDialog.Builder builder = new AlertDialog.Builder(VideoActivity.this);
					builder.setTitle("Choose video file");
					builder.setSingleChoiceItems(musicname, -1, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							button1.setText(musicname[item]+"");
							chosen = item;
							levelDialog.dismiss();    
						}
					});
					levelDialog = builder.create();
					levelDialog.show();

				}else {
					Toast.makeText(VideoActivity.this,"NO VIDEO FOUND",
							Toast.LENGTH_SHORT).show();
				}
			}});

	}

	public void walkdir(File dir) {

		String pdfPattern = ".mp4";

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
		super.onBackPressed();  
		finish();
	}


	public static void playVideo(byte[][] payload){
		byte[] byteArray = payload[0];

		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/temp.mp4";

		File file = new File(path);
		//"/sdcard/SHELLA2.mp4");
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

		//String SrcPath = path;//"/sdcard/SHELLA2.mp4";
		mVideoView.setVideoPath(path);
		mVideoView.requestFocus();
		mVideoView.start();
	}




}