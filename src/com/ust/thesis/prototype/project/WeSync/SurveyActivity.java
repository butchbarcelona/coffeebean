package com.ust.thesis.prototype.project.WeSync;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
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

	
	
	public static boolean  isHost = false;
	
	public static int score = 0;
	static String correctAnswer;
	static LinearLayout createSurveyButtons, answerSurveyButtons, all;
	static TextView tvWaitingQuestions;

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

		ctx = this;
		
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

		all = (LinearLayout) findViewById(R.id.allSurvey);
		tvWaitingQuestions = (TextView) findViewById(R.id.tv_waiting_questions);

		
		if(!ChordConnectionManager.getInstance().isHost){
			all.setVisibility(View.GONE);
			tvWaitingQuestions.setVisibility(View.VISIBLE);
		}
		
		
		al = new ArrayList<String>();// initialize array list
		al = new ArrayList<String>();
		al.clear();

		aa = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_checked
				// ,R.layout.survey_list_item
				// ,R.id.tv_poll_answer
				, al);// step4 : establish

		lv.setAdapter(aa);

		answers = "^^^" + BluetoothAdapter.getDefaultAdapter().getName();

		nextQuestion.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// showQuestion(arrQuestions.get(0));

				String strA = String.valueOf(lv.getItemAtPosition(lv
						.getCheckedItemPosition()));
				answers += "<>"+ strA;// String.valueOf(lv.getSelectedItem());

				if (arrQuestions.size() >= 1) {
					if(correctAnswer.equals(strA)){
						score++;
					}
					showQuestion(arrQuestions.get(0));
					arrQuestions.remove(0);

					if (arrQuestions.size() == 0) {
						nextQuestion.setText("Send answers");
					}

				} else {
					byte[][] payload = new byte[1][];
					// answers = BluetoothAdapter.getDefaultAdapter().getName()
					// +"<>"+answers;
					answers += "||"+score;
					payload[0] = answers.getBytes();

					ChordConnectionManager.getInstance().sendData(payload,
							ChordMessageType.SHOW_SURVEY);
					
					all.setVisibility(View.GONE);
					tvWaitingQuestions.setVisibility(View.VISIBLE);
					tvWaitingQuestions.setText("Successfully sent answers.");
				}

			}
		});

		addQuestion.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String ques = questions.getText().toString();
				questions.setText("");

				arrQuestions.add(new SurveyQuestion(ques,
						(ArrayList<String>) arrAnswers.clone(), String
								.valueOf(lv.getItemAtPosition(lv
										.getCheckedItemPosition()))// lv.getSelectedItem()
						// arrAnswers.get(0)
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

					isHost = true;
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
			aa.notifyDataSetChanged();
		}
		
		lv.setItemChecked(0, true);

		correctAnswer = sq.correctAnswer;
		questiontextView.setText(sq.question);
	}


	static Intent surveyAnswersintent = null;
	static Context ctx;
	
	static byte[] msgs ;
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	
		if(msgs != null){
			showSurvey(msgs);
		}
	
	}
	
	
	public static void showSurvey(byte[] payload) {

		msgs = payload;
		
		
		String message = new String(payload);
		
		if (message.startsWith("^^^")) {
			if (SurveyActivity.isHost) {
				if (surveyAnswersintent != null) {
					SurveyPlayerAnswersActivity.addAnswers(payload);
					
				} else {
					surveyAnswersintent = new Intent(ctx,
							SurveyPlayerAnswersActivity.class);
					surveyAnswersintent.putExtra("msgs", payload);
					ctx.startActivity(surveyAnswersintent);
					((Activity) ctx).finish();
				}
			}
			
		} else
		
		
		
		if (!ChordConnectionManager.getInstance().isHost) {

			all.setVisibility(View.VISIBLE);
			tvWaitingQuestions.setVisibility(View.GONE);
			
			score = 0;
			arrQuestions = new ArrayList<SurveyQuestion>();
			ArrayList<String> arrAnswers = new ArrayList<String>();

			String[] arrStr = message.split("\\^\\^").clone();

			String str;

			for (int i = 0; i < arrStr.length; i++) {

				str = arrStr[i];
				arrAnswers.clear();

				for (String strA : str.split("\\*\\*")[1].split("~~")) {
					arrAnswers.add(strA);
				}

				String correct = arrAnswers.remove(arrAnswers.size() - 1);

				arrQuestions.add(0, new SurveyQuestion(str.split("\\*\\*")[0],
						(ArrayList<String>) arrAnswers.clone(), correct));
			}

			for (int i = 0; i < arrQuestions.get(0).answers.size(); i++) {
				al.add(0, arrQuestions.get(0).answers.get(i));
				aa.notifyDataSetChanged();
			}
			lv.setItemChecked(0, true);

			correctAnswer = arrQuestions.get(0).correctAnswer;
			
			createSurveyButtons.setVisibility(View.GONE);
			answerSurveyButtons.setVisibility(View.VISIBLE);

			questions.setVisibility(View.GONE);
			questiontextView.setVisibility(View.VISIBLE);
			questiontextView.setText(arrQuestions.get(0).question);

			arrQuestions.remove(0);

			if (arrQuestions.size() == 0)
				nextQuestion.setText("Send answer");

		} /*else {
			deviceanswer.setVisibility(View.VISIBLE);
			devicename.setVisibility(View.VISIBLE);
			String[] splitans = message.split("<>");
			devicename.setText("Player Name\n"
					+ devicename.getText().toString() + "\n" + splitans[0]);

			deviceanswer.setText("Player Answer\n"
					+ deviceanswer.getText().toString() + "\n" + message);

			lv.setVisibility(View.GONE);

			questiontextView.setVisibility(View.GONE);
			answerSurveyButtons.setVisibility(View.GONE);
			createSurveyButtons.setVisibility(View.GONE);

		}*/
	}

}
