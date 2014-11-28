package com.example.phoenix;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class JoinGroup extends Fragment {
	private String username, groupname, password, count, multicast, multicast2,
			port1, port2, time, number, user;
	private ProgressDialog pDialog2;
	private static String url_create2 = "http://192.168.1.55/listenphp/create.php";
	private static String url_get = "http://192.168.1.55/listenphp/getuser.php";
	private static String url = "http://192.168.1.55/listenphp/getgroup.php";
	private EditText gname, pass;
	private boolean flag = false;
	private boolean flag2 = false;
	private boolean flag3 = false;
	private int length, clength;
	Button okey;
	JSONParser jsonParser = new JSONParser();
	JSONParser jsonParser2 = new JSONParser();
	JSONParser jsonParser3 = new JSONParser();

	public JoinGroup(String username) {
		this.username = username;
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.join_group, container, false);
		gname = (EditText) rootView.findViewById(R.id.editText1);
		pass = (EditText) rootView.findViewById(R.id.editText2);
		okey = (Button) rootView.findViewById(R.id.button1);
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		okey.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				groupname = gname.getText().toString();
				password = pass.getText().toString();
				flag = false;
				flag2 = false;
				flag3 = false;
				new GetGroup().execute();

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
		rootView.setFocusableInTouchMode(true);
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

	class GetGroup extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog2 = new ProgressDialog(getActivity());
			pDialog2.setMessage("Giris Yapýlýyor...");
			pDialog2.setIndeterminate(false);
			pDialog2.setCancelable(true);
			pDialog2.show();
		}

		protected String doInBackground(String... args) {

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("isim", "'" + groupname + "'"));
			params.add(new BasicNameValuePair("sifre", "'" + password + "'"));
			JSONObject json = jsonParser.makeHttpRequest(url, "GET", params);
			Log.d("Single Product Details", json.toString());
			try {
				int success = json.getInt("success");
				if (success == 1) {
					JSONArray productObj = json.getJSONArray("grup");
					JSONObject c = productObj.getJSONObject(0);

					multicast = c.getString("multicast");
					multicast2 = c.getString("multicast2");
					port1 = c.getString("port1");
					port2 = c.getString("port2");
					time = c.getString("zaman");
					user = c.getString("manager");
					count = c.getString("sayi");
					clength = Integer.parseInt(count);
					if (username.equals(user)) {
						flag2 = true;
					}

					System.out.println(multicast + " " + " " + username + " "
							+ user);

				} else {
					flag3 = true;
				}
			} catch (JSONException e) {
				e.printStackTrace();

			}
			if (!flag3) {
				List<NameValuePair> params2 = new ArrayList<NameValuePair>();
				params2.add(new BasicNameValuePair("isim", "'" + groupname
						+ "'"));
				JSONObject json2 = jsonParser2.makeHttpRequest(url_get, "GET",
						params2);
				Log.d("Single Product Details", json2.toString());
				try {
					int success2 = json.getInt("success");
					if (success2 == 1) {
						JSONArray productObj = json2.getJSONArray("profil");
						length = productObj.length();
						for (int i = 0; i < productObj.length(); i++) {
							JSONObject c = productObj.getJSONObject(i);
							String u = c.getString("user");
							if (username.equals(u)) {
								System.out.println("girdi");
								number = c.getString("number");
								flag = true;
								break;
							}
						}

					} else {
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (!flag) {
					if (length < clength) {
						List<NameValuePair> params3 = new ArrayList<NameValuePair>();

						params3.add(new BasicNameValuePair("user", username));
						params3.add(new BasicNameValuePair("grup", groupname));

						JSONObject json3 = jsonParser3.makeHttpRequest(
								url_create2, "POST", params3);
						Log.d("Create Response", json3.toString());
						try {
							int success3 = json3.getInt("success");
							if (success3 == 1) {
								System.out.println("basarýlý");
								List<NameValuePair> params4 = new ArrayList<NameValuePair>();
								params4.add(new BasicNameValuePair("isim", "'"
										+ groupname + "'"));
								JSONObject json4 = jsonParser2.makeHttpRequest(
										url_get, "GET", params4);
								Log.d("Single Product Details",
										json4.toString());
								try {
									int success4 = json.getInt("success");
									if (success4 == 1) {
										JSONArray productObj = json4
												.getJSONArray("profil");
										for (int i = 0; i < productObj.length(); i++) {
											JSONObject c = productObj
													.getJSONObject(i);
											String u = c.getString("user");
											if (username.equals(u)) {
												System.out.println("girdi2");
												number = c.getString("number");
											}
										}
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else {
						System.out.println("elaman fazlaliliði");
					}
				} else {
					System.out.println("mevcut kayit var");
				}
			} else {
				System.out.println("grup yok");
			}

			return null;
		}

		protected void onPostExecute(String file_url) {
			pDialog2.dismiss();
			if (flag3) {
				Toast.makeText(getActivity().getApplicationContext(),
						"Grup bulunamadi", Toast.LENGTH_LONG).show();
			} else if (!flag && length >= clength) {
				Toast.makeText(getActivity().getApplicationContext(),
						"Eleman Fazlaligi", Toast.LENGTH_LONG).show();
			} else {
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager
						.beginTransaction()
						.replace(
								R.id.frame_container,
								new NewMeet(groupname, username,
										multicast, multicast2, port1, port2,
										time, number, count, flag2)).commit();
			}

		}
	}

}
