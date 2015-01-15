package com.ust.thesis.prototype.project.WeSync;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.ust.thesis.prototype.project.WeSync.chord.ChordMessageType;

/**
 * Created by robertjordanebdalin on 8/28/14.
 */
public class DocumentActivity extends ChordActivity {
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
		setContentView(R.layout.document_activity);
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
							DocumentActivity.this);
					builder.setTitle("Choose document file");
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
									
									ChordConnectionManager
											.getInstance()
											.sendData(
													payload,
													ChordMessageType.SHOW_DOCUMENT);

								}
							});
					levelDialog = builder.create();
					levelDialog.show();

				} else {
					Toast.makeText(DocumentActivity.this, "NO DOCUMENT FOUND",
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

		String pdfPattern = ".pdf";

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