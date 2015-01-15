package com.ust.thesis.prototype.project.WeSync;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.chord.InvalidInterfaceException;
import com.samsung.android.sdk.chord.Schord;
import com.samsung.android.sdk.chord.SchordChannel;
import com.samsung.android.sdk.chord.SchordManager;
import com.samsung.android.sdk.chord.SchordManager.NetworkListener;
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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Cap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by robertjordanebdalin on 8/28/14.
 */
public class WhiteboardActivity extends ChordActivity implements OnTouchListener{
	LinearLayout linlay;
	private Paint paint;
	private ArrayList points;
	private ArrayList strokes;
	private ImageButton currPaint, pen, eraser, newBtn, saveBtn;

	private float smallBrush, mediumBrush, largeBrush;
	//for doodle
	private float x;
	private float y;
	static DrawPanel drawingPanel;
	Canvas c;

	static int width;
	static int height;

	@Override
	public void onBackPressed()
	{
		// code here to show dialog
		super.onBackPressed();  // optional depending on your needs
		finish();
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		Global.setColor("Red");
		setContentView(R.layout.whiteboard_activity);
		linlay = (LinearLayout) findViewById(R.id.linlay);

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(WhiteboardActivity.this);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("penColor","Black");

		linlay = (LinearLayout)findViewById(R.id.linlay);
		drawingPanel = new DrawPanel(getApplicationContext());
		linlay.addView(drawingPanel);
		drawingPanel.setOnTouchListener(this);
		pen = (ImageButton)findViewById(R.id.pen);
		eraser = (ImageButton)findViewById(R.id.eraser);
		saveBtn = (ImageButton)findViewById(R.id.save_btn);



		pen.setOnClickListener( new OnClickListener() {
			@SuppressLint("WrongCall")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				drawingPanel.setErase(false);
				final String[] color = {"Red","Magenta","Yellow", "Green", "Blue","Cyan"};
				AlertDialog.Builder builder = new AlertDialog.Builder(WhiteboardActivity.this);
				builder.setTitle("Pick pen color")
				.setItems(color, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// The 'which' argument contains the index position
						// of the selected item
						/*   SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(WhiteboardActivity.this);
    		            	   SharedPreferences.Editor editor = preferences.edit();
    		            	   editor.putString("penColor",color[which]);
    		            	   editor.apply();
						 */
						Global.setColor(color[which]);
						System.out.println("Pen Color : "+color[which]);

					}
				});
				builder.show();
			}
		});
		eraser.setOnClickListener( new OnClickListener() {
			@SuppressLint("WrongCall")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				drawingPanel.setErase(true);


			}
		});
		saveBtn.setOnClickListener( new OnClickListener() {
			@SuppressLint("WrongCall")
			@Override
			public void onClick(View v) {
				//else if(view.getId()==R.id.save_btn){
				//save drawing
				AlertDialog.Builder saveDialog = new AlertDialog.Builder(WhiteboardActivity.this);
				saveDialog.setTitle("Save drawing");
				saveDialog.setMessage("Save drawing to device Gallery?");
				saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which){
						//save drawing
						linlay.setDrawingCacheEnabled(true);
						String imgSaved = MediaStore.Images.Media.insertImage(
								getContentResolver(), linlay.getDrawingCache(),
								UUID.randomUUID().toString()+".png", "drawing");
						if(imgSaved!=null){
							Toast savedToast = Toast.makeText(getApplicationContext(), 
									"Drawing saved to Gallery!", Toast.LENGTH_SHORT);
							savedToast.show();
						}
						else{
							Toast unsavedToast = Toast.makeText(getApplicationContext(), 
									"Oops! Image could not be saved.", Toast.LENGTH_SHORT);
							unsavedToast.show();
						}
						linlay.destroyDrawingCache();
					}
				});
				saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which){
						dialog.cancel();
					}

				});
				saveDialog.show();


			}});


		Display v = WhiteboardActivity.this.getWindowManager().getDefaultDisplay();
		width = v.getWidth();
		height = v.getHeight();

		
		
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent mev) {
		int width = v.getWidth();
		int height = v.getHeight();
		float x = mev.getX();
		float y = mev.getY();
		String msg;
		if (x < width / 2) {
			if (y < height / 2)
				msg = "Top left quarter";
			else
				msg = "Bottom left quarter";

		} else {
			if (y < height / 2)
				msg = "Top right quarter";
			else
				msg = "Bottom right quarter";
		}
		String globalPen = Global.getColor()+"";
		double xint = (double) x / width;
		double yint = (double) y/ height;
		String shella = globalPen+","+xint+","+yint;
		
		arrYourMsgs.add(shella);
		
		byte[][] payload = new byte[1][];
		payload[0] = shella.getBytes();


		List<Integer> infList = ChordConnectionManager.mChordManager.getAvailableInterfaceTypes();

		if(infList.isEmpty()){
			System.out.println("    There is no available connection.");

		}else{

			ChordConnectionManager.getInstance().sendData(payload, ChordMessageType.WHITEBOARD); 

			if(mev.getActionMasked() == MotionEvent.ACTION_UP){
				String end = "end";
				payload = new byte[1][];
				payload[0] = end.getBytes();

				ChordConnectionManager.getInstance().sendData(payload, ChordMessageType.WHITEBOARD);	  
			}
		}
		return false;
	}
	
	
	static ArrayList<String> arrMsgs = new ArrayList<String>();
	static ArrayList<String> arrYourMsgs = new ArrayList<String>();

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		for(String msg : arrMsgs){
			drawSavedData(msg);
		}
		
		for(String msg : arrYourMsgs){
			drawSavedData(msg);
		}
	}
	
	public static void drawSavedData(String message){ 
		if (message.equals("end")){
			drawingPanel.EndDrawFromOther();
		}
		else{
			String[] msg = message.split(","); 
			drawingPanel.DrawFromOther(msg[0],Double.parseDouble(msg[1])*width,Double.parseDouble(msg[2])*height);
		}
	}
	

	public static void drawBoard(byte[][] payload){
		if(WhiteboardActivity.isRunning()){
			String message = new String(payload[0]);  
			drawSavedData(message);
			arrMsgs.clear();
		}else{
			arrMsgs.add(new String(payload[0]));
		}
	}

}