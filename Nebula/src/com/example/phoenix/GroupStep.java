package com.example.phoenix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class GroupStep extends Fragment {
	private ProgressDialog pDialog;
	private EditText gname, pass;
	private Spinner time, count;
	private String groupname, password, timer, counter, username;
	Button okey;
	private boolean flag = false;
	private static final String TAG_SUCCESS = "success";
	JSONParser jsonParser2 = new JSONParser();
	JSONParser jsonParser = new JSONParser();

	private static String url_create = "http://192.168.1.55/listenphp/creategroup.php";
	private static String url_create2 = "http://192.168.1.55/listenphp/create.php";

	public GroupStep(String username) {
		this.username = username;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		View rootView = inflater.inflate(R.layout.group_step, container, false);
		gname = (EditText) rootView.findViewById(R.id.editText1);
		pass = (EditText) rootView.findViewById(R.id.editText2);
		time = (Spinner) rootView.findViewById(R.id.spinner1);
		count = (Spinner) rootView.findViewById(R.id.spinner2);
		okey = (Button) rootView.findViewById(R.id.button1);
		ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(),
				R.array.kisi, R.layout.spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ArrayAdapter adapter2 = ArrayAdapter.createFromResource(getActivity(),
				R.array.zaman, R.layout.spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		time.setAdapter(adapter2);
		count.setAdapter(adapter);
		time.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if (arg2 == 1)
					timer = "1";
				else if (arg2 == 2)
					timer = "2";
				else if (arg2 == 3)
					timer = "3";
				else if (arg2 == 4)
					timer = "6";
				else if (arg2 == 5)
					timer = "12";
				else if (arg2 == 6)
					timer = "18";
				else if (arg2 == 7)
					timer = "24";
				else if (arg2 == 8)
					timer = "30";
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		count.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if (arg2 == 1)
					counter = "3";
				else if (arg2 == 2)
					counter = "4";
				else if (arg2 == 3)
					counter = "5";
				else if (arg2 == 4)
					counter = "6";
				else if (arg2 == 5)
					counter = "7";
				else if (arg2 == 6)
					counter = "8";
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		
		okey.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				groupname = gname.getText().toString();
				password = pass.getText().toString();

				flag = false;
				new CreateNewProduct().execute();

			}
		});
		rootView.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				InputMethodManager imm = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getActivity().getCurrentFocus()
						.getWindowToken(), 0);

				return true;
			}
		});
		rootView.requestFocus();
		rootView.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.frame_container, new Group(username))
							.commit();
					return true;
				} else {
					return false;
				}
			}
		});
		return rootView;
	}

	class CreateNewProduct extends AsyncTask<String, String, String> {
		String multicast;
		String multicast2;
		String port1;
		String port2;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Kaydiniz Olusturuluyor...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {

			multicast = randomly();
			multicast2 = randomly();
			port1 = randomport();
			port2 = randomport();
			System.out.println(multicast + " " + multicast2 + " " + port1 + " "
					+ port2);
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("isim", groupname));
			params.add(new BasicNameValuePair("sifre", password));
			params.add(new BasicNameValuePair("multicast", multicast));
			params.add(new BasicNameValuePair("multicast2", multicast2));
			params.add(new BasicNameValuePair("user", username));
			params.add(new BasicNameValuePair("zaman", timer));
			params.add(new BasicNameValuePair("sayi", counter));
			params.add(new BasicNameValuePair("port1", port1));
			params.add(new BasicNameValuePair("port2", port2));

			JSONObject json = jsonParser2.makeHttpRequest(url_create, "POST",
					params);
			Log.d("Create Response", json.toString());
			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					System.out.println("basarýlý");
				} else {
					System.out.println("basarýsýz");
					flag = true;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (!flag) {
				List<NameValuePair> params2 = new ArrayList<NameValuePair>();

				params2.add(new BasicNameValuePair("user", username));
				params2.add(new BasicNameValuePair("grup", groupname));

				JSONObject json2 = jsonParser.makeHttpRequest(url_create2,
						"POST", params2);
				Log.d("Create Response", json2.toString());
				try {
					int success = json2.getInt(TAG_SUCCESS);
					if (success == 1) {
						System.out.println("basarýlý");
					} else {
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("olmadi");
			}
			return null;
		}

		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			if (!flag) {
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager
						.beginTransaction()
						.replace(
								R.id.frame_container,
								new NewMeet(groupname, username, multicast,
										multicast2, port1, port2, timer, "0",
										counter, true)).commit();
			} else {
				Toast.makeText(getActivity().getApplicationContext(),
						"Grup Kurulamadi", Toast.LENGTH_LONG).show();
			}
		}

		public String randomly() {
			Random rand = new Random();
			int n = rand.nextInt(15) + 224;
			int m = rand.nextInt(255);
			int t = rand.nextInt(255);
			int f = rand.nextInt(255);
			return n + "." + m + "." + t + "." + f;
		}

		public String randomport() {
			Random rand = new Random();
			int f = rand.nextInt(8999) + 1000;
			return f + "";
		}
	}

}
