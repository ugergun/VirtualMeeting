package com.example.phoenix;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class Receiver extends Activity {

	ImageView img;
	String multicast1, multicast2, port1, port2, time, number, count, flag;
	String[] array;
	int num, counter;
	MulticastSocket multicast;
	InetAddress group;
	MulticastSocket multiaudio;
	CheckBox check1, check2;
	Button stop;
	InetAddress audiogroup;
	ProgressDialog pDialog;
	boolean f1 = false, f2 = false;
	int p1, p2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.receiver);
		Intent i = getIntent();
		flag = "f";
		multicast1 = i.getStringExtra("multicast");
		multicast2 = i.getStringExtra("multicast2");
		port1 = i.getStringExtra("port1");
		port2 = i.getStringExtra("port2");
		p1 = Integer.parseInt(port1);
		p2 = Integer.parseInt(port2);
		number = i.getStringExtra("number");
		array = i.getStringArrayExtra("name");
		time = i.getStringExtra("time");
		count = i.getStringExtra("count");
		num = Integer.parseInt(number);
		counter = Integer.parseInt(count);
		System.out.println(multicast1 + " " + multicast2 + " " + p1 + " " + p2
				+ " " + num + " " + time + " " + counter + "receiver");
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		img = (ImageView) findViewById(R.id.imageView1);
		check1 = (CheckBox) findViewById(R.id.checkBox1);
		check2 = (CheckBox) findViewById(R.id.checkBox2);
		stop = (Button) findViewById(R.id.button1);
		check1.setChecked(true);
		check1.setVisibility(View.INVISIBLE);
		check2.setVisibility(View.INVISIBLE);
		stop.setVisibility(View.INVISIBLE);
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		MulticastLock multicastLock = wifi.createMulticastLock("multicastLock");
		multicastLock.setReferenceCounted(true);
		multicastLock.acquire();
		if (multicastLock != null) {
			multicastLock.release();
			multicastLock = null;
		}
		try {
			multicast = new MulticastSocket(p1);
			group = InetAddress.getByName(multicast1);
			multicast.joinGroup(group);
			multiaudio = new MulticastSocket(p2);
			audiogroup = InetAddress.getByName(multicast2);
			multiaudio.joinGroup(audiogroup);
			new TalkWithServer().execute(multicast);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (num == 0) {
			check2.setVisibility(View.VISIBLE);
			stop.setVisibility(View.VISIBLE);
			Thread audioThread2 = new Thread(new Runnable() {
				public void run() {
					try {
						int buffsize = AudioTrack.getMinBufferSize(44100,
								AudioFormat.CHANNEL_OUT_MONO,
								AudioFormat.ENCODING_PCM_16BIT);
						AudioTrack audioTrack = new AudioTrack(
								AudioManager.STREAM_MUSIC, 44100,
								AudioFormat.CHANNEL_OUT_MONO,
								AudioFormat.ENCODING_PCM_16BIT, buffsize,
								AudioTrack.MODE_STREAM);
						audioTrack.play();
						while (true) {
							byte[] buffer = new byte[64000];
							DatagramPacket packet = new DatagramPacket(buffer,
									buffer.length);
							multiaudio.receive(packet);
							if (buffer[0] == 'f' && buffer[1] == 'i'
									&& buffer[2] == 'n') {
								if (buffer[3] != 'e') {
									String s = new String(buffer, "UTF-8");
									char c = s.charAt(3);
									int i = Integer.parseInt(c + "");
									if (check2.isChecked()) {
										flag = "t";
									} else
										flag = "f";
									multicast.close();
									multiaudio.close();
									finish();
									Intent in = new Intent(
											getApplicationContext(),
											Scheduler.class);
									in.putExtra("multicast", multicast1);
									in.putExtra("multicast2", multicast2);
									in.putExtra("port1", port1);
									in.putExtra("port2", port2);
									in.putExtra("time", time);
									in.putExtra("count", count);
									in.putExtra("number", number);
									in.putExtra("fin", i + "");
									in.putExtra("name", array);
									in.putExtra("flag", flag);
									startActivityForResult(in, 100);

								}

							} else {
								audioTrack.write(buffer, 0, 3904);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			audioThread2.start();
		} else {
			check1.setVisibility(View.VISIBLE);
			Thread audioThread = new Thread(new Runnable() {
				public void run() {
					try {
						int buffsize = AudioTrack.getMinBufferSize(44100,
								AudioFormat.CHANNEL_OUT_MONO,
								AudioFormat.ENCODING_PCM_16BIT);
						AudioTrack audioTrack = new AudioTrack(
								AudioManager.STREAM_MUSIC, 44100,
								AudioFormat.CHANNEL_OUT_MONO,
								AudioFormat.ENCODING_PCM_16BIT, buffsize,
								AudioTrack.MODE_STREAM);
						audioTrack.play();
						while (true) {
							byte[] buffer = new byte[64000];
							DatagramPacket packet = new DatagramPacket(buffer,
									buffer.length);
							multiaudio.receive(packet);
							if (buffer[0] == 'f' && buffer[1] == 'i'
									&& buffer[2] == 'n') {
								System.out.println("gelen  "+ f1);
								if (buffer[3] == 'e') {
									f1 = false;
									f2 = true;
									String s = new String(buffer, "UTF-8");
									char c = s.charAt(4);
									int i = Integer.parseInt(c + "");
									if (num == i) {
										System.out.println("bende");
										Thread.sleep(200);
										multiaudio.close();
										multicast.close();
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
										in.putExtra("number", number);
										startActivityForResult(in, 100);
									}
								}

								else if (check1.isChecked() && f1 == false) {
									System.out.println("þimdi burada");
									f2 = false;
									String s = new String(buffer, "UTF-8");
									char c = s.charAt(3);
									int i = Integer.parseInt(c + "");
									if (num > i) {
										Thread.sleep(1000 + (num - i) * 200);
										Send();
									} else {
										Thread.sleep(1000 + (counter - (i - num)) * 200);
										Send();
									}
								}
								if (f2 == false)
									f1 = true;

							} else {
								f1 = false ; f2 = false;
								audioTrack.write(buffer, 0, 3904);
							}

						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			audioThread.start();
		}

	}

	public void Send() {
		Thread rmessage = new Thread(new Runnable() {
			public void run() {
				try {
					String s = "onl" + num;
					DatagramPacket hi = new DatagramPacket(s.getBytes(),
							s.getBytes().length, audiogroup, p2);
					multiaudio.send(hi);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		rmessage.start();

	}

	public class TalkWithServer extends
			AsyncTask<MulticastSocket, Bitmap, Void> {

		@Override
		protected Void doInBackground(MulticastSocket... params) {

			try {
				MulticastSocket multicast = params[0];

				while (true) {
					byte[] buffer = new byte[64000];
					DatagramPacket packet = new DatagramPacket(buffer,
							buffer.length);
					multicast.receive(packet);
					Bitmap bmp = BitmapFactory.decodeByteArray(buffer, 0,
							buffer.length);
					System.out.println(bmp.getHeight() + " " + bmp.getWidth());
					publishProgress(bmp);

				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Bitmap... values) {
			super.onProgressUpdate(values);
			Bitmap bmp = values[0];
			img.setImageBitmap(bmp);
		}

	}
}