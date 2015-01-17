package com.ust.thesis.prototype.project.WeSync;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ust.thesis.prototype.project.WeSync.chord.ChordConnectionManager;
import com.ust.thesis.prototype.project.WeSync.chord.ChordMessageType;
import com.ust.thesis.prototype.project.WeSync.chord.SurveyQuestion;

/**
 * Created by robertjordanebdalin on 8/28/14.
 */
public class SurveyActivity extends ChordActivity {
	static EditText questions;
	static TextView questiontextView;
	static TextView deviceanswer, devicename;
	static Button addPolls, addQuestion;
	static Button broadcast, nextQuestion;
	static ListView lv;
	String clickanser = "";
	// String answers;
	static ArrayList<String> al;
	static ArrayAdapter<String> aa;

	static ArrayList<SurveyQuestion> arrQuestions;
	ArrayList<String> arrAnswers;
	String answers;
	
	static LinearLayout createSurveyButtons, answerSurveyButtons;

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.survey_activity);

		answers = null;
		arrQuestions = new ArrayList<SurveyQuestion>();
		arrAnswers = new ArrayList<String>();

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
		TextView txtb = (TextView) findViewById(R.id.addNewQuestion);
		Typeface font = Typeface
				.createFromAsset(getAssets(), "Roboto-Thin.ttf");
		txt.setTypeface(font);
		txta.setTypeface(font);
		txtb.setTypeface(font);

		addPolls = (Button) findViewById(R.id.addPolls);
		addQuestion = (Button) findViewById(R.id.addNewQuestion);
		broadcast = (Button) findViewById(R.id.broadcast);
		nextQuestion = (Button) findViewById(R.id.nextQuestion);
		
		createSurveyButtons = (LinearLayout) findViewById(R.id.createSurveyButtons);
		answerSurveyButtons = (LinearLayout) findViewById(R.id.answerSurveyButtons);
		answerSurveyButtons.setVisibility(View.GONE);
		createSurveyButtons.setVisibility(View.VISIBLE);
		


		lv = (ListView) findViewById(R.id.listView_items);

		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		al = new ArrayList<String>();// initialize array list
		al = new ArrayList<String>();
		al.clear();

		aa = new ArrayAdapter<String>(this
				,android.R.layout.simple_list_item_checked
				//,R.layout.survey_list_item
				//,R.id.tv_poll_answer
				, al);// step4 : establish
		
		lv.setAdapter(aa);

		answers = BluetoothAdapter.getDefaultAdapter().getName() ;
		
		nextQuestion.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				//showQuestion(arrQuestions.get(0));

				answers += "<>" + String.valueOf(lv.getItemAtPosition(lv.getCheckedItemPosition()));//String.valueOf(lv.getSelectedItem());

				
				
				
				if (arrQuestions.size() >= 1) {

					showQuestion(arrQuestions.get(0));
					arrQuestions.remove(0);

					if(arrQuestions.size()  == 0){
						nextQuestion.setText("Send answers");
					}
					
				} else {
					byte[][] payload = new byte[1][];
					//answers = BluetoothAdapter.getDefaultAdapter().getName() +"<>"+answers;
					payload[0] = answers.getBytes();

					ChordConnectionManager.getInstance().sendData(payload,
							ChordMessageType.SHOW_SURVEY);
				}

			}
		});

		addQuestion.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String ques = questions.getText().toString();
				questions.setText("");

				arrQuestions.add(new SurveyQuestion(ques, (ArrayList<String>)arrAnswers.clone(),
						String.valueOf(lv.getItemAtPosition(lv.getCheckedItemPosition()))//lv.getSelectedItem()
						//arrAnswers.get(0)
						));// TODO: get correct

				al.clear();
				aa.notifyDataSetChanged();
				arrAnswers.clear();
			}
		});

		broadcast.setOnClickListener(new OnClickListener() {
			@SuppressLint("WrongCall")
			@Override
			public void onClick(View v) {
				List<Integer> infList = null;
				if (ChordConnectionManager.mChordManager == null) {

					System.out.println("    There is no available connection.");
					Toast.makeText(SurveyActivity.this,
							"NO CONNECTION AVAILABLE", Toast.LENGTH_SHORT)
							.show();
				} else
					infList = ChordConnectionManager.mChordManager
							.getAvailableInterfaceTypes();

				if (infList.isEmpty()) {
					System.out.println("    There is no available connection.");
					Toast.makeText(SurveyActivity.this,
							"NO CONNECTION AVAILABLE", Toast.LENGTH_SHORT)
							.show();
				} else {

					broadcast.setVisibility(View.GONE);
					addPolls.setVisibility(View.GONE);
					addQuestion.setVisibility(View.GONE);
					lv.setVisibility(View.GONE);
					questions.setVisibility(View.GONE);
					questiontextView.setVisibility(View.VISIBLE);
					questiontextView
							.setText("Waiting for answers. Please wait...");
					addPolls.setText("Waiting for answers");
					addPolls.setClickable(false);

					// add last question
					arrQuestions.add(new SurveyQuestion(questions.getText()
							.toString(), arrAnswers, arrAnswers.get(0)));

					StringBuilder str = null;

					for (SurveyQuestion q : arrQuestions) {

						if (str == null)
							str = new StringBuilder();
						else
							str.append("^^");

						str.append(q.question).append("**");

						for (String a : q.answers)
							str.append(a).append("~~");

						str.append(q.correctAnswer);
					}

					byte[][] payload = new byte[1][];
					payload[0] = str.toString().getBytes();

					ChordConnectionManager.getInstance().sendData(payload,
							ChordMessageType.SHOW_SURVEY);

				}
			}
		});
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
								// answers = answers + "~~" + value;
								arrAnswers.add(value.toString());

								lv.setItemChecked(0, true);
								// answers = answers + "^" + value;
								// System.out.println("Answers : " + answers);
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
/*lv.setOnItemSelectedListener(new OnItemSelectedListener() {
	
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub

		clickanser = o.toString();
		Toast.makeText(SurveyActivity.this,
				"You answered " + clickanser, Toast.LENGTH_SHORT)
				.show();
		broadcast.setText("Send Answer : " + clickanser);
	}
	
});

lv.getSel

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
		});*/

	}

	public static void saveMsg(byte[] payload) {
		if (SurveyActivity.isRunning()) {
			showSurvey(payload);
		}
	}
   
	public void showQuestion(SurveyQuestion sq) {

		al.clear();
		for (int i = 0; i < sq.answers.size(); i++) {
			al.add(0, sq.answers.get(i));
			// step ii: notify to adapter
			aa.notifyDataSetChanged();
		}
		lv.setItemChecked(0, true);
		
		questiontextView.setText(sq.question);
	}

	public static void showSurvey(byte[] payload) {

		String message = new String(payload);
		if (addPolls.getText().toString().equals("Add Polls")) {

			arrQuestions = new ArrayList<SurveyQuestion>();
			ArrayList<String> arrAnswers = new ArrayList<String>();

			String[] arrStr = message.split("\\^\\^").clone();

			String str;

			for (int i = 0; i < arrStr.length; i++) {

				str = arrStr[i];

				// for (String str : arrStr) {

				arrAnswers.clear();

				for (String strA : str.split("\\*\\*")[1].split("~~")) {
					arrAnswers.add(strA);
				}

				String correct = arrAnswers.remove(arrAnswers.size() - 1);

				arrQuestions.add(0, new SurveyQuestion(str.split("\\*\\*")[0],
						(ArrayList<String>)arrAnswers.clone(), correct));
			}

			for (int i = 0; i < arrQuestions.get(0).answers.size(); i++) {
				al.add(0, arrQuestions.get(0).answers.get(i));
				// step ii: notify to adapter
				aa.notifyDataSetChanged();
			}

			lv.setItemChecked(0, true);
			createSurveyButtons.setVisibility(View.GONE);
			answerSurveyButtons.setVisibility(View.VISIBLE);

			questions.setVisibility(View.GONE);
			questiontextView.setVisibility(View.VISIBLE);
			questiontextView.setText(arrQuestions.get(0).question);

			arrQuestions.remove(0);

			if(arrQuestions.size() == 0)
				nextQuestion.setText("Send answer");
			
		} else {// if(addPolls.getText().toString().equals("Send Answer")){
			/*
			 * String newstring = devicename.getText().toString()
			 * .replace("Player Name\n", ""); String newans =
			 * deviceanswer.getText().toString() .replace("Player Answer\n",
			 * ""); devicename.setText(newstring); deviceanswer.setText(newans);
			 * deviceanswer.setVisibility(View.VISIBLE);
			 * devicename.setVisibility(View.VISIBLE);
			 * broadcast.setVisibility(View.GONE);
			 * addPolls.setVisibility(View.GONE);
			 * questions.setVisibility(View.GONE);
			 * 
			 * 
			 * String[] splitans = message.split("<>");
			 * devicename.setText("Player Name\n" +
			 * devicename.getText().toString() + "\n" + splitans[0]);
			 * deviceanswer.setText("Player Answer\n" +
			 * deviceanswer.getText().toString() + "\n" + splitans[1]);
			 * lv.setVisibility(View.GONE);
			 * questiontextView.setVisibility(View.GONE);
			 */

			// TODO: what to do on host side when getting answers

			/* String newstring = devicename.getText().toString().replace("Player Name\n", ""); 
			 String newans = deviceanswer.getText().toString() .replace("Player Answer\n", ""); 
			 devicename.setText(newstring); 
			 deviceanswer.setText(newans);*/
			 deviceanswer.setVisibility(View.VISIBLE);
			 devicename.setVisibility(View.VISIBLE);
			 String[] splitans = message.split("<>");
			 devicename.setText("Player Name\n" + devicename.getText().toString() + "\n" + splitans[0]);
			 
			 deviceanswer.setText("Player Answer\n" + deviceanswer.getText().toString() + "\n" + message);
			 
			 lv.setVisibility(View.GONE);
			 
			 questiontextView.setVisibility(View.GONE);
			 
			 
			answerSurveyButtons.setVisibility(View.GONE);			
			createSurveyButtons.setVisibility(View.GONE);
			
			
			
		}
	}

}
