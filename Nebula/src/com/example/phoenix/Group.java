package com.example.phoenix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Group extends Fragment {
	Button create, join;
	private String groupname, count, multicast, multicast2, port1,
			port2, time, number, user;
	String username;
	ListView list;
	LazyAdapter adapter;
	ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	public final static String NAME = "name";
	public final static String JOB = "job";
	private static String url = "http://192.168.1.55/listenphp/group.php";
	private static String url2 = "http://192.168.1.55/listenphp/listgroup.php";
	boolean flag2 = false;
	JSONParser jsonParser = new JSONParser();
	private ProgressDialog pDialog2, pDialog;

	public Group(String username) {
		this.username = username;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.group, container, false);
		create = (Button) rootView.findViewById(R.id.button2);
		list = (ListView) rootView.findViewById(R.id.list);
		new GetGroup().execute();
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				groupname = ((TextView) view.findViewById(R.id.title))
						.getText().toString();
				flag2 = false;
				new GroupItem().execute();

			}
		});

		create.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, new GroupStep(username))
						.commit();

			}
		});
		join = (Button) rootView.findViewById(R.id.button1);
		join.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, new JoinGroup(username))
						.commit();

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
			params.add(new BasicNameValuePair("isim", "'" + username + "'"));
			JSONObject json = jsonParser.makeHttpRequest(url, "GET", params);
			Log.d("Single Product Details", json.toString());
			try {
				int success = json.getInt("success");
				if (success == 1) {
					JSONArray productObj = json.getJSONArray("profil");
					for (int i = 0; i < productObj.length(); i++) {
						JSONObject c = productObj.getJSONObject(i);
						String groupname = c.getString("isim");
						String manager = c.getString("manager");
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(NAME, groupname);
						map.put(JOB, manager);
						songsList.add(map);

					}
				}
			} catch (Exception e) {

			}
			return null;
		}

		protected void onPostExecute(String file_url) {
			pDialog2.dismiss();
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

	class GroupItem extends AsyncTask<String, String, String> {
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
			params.add(new BasicNameValuePair("user", "'" + username + "'"));
			JSONObject json = jsonParser.makeHttpRequest(url2, "GET", params);
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
					number = c.getString("number");
					count = c.getString("sayi");
					if (username.equals(user)) {
						flag2 = true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager
					.beginTransaction()
					.replace(
							R.id.frame_container,
							new NewMeet(groupname, username, multicast,
									multicast2, port1, port2, time, number,
									count, flag2)).commit();

		}
	}

}
