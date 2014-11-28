package com.example.phoenix;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Button register;
	private Button login;
	private String username, uname;
	private String password;
	private ProgressDialog pDialog;
	String name, surname, job, image;
	private static final String SPF_NAME = "vidslogin"; // <--- Add this
	private static final String USERNAME = "username"; // <--- To save username
	private static final String PASSWORD = "password";
	JSONParser jsonParser = new JSONParser();
	private static final String url_product_detials = "http://192.168.1.55/listenphp/getprofile.php";
	SharedPreferences sharedpreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		login = (Button) findViewById(R.id.button1);
		sharedpreferences = getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);

		if (isOnline()) {
			if (sharedpreferences.contains(USERNAME)
					&& sharedpreferences.contains(PASSWORD)) {
				uname = sharedpreferences.getString(USERNAME, "");
				username = "'" + uname + "'";
				password = "'" + sharedpreferences.getString(PASSWORD, "")
						+ "'";
				new GetProductDetails().execute();
			}
		} else {
			Toast.makeText(getApplicationContext(),
					"Ýnternet Baðlantýsýný Kontrol Edin", Toast.LENGTH_LONG)
					.show();
		}
		login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				if (isOnline())
					if (sharedpreferences.contains(USERNAME)
							&& sharedpreferences.contains(PASSWORD)) {
						uname = sharedpreferences.getString(USERNAME, "");
						username = "'" + uname + "'";
						password = "'"
								+ sharedpreferences.getString(PASSWORD, "")
								+ "'";
						new GetProductDetails().execute();
					} else {
						Intent in = new Intent(getApplicationContext(),
								Login.class);
						startActivityForResult(in, 100);
					}

			}
		});
		register = (Button) findViewById(R.id.button2);
		register.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				if (isOnline()) {
					Intent in = new Intent(getApplicationContext(),
							Register.class);
					startActivityForResult(in, 100);
				}

			}
		});

		/*
		 * new Handler().postDelayed(new Runnable() {
		 * 
		 * @Override public void run() {
		 * 
		 * } }, 1500);
		 */
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	class GetProductDetails extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity.this);
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
					JSONArray productObj = json.getJSONArray("user");
					JSONObject c = productObj.getJSONObject(0);

					name = c.getString("name");
					surname = c.getString("surname");
					job = c.getString("job");
					image = c.getString("image");

					Intent in = new Intent(getApplicationContext(),
							AppPage.class);
					in.putExtra("job", job);
					in.putExtra("name", name);
					in.putExtra("surname", surname);
					in.putExtra("image", image);
					in.putExtra("username", uname);

					startActivityForResult(in, 100);
					finish();
				} else {
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
