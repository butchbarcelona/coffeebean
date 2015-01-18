package com.ust.thesis.prototype.project.WeSync;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SurveyPlayerAnswersActivity extends ChordActivity {

	static ListView lv;
	static ListAdapter lva;
	static HashMap<String, ArrayList<String>> answers = new HashMap<String, ArrayList<String>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.survey_answers_activity);
		lv = (ListView) findViewById(R.id.lv_survey_answers);
		/*
		 * al = new ArrayList<String>();// initialize array list al = new
		 * ArrayList<String>(); al.clear();
		 * 
		 * aa = new ArrayAdapter<String>(this,
		 * android.R.layout.simple_list_item_checked //
		 * ,R.layout.survey_list_item // ,R.id.tv_poll_answer , al);// step4 :
		 * establish
		 * 
		 * lv.setAdapter(aa);
		 */

		lva = new ListAdapter(this);
		lv.setAdapter(lva);

		addAnswers(this.getIntent().getByteArrayExtra("msgs"));

	}

	public static void addAnswers(byte[] payload) {
		String message = new String(payload);
		ArrayList<String> arrAnswers = null;
		
		String name = "";
		for (String str : message.split("<>")) {
			if (arrAnswers == null) {
				arrAnswers = new ArrayList<String>();
				name = str.split("\\^\\^\\^")[1];
			}else
				arrAnswers.add(str);
		}

		answers.put(name, arrAnswers);

		lva.notifyDataSetChanged();
		//lv.invalidate();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}
	
	public class ListAdapter extends BaseAdapter {

		Context ctx;

		public ListAdapter(Context context) {
			super();

			ctx = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View v = convertView;

			if (v == null) {

				LayoutInflater vi;
				vi = LayoutInflater.from(ctx);
				v = vi.inflate(R.layout.survey_answers_item, null);
			}

			TextView tvPlayer = (TextView) v.findViewById(R.id.tvPlayerName);
			TextView tvAnswers = (TextView) v.findViewById(R.id.tvAnswers);
			TextView tvScore = (TextView) v.findViewById(R.id.tvScore);

			String key = String.valueOf(answers.keySet().toArray()[position]);
			
			
			
			
			tvPlayer.setText(key);

			ArrayList<String> strAnswers = answers.get(key);

			String answers = "";
			
			String[] arrAnswer;
			
			for (String str : strAnswers) {
				arrAnswer = str.split("\\|\\|");
				if(arrAnswer.length > 1){
					tvScore.setText(arrAnswer[1]);
				}				
				answers += str + "\n";
			}

			tvAnswers.setText(answers);
			return v;

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return answers.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

	}
}
