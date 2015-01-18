package com.ust.thesis.prototype.project.WeSync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ust.thesis.prototype.project.WeSync.chord.ChordConnectionManager;
import com.ust.thesis.prototype.project.WeSync.chord.ChordMessageType;
import com.ust.thesis.prototype.project.WeSync.chord.RoomType;

/**
 * Created by robertjordanebdalin on 8/28/14.
 */
public class HostOptionsActivity extends Activity implements OnClickListener {

	Button whiteboard, picture, video, document, music, survey;
	static TextView whiteIndicator, pictureIndicator, vidIndicator,
			docIndicator, musicIndicator, surveyIndicator;

	ListView mDrawerList;
	static ArrayAdapter<String> drawerListAdapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.host_options_activity);

		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		drawerListAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, new ArrayList<String>());
		// ChordConnectionManager.getInstance().getArrMembers() );
		// Set the adapter for the list view
		mDrawerList.setAdapter(drawerListAdapter);

		whiteIndicator = (TextView) findViewById(R.id.whiteboard_indicator);
		pictureIndicator = (TextView) findViewById(R.id.picture_indicator);
		vidIndicator = (TextView) findViewById(R.id.video_indicator);
		docIndicator = (TextView) findViewById(R.id.document_indicator);
		musicIndicator = (TextView) findViewById(R.id.music_indicator);
		surveyIndicator = (TextView) findViewById(R.id.survey_indicator);

		TextView txt1 = (TextView) findViewById(R.id.whiteboard);
		TextView txt2 = (TextView) findViewById(R.id.picture);
		TextView txt3 = (TextView) findViewById(R.id.music);
		TextView txt4 = (TextView) findViewById(R.id.video);
		TextView txt5 = (TextView) findViewById(R.id.document);
		TextView txt6 = (TextView) findViewById(R.id.survey);

		Typeface font = Typeface
				.createFromAsset(getAssets(), "Roboto-Thin.ttf");

		txt1.setTypeface(font);
		txt2.setTypeface(font);
		txt3.setTypeface(font);
		txt4.setTypeface(font);
		txt5.setTypeface(font);
		txt6.setTypeface(font);
		
		if(!ChordConnectionManager.getInstance().isHost){
			txt6.setText("Join Survey");
		}

		whiteboard = (Button) findViewById(R.id.whiteboard);
		picture = (Button) findViewById(R.id.picture);
		video = (Button) findViewById(R.id.video);
		document = (Button) findViewById(R.id.document);
		music = (Button) findViewById(R.id.music);
		survey = (Button) findViewById(R.id.survey);

		whiteboard.setOnClickListener(this);
		picture.setOnClickListener(this);
		video.setOnClickListener(this);
		document.setOnClickListener(this);
		music.setOnClickListener(this);
		survey.setOnClickListener(this);

		ChordConnectionManager.getInstance().initChord(this);
	}

	public static void changeIndicators() {
		HashMap<String, RoomType> memberRooms = ChordConnectionManager
				.getInstance().getMembersRooms();
		for (RoomType type : RoomType.values()) {
			changeIndicator(type, 0);
		}

		for (String key : memberRooms.keySet()) { 
			changeIndicator(memberRooms.get(key), 1);
		}
	}

	public static void changeIndicator(RoomType type, int members) {

		switch (type) {
		case HOST:
			break;
		case DOCUMENT:
			docIndicator.setText(String.valueOf(members));
			break;
		case MUSIC:
			musicIndicator.setText(String.valueOf(members));
			break;
		case PICTURE:
			pictureIndicator.setText(String.valueOf(members));
			break;
		case VIDEO:
			vidIndicator.setText(String.valueOf(members));
			break;
		case WHITEBOARD:
			whiteIndicator.setText(String.valueOf(members));
			break;
		case SURVEY:
			surveyIndicator.setText(String.valueOf(members));
		default:
			break;

		}
	}

	public static void addMemberToList(String member) {

		//drawerListAdapter.clear();
		/*
		 * drawerListAdapter.addAll(ChordConnectionManager.getInstance()
		 * .getArrMembers());
		 */

		
		
		drawerListAdapter.add(member);
		drawerListAdapter.notifyDataSetChanged();
	}
	public static void removeMemberFromList(String member) {

		drawerListAdapter.remove(member);
		drawerListAdapter.notifyDataSetChanged();
	}

	public void changeRoom(RoomType room) {

		byte[][] payload = new byte[1][];
		payload[0] = room.getString().getBytes();

		ChordConnectionManager.getInstance().sendData(payload,
				ChordMessageType.CHANGING_ROOM);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		changeRoom(RoomType.HOST);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ChordConnectionManager.getInstance().stopChord();

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();
	}

	@Override
	public void onClick(View v) {

		Intent activityIntent = null;

		switch (v.getId()) {
		case R.id.whiteboard:
			changeRoom(RoomType.WHITEBOARD);
			activityIntent = new Intent(getApplicationContext(),
					WhiteboardActivity.class);
			break;
		case R.id.picture:
			changeRoom(RoomType.PICTURE);
			activityIntent = new Intent(getApplicationContext(),
					PictureActivity.class);
			break;
		case R.id.video:
			changeRoom(RoomType.VIDEO);
			activityIntent = new Intent(getApplicationContext(),
					VideoActivity.class);
			break;
		case R.id.document:
			changeRoom(RoomType.DOCUMENT);
			activityIntent = new Intent(getApplicationContext(),
					DocumentActivity.class);
			break;
		case R.id.music:
			changeRoom(RoomType.MUSIC);
			activityIntent = new Intent(getApplicationContext(),
					MusicActivity.class);
			break;
		case R.id.survey:
			changeRoom(RoomType.SURVEY);
			activityIntent = new Intent(getApplicationContext(),
					SurveyActivity.class);
			break;
		}

		if (activityIntent != null) {
			startActivity(activityIntent);
		}
	}

}