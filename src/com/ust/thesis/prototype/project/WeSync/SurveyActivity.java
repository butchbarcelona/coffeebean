package com.ust.thesis.prototype.project.WeSync;

import java.util.ArrayList;
import java.util.List;

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
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by robertjordanebdalin on 8/28/14.
 */
public class SurveyActivity extends ChordActivity {
	static EditText questions;
	static TextView questiontextView;
	static TextView deviceanswer, devicename;
	static Button addPolls;
	static Button broadcast;
	static ListView lv;
	String clickanser = "";
	String answers;
	static ArrayList<String> al;
	static ArrayAdapter<String> aa;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.survey_activity);
		questions = (EditText) findViewById(R.id.question);
		questiontextView = (TextView) findViewById(R.id.questiontextView);
		devicename = (TextView) findViewById(R.id.devicename);
		deviceanswer = (TextView) findViewById(R.id.deviceanswer);

		questions.setVisibility(View.VISIBLE);
		questiontextView.setVisibility(View.GONE);
		devicename.setVisibility(View.GONE);
		deviceanswer.setVisibility(View.GONE);

		TextView txt = (TextView) findViewById(R.id.addPolls);
		TextView txta = (TextView) findViewById(R.id.broadcast);
		Typeface font = Typeface
				.createFromAsset(getAssets(), "Roboto-Thin.ttf");
		txt.setTypeface(font);
		txta.setTypeface(font);

		addPolls = (Button) findViewById(R.id.addPolls);
		broadcast = (Button) findViewById(R.id.broadcast);
		broadcast.setOnClickListener(new OnClickListener() {
			@SuppressLint("WrongCall")
			@Override
			public void onClick(View v) {
				List<Integer> infList = ChordConnectionManager.mChordManager
						.getAvailableInterfaceTypes();
				if (infList.isEmpty()) {
					System.out.println("    There is no available connection.");
					Toast.makeText(SurveyActivity.this,
							"NO CONNECTION AVAILABLE", Toast.LENGTH_SHORT)
							.show();
				} else {
					if (addPolls.getText().toString().equals("Add Polls")) {
						broadcast.setVisibility(View.GONE);
						addPolls.setVisibility(View.GONE);
						lv.setVisibility(View.GONE);
						questions.setVisibility(View.GONE);
						questiontextView.setVisibility(View.VISIBLE);
						questiontextView
								.setText("Waiting for answers. Please wait...");
						addPolls.setText("Waiting for answers");
						addPolls.setClickable(false);
						String ques = questions.getText().toString();
						String ans = answers.replace("null^", "");
						ques = ques + "^^^" + ans;
						byte[][] payload = new byte[1][];
						payload[0] = ques.getBytes();

						/*
						 * SchordChannel channel =
						 * mChordManager.getJoinedChannel
						 * (CHORD_HELLO_TEST_CHANNEL);
						 * channel.sendDataToAll(CHORD_SAMPLE_MESSAGE_TYPE,
						 * payload);
						 */

						ChordConnectionManager.getInstance().sendData(payload,
								ChordMessageType.SHOW_SURVEY);

					} else {
						broadcast.setVisibility(View.GONE);
						addPolls.setVisibility(View.GONE);
						lv.setVisibility(View.GONE);
						questions.setVisibility(View.GONE);
						questiontextView.setVisibility(View.VISIBLE);
						questiontextView
								.setText("Successfully submitted your answer.");
						String devinfo = Global.getPlayerName() + "<>"
								+ clickanser;
						byte[][] payload = new byte[1][];
						payload[0] = devinfo.getBytes();

						/*
						 * SchordChannel channel =
						 * mChordManager.getJoinedChannel
						 * (CHORD_HELLO_TEST_CHANNEL);
						 * channel.sendDataToAll(CHORD_SAMPLE_MESSAGE_TYPE,
						 * payload);
						 */

						ChordConnectionManager.getInstance().sendData(payload,
								ChordMessageType.SHOW_SURVEY);
					}
				}
			}
		});

		lv = (ListView) findViewById(R.id.listView_items);
		al = new ArrayList<String>();// initialize array list
		al = new ArrayList<String>();
		al.clear();

		aa = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, al);// step4 : establish
															// communication bw
															// arraylist and
															// adapter
		// step5 : establish communication bw adapter and dest (listview)
		lv.setAdapter(aa);

		addPolls.setOnClickListener(new OnClickListener() {
			@SuppressLint("WrongCall")
			@Override
			public void onClick(View v) {

				AlertDialog.Builder alert = new AlertDialog.Builder(
						SurveyActivity.this);

				alert.setTitle("Survey");
				alert.setMessage("Write Option: ");

				// Set an EditText view to get user input
				final EditText input = new EditText(SurveyActivity.this);
				alert.setView(input);

				alert.setPositiveButton("Add",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								Editable value = input.getText();
								// Do something with value!

								al.add(0, value.toString());
								// step ii: notify to adapter
								aa.notifyDataSetChanged();
								// step iii: clr edit text
								answers = answers + "^" + value;
								System.out.println("Answers : " + answers);
							}
						});

				alert.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// Canceled.
							}
						});

				alert.show();

			}
		});

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Object o = lv.getItemAtPosition(position);
				if (addPolls.getText().toString().equals("Add Polls")) {

				} else {
					clickanser = o.toString();
					Toast.makeText(SurveyActivity.this,
							"You answered " + clickanser, Toast.LENGTH_SHORT)
							.show();
					broadcast.setText("Send Answer : " + clickanser);

				}
			}
		});

	}

	public static void saveMsg(byte[] payload){
		if(SurveyActivity.isRunning()){
			showSurvey(payload);
		}
	}
	
	public static void showSurvey(byte[] payload) {

		String message = new String(payload);
		if (addPolls.getText().toString().equals("Add Polls")) {
			message = message.replace("^^^", "<>");
			String[] splitmessage = message.split("<>");

			splitmessage[1] = splitmessage[1].replace("^", "<>");
			String[] splitans = splitmessage[1].split("<>");
			for (int i = 0; i < splitans.length; i++) {
				al.add(0, splitans[i]);
				// step ii: notify to adapter
				aa.notifyDataSetChanged();
			}

			addPolls.setVisibility(View.GONE);
			questiontextView.setVisibility(View.VISIBLE);
			questiontextView.setText(splitmessage[0]);
			questions.setVisibility(View.GONE);
			broadcast.setText("Send Answer");
			addPolls.setText("Send ");
		} else {// if(addPolls.getText().toString().equals("Send Answer")){
			String newstring = devicename.getText().toString()
					.replace("Player Name\n", "");
			String newans = deviceanswer.getText().toString()
					.replace("Player Answer\n", "");
			devicename.setText(newstring);
			deviceanswer.setText(newans);
			deviceanswer.setVisibility(View.VISIBLE);
			devicename.setVisibility(View.VISIBLE);
			broadcast.setVisibility(View.GONE);
			addPolls.setVisibility(View.GONE);
			questions.setVisibility(View.GONE);
			String[] splitans = message.split("<>");
			devicename.setText("Player Name\n"
					+ devicename.getText().toString() + "\n" + splitans[0]);
			deviceanswer.setText("Player Answer\n"
					+ deviceanswer.getText().toString() + "\n" + splitans[1]);
			lv.setVisibility(View.GONE);
			questiontextView.setVisibility(View.GONE);

		}
	}

	
}
