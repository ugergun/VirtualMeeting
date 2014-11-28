package com.example.phoenix;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Scheduler extends Activity {
	public int post;
	String multicast1, multicast2, port1, port2, time, number, count, fin,
			flag;
	String realno;
	String[] array;
	int p1, p2, num, counter, zaman, finish;
	MulticastSocket multiaudio;
	InetAddress audiogroup;
	ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	ListView list;
	LazyAdapter adapter;
	ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedular);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		list = (ListView) findViewById(R.id.list);
		Intent i = getIntent();
		multicast1 = i.getStringExtra("multicast");
		multicast2 = i.getStringExtra("multicast2");
		port1 = i.getStringExtra("port1");
		port2 = i.getStringExtra("port2");
		p1 = Integer.parseInt(port1);
		p2 = Integer.parseInt(port2);
		number = i.getStringExtra("number");
		time = i.getStringExtra("time");
		count = i.getStringExtra("count");
		fin = i.getStringExtra("fin");
		array = i.getStringArrayExtra("name");
		flag = i.getStringExtra("flag");
		counter = Integer.parseInt(count);
		zaman = Integer.parseInt(time);
		num = Integer.parseInt(number);
		finish = Integer.parseInt(fin);
		System.out.println(multicast1 + " " + multicast2 + " " + p1 + " " + p2
				+ " " + num + " " + zaman + " " + counter + " " + flag
				+ " scheduler");
		for (int j = 0; j < array.length; j++)
			System.out.println(array[j]);
		new Talkserver().execute();
		if (flag.equals("t"))
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					System.out.println("naber");
					adapter = new LazyAdapter(Scheduler.this, songsList);
					list.setAdapter(adapter);
					songsList = null;
					songsList = new ArrayList<HashMap<String, String>>();
					list.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {

							realno = ((TextView) view
									.findViewById(R.id.duration)).getText()
									.toString();
							post = Integer.parseInt(realno);
							if (post == 0) {
								multiaudio.close();
								while (true) {
									if (multiaudio.isClosed()) {
										finish();
										Intent in = new Intent(
												getApplicationContext(),
												Sender.class);
										in.putExtra("multicast", multicast1);
										in.putExtra("multicast2", multicast2);
										in.putExtra("port1", port1);
										in.putExtra("port2", port2);
										in.putExtra("time", time);
										in.putExtra("count", count);
										in.putExtra("number", "0");
										in.putExtra("name", array);
										startActivityForResult(in, 100);
									}
								}
							} else {
								Send();
								while (true) {
									if (multiaudio.isClosed()) {
										finish();
										Intent in = new Intent(
												getApplicationContext(),
												Receiver.class);
										in.putExtra("multicast", multicast1);
										in.putExtra("multicast2", multicast2);
										in.putExtra("port1", port1);
										in.putExtra("port2", port2);
										in.putExtra("time", time);
										in.putExtra("count", count);
										in.putExtra("number", "0");
										in.putExtra("name", array);
										startActivityForResult(in, 100);
										break;
									}
								}
							}

						}
					});
				}
			}, 1000 + counter * 600);

	}

	public void Send() {
		Thread rmessage = new Thread(new Runnable() {
			public void run() {
				try {
					System.out.println("sender");
					String s = "fine" + post;
					DatagramPacket hi = new DatagramPacket(s.getBytes(),
							s.getBytes().length, audiogroup, p2);
					multiaudio.send(hi);
					multiaudio.send(hi);
					multiaudio.send(hi);
					Thread.sleep(200);
					multiaudio.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		rmessage.start();

	}

	public class Talkserver extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			try {
				System.out.println("gönder");
				multiaudio = new MulticastSocket(p2);
				audiogroup = InetAddress.getByName(multicast2);
				multiaudio.joinGroup(audiogroup);
				String fin = "fin" + num;
				DatagramPacket hi = new DatagramPacket(fin.getBytes(),
						fin.getBytes().length, audiogroup, p2);
				multiaudio.send(hi);
				multiaudio.send(hi);
				multiaudio.send(hi);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (flag.equals("f")) {
				try {
					while (true) {
						byte[] buffer = new byte[64000];
						DatagramPacket packet = new DatagramPacket(buffer,
								buffer.length);
						multiaudio.receive(packet);
						System.out.println("calisiyo");
						if (buffer[0] == 'o' && buffer[1] == 'n'
								&& buffer[2] == 'l') {
							String s = new String(buffer, "UTF-8");
							char c = s.charAt(3);
							post = Integer.parseInt(c + "");
							Thread.sleep(counter * 400);
							if (post < finish) {
								multiaudio.close();
								while (true) {
									if (multiaudio.isClosed()) {
										finish();
										Intent in = new Intent(
												getApplicationContext(),
												Sender.class);
										in.putExtra("multicast", multicast1);
										in.putExtra("multicast2", multicast2);
										in.putExtra("port1", port1);
										in.putExtra("port2", port2);
										in.putExtra("time", time);
										in.putExtra("count", count);
										in.putExtra("number", "0");
										in.putExtra("name", array);
										startActivityForResult(in, 100);
										break;
									}
								}
							} else {
								Send();
								while (true) {
									if (multiaudio.isClosed()) {
										finish();
										Intent in = new Intent(
												getApplicationContext(),
												Receiver.class);
										in.putExtra("multicast", multicast1);
										in.putExtra("multicast2", multicast2);
										in.putExtra("port1", port1);
										in.putExtra("port2", port2);
										in.putExtra("time", time);
										in.putExtra("count", count);
										in.putExtra("number", "0");
										in.putExtra("name", array);
										startActivityForResult(in, 100);
										break;
									}
								}

							}
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (flag.equals("t")) {
				HashMap<String, String> pam = new HashMap<String, String>();
				pam.put("name", array[0]);
				pam.put("number", "0");
				songsList.add(pam);
				System.out.println("hey");
				try {
					while (true) {
						byte[] buffer = new byte[64000];
						DatagramPacket packet = new DatagramPacket(buffer,
								buffer.length);
						multiaudio.receive(packet);
						System.out.println("calisiyo");
						if (buffer[0] == 'o' && buffer[1] == 'n'
								&& buffer[2] == 'l') {
							String s = new String(buffer, "UTF-8");
							char c = s.charAt(3);
							post = Integer.parseInt(c + "");
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("name", array[post]);
							map.put("number", post + "");
							songsList.add(map);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return null;
		}

	}

}
