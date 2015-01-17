package com.ust.thesis.prototype.project.WeSync;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import net.sf.andpdf.nio.ByteBuffer;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
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
	static ProgressDialog progressDialog;
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.document_activity);
		button1 = (Button) findViewById(R.id.button1);
		imageView1 = (ImageView) findViewById(R.id.imageView1);

		walkdir(Environment.getExternalStorageDirectory());

		// Setup webview tonny
		wv = (WebView) findViewById(R.id.webView1);
		wv.getSettings().setBuiltInZoomControls(true);// show zoom buttons
		wv.getSettings().setSupportZoom(true);// allow zoom
		// get the width of the webview
		wv.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						ViewSize = wv.getWidth();
						wv.getViewTreeObserver().removeGlobalOnLayoutListener(
								this);
					}
				});

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

									try {

										File file = new File(musicpath[item]);
										/*
										 * File file = new File(Environment.
										 * getExternalStorageDirectory()
										 * .getAbsolutePath() +
										 * "/word-to-pdf.pdf/");
										 */
										RandomAccessFile f;
										f = new RandomAccessFile(file, "r");

										byte[] data = new byte[(int) f.length()];
										f.readFully(data);

										pdfLoadImages(DocumentActivity.this.getApplicationContext(),data);

										byte[][] payload = new byte[1][];
										payload[0] = data;

										ChordConnectionManager
												.getInstance()
												.sendData(
														payload,
														ChordMessageType.SHOW_DOCUMENT);
										
										wv.loadUrl("about:blank");
										progressDialog = ProgressDialog.show(
												DocumentActivity.this, "", "Opening...");

									} catch (FileNotFoundException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

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

//		progressDialog = new ProgressDialog(this);
		/*
		progressDialog.setMessage("Opening...");*/
		//.show(ctx, "", "Opening...");
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
		} else {
			bmpMsg = bmp;
		}
	}

	private static WebView wv;
	private static int ViewSize = 0;

	// Load Images:
	public static void pdfLoadImages(final Context ctx, final byte[] data) {
		try {
			// run async
			new AsyncTask<Void, Void, String>() {
				// create and show a progress dialog

				
				
				@Override
				protected void onPostExecute(String html) {
					// after async close progress dialog
					progressDialog.dismiss();

					// imageView1.setImageBitmap(scaled);
					// load the html in the webview

					wv.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");

				}

				@Override
				protected String doInBackground(Void... params) {
					try {
						// create pdf document object from bytes
						ByteBuffer bb = ByteBuffer.NEW(data);
						PDFFile pdf = new PDFFile(bb);
						// Get the first page from the pdf doc
						PDFPage PDFpage = pdf.getPage(1, true);
						// create a scaling value according to the WebView Width
						final float scale = ViewSize / PDFpage.getWidth()
								* 0.95f;
						// convert the page into a bitmap with a scaling value
						
						  Bitmap page = PDFpage.getImage( (int)
						  (PDFpage.getWidth() * scale), (int)
						  (PDFpage.getHeight() * scale), null, true, true);
						 
/*						Bitmap page = PDFpage.getImage((getWindowManager()
								.getDefaultDisplay().getWidth()),
								(getWindowManager().getDefaultDisplay()
										.getHeight()), null, true, true);
*/
						// save the bitmap to a byte array

						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						page.compress(Bitmap.CompressFormat.PNG, 100, stream);
						byte[] byteArray = stream.toByteArray();
						stream.reset();

						// convert the byte array to a base64 string
						String base64 = Base64.encodeToString(byteArray,
								Base64.NO_WRAP);

						// create the html + add the first image to the html
						String html = "<!DOCTYPE html><html><body bgcolor=\"#b4b4b4\"><img src=\"data:image/png;base64,"
								+ base64 + "\" hspace=10 vspace=10><br>";
						// loop though the rest of the pages and repeat the
						// above
						for (int i = 1; i < pdf.getNumPages(); i++) {
							PDFpage = pdf.getPage(i, true);
							page = PDFpage.getImage(
									(int) (PDFpage.getWidth() * scale),
									(int) (PDFpage.getHeight() * scale), null,
									true, true);
							page.compress(Bitmap.CompressFormat.PNG, 100,
									stream);
							byteArray = stream.toByteArray();
							stream.reset();
							base64 = Base64.encodeToString(byteArray,
									Base64.NO_WRAP);
							html += "<img src=\"data:image/png;base64,"
									+ base64 + "\" hspace=10 vspace=10><br>";
						}
						stream.close();
						html += "</body></html>";
						return html;

					} catch (Exception e) {
						Log.d("error", e.toString());
					}
					return null;
				}
			}.execute();
			System.gc();// run GC
		} catch (Exception e) {
			Log.d("error", e.toString());
		}
	}

}