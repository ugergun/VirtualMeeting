package com.example.phoenix;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

public class Sender extends Activity {
	String multicast1, multicast2, port1, port2, time, number, count, flag;
	int p1, p2, num, counter, zaman;
	String[] array, array2;
	private SurfaceView preview = null;
	private SurfaceHolder previewHolder = null;
	private Camera camera = null;
	private CheckBox check;
	private boolean inPreview = false;
	private boolean cameraConfigured = false;
	public ImageView imgView1;
	AudioRecord ar = null;
	int buffsize = 0;
	int blockSize = 256;
	boolean isRecording = false;
	private Thread recordingThread = null;
	private Button button;
	private MulticastSocket socket;
	private InetAddress group;
	private MulticastSocket socket2;
	private InetAddress group2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sender);
		flag = "f";
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
		array = i.getStringArrayExtra("name");
		num = Integer.parseInt(number);
		counter = Integer.parseInt(count);
		zaman = Integer.parseInt(time);
		check = (CheckBox) findViewById(R.id.checkBox1);
		System.out.println(multicast1 + " " + multicast2 + " " + p1 + " " + p2
				+ " " + num + " " + zaman + " " + counter + "sender");
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		MulticastLock multicastLock = wifi.createMulticastLock("multicastLock");
		multicastLock.setReferenceCounted(true);
		multicastLock.acquire();
		if (multicastLock != null) {
			multicastLock.release();
			multicastLock = null;
		}
		try {
			group = InetAddress.getByName(multicast1);
			socket = new MulticastSocket(p1);
			socket.joinGroup(group);
			group2 = InetAddress.getByName(multicast2);
			socket2 = new MulticastSocket(p2);
			socket2.joinGroup(group2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		baslat();
		preview = (SurfaceView) findViewById(R.id.preview);
		previewHolder = preview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		if (num == 0) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if (check.isChecked())
						flag = "t";
					else
						flag = "f";
					socket.close();
					durdur();
					onPause();
					finish();
					socket2.close();
					while (true) {
						if (socket2.isClosed()) {
							Intent in = new Intent(getApplicationContext(),
									Scheduler.class);
							in.putExtra("multicast", multicast1);
							in.putExtra("multicast2", multicast2);
							in.putExtra("port1", port1);
							in.putExtra("port2", port2);
							in.putExtra("time", time);
							in.putExtra("count", count);
							in.putExtra("number", "0");
							in.putExtra("fin", "0");
							in.putExtra("name", array);
							in.putExtra("image", array2);
							in.putExtra("flag", flag);
							startActivityForResult(in, 100);
							break;
						}
					}
				}
			}, zaman * 10000);
		} else {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					socket.close();
					durdur();
					Sendmessage();
					onPause();
					finish();
					Intent in = new Intent(getApplicationContext(),
							Receiver.class);
					in.putExtra("multicast", multicast1);
					in.putExtra("multicast2", multicast2);
					in.putExtra("port1", port1);
					in.putExtra("port2", port2);
					in.putExtra("time", time);
					in.putExtra("count", count);
					in.putExtra("number", number);
					startActivityForResult(in, 100);
				}
			}, zaman * 10000);

		}
		button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

			}
		});

	}

	@Override
	public void onResume() {
		super.onResume();
		camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
		startPreview();
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (camera != null) {
			camera.stopPreview();
			camera.setPreviewCallback(null);
			camera.release();
			camera = null;
		}

	}

	private Camera.Size getBestPreviewSize(int width, int height,
			Camera.Parameters parameters) {
		Camera.Size result = null;

		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width <= width && size.height <= height) {
				if (result == null) {
					result = size;
				} else {
					int resultArea = result.width * result.height;
					int newArea = size.width * size.height;

					if (newArea > resultArea) {
						result = size;
					}
				}
			}
		}

		return (result);
	}

	private void initPreview(int width, int height) {
		if (camera != null && previewHolder.getSurface() != null) {
			try {
				camera.setPreviewDisplay(previewHolder);
			} catch (Throwable t) {
				Log.e("PreviewDemo-surfaceCallback",
						"Exception in setPreviewDisplay()", t);
				Toast.makeText(Sender.this, t.getMessage(), Toast.LENGTH_LONG)
						.show();
			}

			if (!cameraConfigured) {
				Camera.Parameters parameters = camera.getParameters();
				Camera.Size size = getBestPreviewSize(width, height, parameters);

				if (size != null) {
					parameters.setPreviewSize(size.width, size.height);
					camera.setParameters(parameters);
					cameraConfigured = true;
				}
			}
		}
	}

	private void startPreview() {
		if (cameraConfigured && camera != null) {
			camera.startPreview();
			inPreview = true;
		}
	}

	SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				camera.setDisplayOrientation(90);
				camera.setPreviewDisplay(holder);
				camera.setPreviewCallback(new PreviewCallback() {
					public void onPreviewFrame(byte[] data, Camera camera) {
						try {
							Camera.Parameters parameters = camera
									.getParameters();
							Size size = parameters.getPreviewSize();
							YuvImage image = new YuvImage(data, parameters
									.getPreviewFormat(), size.width,
									size.height, null);
							Rect rect = new Rect(0, 0, size.width, size.height);
							ByteArrayOutputStream output_stream = new ByteArrayOutputStream();
							image.compressToJpeg(rect, 80, output_stream);
							byte[] byt = output_stream.toByteArray();
							new SendTask().execute(byt);
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				});
			} catch (IOException exception) {
				// camera.release();
				// camera = null;
			}
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			initPreview(width, height);
			startPreview();
		}

		public void surfaceDestroyed(SurfaceHolder holder) {

		}
	};

	public void baslat() {
		buffsize = AudioRecord.getMinBufferSize(44100,
				AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
		ar = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100,
				AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
				buffsize);

		ar.startRecording();

		isRecording = true;
		recordingThread = new Thread(new Runnable() {
			public void run() {
				SendAudio();
			}
		}, "AudioRecorder Thread");
		recordingThread.start();
	}

	public void durdur() {
		ar.stop();
		isRecording = false;
	}

	private void SendAudio() {
		short sData[] = new short[buffsize / 2];

		while (isRecording) {
			ar.read(sData, 0, buffsize / 2);
			byte bData[] = short2byte(sData);
			try {
				System.out.println("gonderiliyo");
				DatagramPacket hi = new DatagramPacket(bData, bData.length,
						group2, p2);
				socket2.send(hi);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private byte[] short2byte(short[] sData) {
		int shortArrsize = sData.length;
		byte[] bytes = new byte[shortArrsize * 2];
		for (int i = 0; i < shortArrsize; i++) {
			bytes[i * 2] = (byte) (sData[i] & 0x00FF);
			bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
			sData[i] = 0;
		}
		return bytes;

	}

	public void Sendmessage() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				Message();
				socket2.close();
			}
		});
		t.start();

	}

	public void Message() {
		try {
			String fin = "fin" + num;
			DatagramPacket hi = new DatagramPacket(fin.getBytes(),
					fin.getBytes().length, group2, p2);
			socket2.send(hi);
			socket2.send(hi);
			socket2.send(hi);
			Thread.sleep(100);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class SendTask extends AsyncTask<byte[], String, String> {

		@Override
		protected String doInBackground(byte[]... params) {

			try {
				byte[] si = params[0];
				DatagramPacket hi = new DatagramPacket(si, si.length, group, p1);
				socket.send(hi);

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

	}
}