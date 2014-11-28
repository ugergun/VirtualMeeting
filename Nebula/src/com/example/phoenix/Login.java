package com.example.phoenix;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {
	private EditText text1;
	private EditText text2;
	private String username, isim, sifre;
	private String password;
	Button select;
	private CheckBox check;
	private ProgressDialog pDialog;
	String name, surname, job, image;
	int id;
	JSONParser jsonParser = new JSONParser();
	private static final String url_product_detials = "http://192.168.1.55/listenphp/getprofile.php";
	private static final String SPF_NAME = "vidslogin"; // <--- Add this
	private static final String USERNAME = "username"; // <--- To save username
	private static final String PASSWORD = "password";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		text1 = (EditText) findViewById(R.id.editText1);
		text2 = (EditText) findViewById(R.id.editText2);
		select = (Button) findViewById(R.id.button1);
		check = (CheckBox) findViewById(R.id.checkBox1);

		select.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				isim = text1.getText().toString();
				sifre = text2.getText().toString();
				if (null == isim || isim.length() == 0) {
					Toast.makeText(getApplicationContext(),
							"Kullanýcý adý giriniz", Toast.LENGTH_LONG).show();
					text1.requestFocus();
				} else if (null == sifre || sifre.length() == 0) {
					Toast.makeText(getApplicationContext(), "Þifre Giriniz",
							Toast.LENGTH_LONG).show();
					text2.requestFocus();
				} else {

					username = "'" + isim + "'";
					password = "'" + sifre + "'";
					new GetProductDetails().execute();

				}
			}
		});

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		return true;
	}

	class GetProductDetails extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Login.this);
			pDialog.setMessage("Giris Yapýlýyor...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {

			int success;
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("username", username));
			params.add(new BasicNameValuePair("password", password));
			JSONObject json = jsonParser.makeHttpRequest(url_product_detials,
					"GET", params);
			Log.d("Single Product Details", json.toString());
			try {
				success = json.getInt("success");
				if (success == 1) {
					if (check.isChecked()) {
						SharedPreferences loginPreferences = getSharedPreferences(
								SPF_NAME, Context.MODE_PRIVATE);
						Editor editor = loginPreferences.edit();
						editor.putString(USERNAME, isim);
						editor.putString(PASSWORD, sifre);
						editor.commit();
					}
					JSONArray productObj = json.getJSONArray("user");
					JSONObject c = productObj.getJSONObject(0);

					name = c.getString("name");
					surname = c.getString("surname");
					job = c.getString("job");
					image = c.getString("image");
					Intent i = new Intent(getApplicationContext(),
							MainActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
					finish();
					Intent in = new Intent(getApplicationContext(),
							AppPage.class);
					in.putExtra("job", job);
					in.putExtra("name", name);
					in.putExtra("surname", surname);
					in.putExtra("image", image);
					in.putExtra("username", isim);
					startActivityForResult(in, 100);
					finish();
				} else {
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(getApplicationContext(),
									"Hatalý Giriþ", Toast.LENGTH_LONG).show();
						}
					});

				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String file_url) {
			pDialog.dismiss();

		}
	}
}
