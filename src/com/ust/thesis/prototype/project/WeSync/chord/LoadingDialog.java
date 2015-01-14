package com.ust.thesis.prototype.project.WeSync.chord;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class LoadingDialog extends AsyncTask<Void, Void, Void>{

	ProgressDialog progress;
	LoadingDialogInterface ldInterface;
	String msg;
	Context ctx;
	
	
	public LoadingDialog(Context ctx, String msg, LoadingDialogInterface intrface ) {
		this.ldInterface = intrface;
		this.ctx = ctx;
		this.msg = msg;
	}
	
	
	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub

		ldInterface.onLoading();
		return null;
	}

	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();

		
		progress = ProgressDialog.show(ctx, "",
			    "msg", true);	
		
		
		ldInterface.onStartLoading();
	}
	
	
	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		progress.dismiss();
		
		ldInterface.onFinishLoading();
	}
	
}
