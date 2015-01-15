package com.ust.thesis.prototype.project.WeSync;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Window;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
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

/**
 * Created by robertjordanebdalin on 8/28/14.
 */
public class PictureActivity extends ChordActivity {
	String nodename;
	Button button1;
	static ImageView imageView1;
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
		setContentView(R.layout.picture_activity);
		button1 = (Button) findViewById(R.id.button1);
		imageView1 = (ImageView) findViewById(R.id.imageView1);

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
							PictureActivity.this);
					builder.setTitle("Choose image file");
					builder.setSingleChoiceItems(musicname, -1,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int item) {
									button1.setText(musicname[item]);
									chosen = item;
									levelDialog.dismiss();

									File imgFile = new File(musicpath[item]);

									Bitmap bmp = BitmapFactory
											.decodeFile(imgFile
													.getAbsolutePath());
									ByteArrayOutputStream stream = new ByteArrayOutputStream();
									bmp.compress(Bitmap.CompressFormat.PNG,
											100, stream);
									byte[] byteArray = stream.toByteArray();

									imageView1.setImageBitmap(bmp);

									byte[][] payload = new byte[1][];
									payload[0] = byteArray;

									bmpMsg = bmp;
									
									ChordConnectionManager
											.getInstance()
											.sendData(
													payload,
													ChordMessageType.SHOW_PICTURE);

								}
							});
					levelDialog = builder.create();
					levelDialog.show();

				} else {
					Toast.makeText(PictureActivity.this, "NO MUSIC FOUND",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

	}

	@Override
	public void onBackPressed() {
		// code here to show dialog
		super.onBackPressed();
		finish();
	}

	public void walkdir(File dir) {

		String pdfPattern = ".jpg";

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

	static Bitmap bmpMsg = null;

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	
		if(bmpMsg != null) {
			imageView1.setImageBitmap(bmpMsg);
		}
	}
	
	public static void setImage(Bitmap bmp) {
		if (PictureActivity.isRunning()) {
			imageView1.setImageBitmap(bmp);
			bmpMsg = null;
		} else{
			bmpMsg = bmp;
		}
	}

}