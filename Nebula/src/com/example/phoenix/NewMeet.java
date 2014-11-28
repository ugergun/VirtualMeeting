package com.example.phoenix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class NewMeet extends Fragment {
	String groupname, username, multicast, multicast2, port1, port2, time,
			count, number, user;
	private int realno;
	boolean flag;
	public final static String NAME = "name";
	public final static String JOB = "job";
	public final static String PICTURE = "picture";
	public final static String NUMBER = "number";
	public ProgressDialog pDialog2, pDialog;
	private Button start, ready, refresh;
	private TextView text;
	ListView list;
	LazyAdapter adapter;
	String[] array, array2;
	private static String url = "http://192.168.1.55/listenphp/users.php";
	JSONParser jsonParser2 = new JSONParser();
	JSONParser jsonParser = new JSONParser();
	ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

	public NewMeet(String groupname, String username, String multicast,
			String multicast2, String port1, String port2, String time,
			String number, String count, boolean flag) {
		this.groupname = groupname;
		this.username = username;
		this.multicast = multicast;
		this.multicast2 = multicast2;
		this.port1 = port1;
		this.port2 = port2;
		this.time = time;
		this.number = number;
		this.flag = flag;
		this.count = count;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.newmeet, container, false);
		list = (ListView) rootView.findViewById(R.id.list);
		start = (Button) rootView.findViewById(R.id.button1);
		ready = (Button) rootView.findViewById(R.id.button3);
		refresh = (Button) rootView.findViewById(R.id.button2);
		text = (TextView) rootView.findViewById(R.id.textView1);
		text.setText(groupname);
		start.setVisibility(View.INVISIBLE);
		ready.setVisibility(View.INVISIBLE);
		System.out.println(multicast + " " + multicast2 + " " + port1 + " "
				+ port2 + " " + time + " " + number + " " + flag);
		new GetUsers().execute();
		if (flag) {
			start.setVisibility(View.VISIBLE);
		} else {
			ready.setVisibility(View.VISIBLE);
		}

		start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new GetName().execute();
			}
		});
		ready.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new GetNumbers().execute();
			}
		});
		refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new GetUsers().execute();
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

	class GetName extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Giris Yapýlýyor...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("isim", "'" + groupname + "'"));
			JSONObject json = jsonParser.makeHttpRequest(url, "GET", params);
			Log.d("Single Product Details", json.toString());
			try {
				int success = json.getInt("success");
				if (success == 1) {
					JSONArray productObj = json.getJSONArray("profil");
					array = new String[productObj.length()];
					array2 = new String[productObj.length()];
					for (int i = 0; i < productObj.length(); i++) {
						JSONObject c = productObj.getJSONObject(i);
						String uname = c.getString("user");
						array[i] = c.getString("name") + " "
								+ c.getString("surname");
						array2[i] = "http://192.168.1.55/media/uploads/"
								+ uname + "/" + c.getString("image");
					}

				} else {
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			Intent i = new Intent("view.holder.sender");
			i.putExtra("multicast", multicast);
			i.putExtra("multicast2", multicast2);
			i.putExtra("port1", port1);
			i.putExtra("port2", port2);
			i.putExtra("time", time);
			i.putExtra("count", count);
			i.putExtra("number", "0");
			i.putExtra("name", array);
			i.putExtra("image", array2);
			startActivityForResult(i, 100);
			getActivity().finish();
		}
	}

	class GetNumbers extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Giris Yapýlýyor...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("isim", "'" + groupname + "'"));
			JSONObject json = jsonParser.makeHttpRequest(url, "GET", params);
			Log.d("Single Product Details", json.toString());
			try {
				int success = json.getInt("success");
				if (success == 1) {
					JSONArray productObj = json.getJSONArray("profil");

					for (int i = 0; i < productObj.length(); i++) {
						JSONObject c = productObj.getJSONObject(i);
						String no = c.getString("number");
						System.out.println(number + " " + no);
						if (number.equals(no)) {
							realno = i;
							System.out.println("esit");
						}

					}

				} else {
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			Intent i = new Intent("view.holder.receiver");
			i.putExtra("multicast", multicast);
			i.putExtra("multicast2", multicast2);
			i.putExtra("port1", port1);
			i.putExtra("port2", port2);
			i.putExtra("time", time);
			i.putExtra("number", realno + "");
			i.putExtra("count", count);
			startActivityForResult(i, 100);
			getActivity().finish();
		}
	}

	class GetUsers extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Giris Yapýlýyor...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("isim", "'" + groupname + "'"));
			JSONObject json = jsonParser.makeHttpRequest(url, "GET", params);
			Log.d("Single Product Details", json.toString());
			try {
				int success = json.getInt("success");
				if (success == 1) {
					JSONArray productObj = json.getJSONArray("profil");
					for (int i = 0; i < productObj.length(); i++) {
						JSONObject c = productObj.getJSONObject(i);
						String uname = c.getString("user");
						String name = c.getString("name");
						String surname = c.getString("surname");
						String job = c.getString("job");
						String image = "http://192.168.1.55/media/uploads/"
								+ uname + "/" + c.getString("image");
						String no = c.getString("number");
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(NAME, name + " " + surname);
						map.put(JOB, job);
						map.put(PICTURE, image);
						map.put(NUMBER, no);
						songsList.add(map);
					}

				} else {
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					adapter = new LazyAdapter(getActivity(), songsList);
					list.setAdapter(adapter);
					songsList = null;
					songsList = new ArrayList<HashMap<String, String>>();
				}
			});

		}
	}
}
