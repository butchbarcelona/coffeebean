package com.ust.thesis.prototype.project.WeSync;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.ust.thesis.prototype.project.WeSync.chord.ChordConnectionManager;
import com.ust.thesis.prototype.project.WeSync.chord.SyncMessageType;


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

	static MediaPlayer mediaPlayer;
	static File pathToCache;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.music_activity);

		pathToCache = getCacheDir();
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

						//TODO: chord
						ChordConnectionManager.getInstance().sendData(payload, SyncMessageType.MUSIC_PLAY);


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
		super.onBackPressed();  

		if(mediaPlayer.isPlaying())
			mediaPlayer.stop();


		finish();
	}


	public static void playMp3(byte[] mp3SoundByteArray) {
		try {
			// create temp file that will hold byte array
			File tempMp3 = File.createTempFile("kurchina", "mp3", pathToCache );//getCacheDir());
			tempMp3.deleteOnExit();
			FileOutputStream fos = new FileOutputStream(tempMp3);
			fos.write(mp3SoundByteArray);
			fos.close();

			// Tried reusing instance of media player
			// but that resulted in system crashes...  
			mediaPlayer = new MediaPlayer();

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