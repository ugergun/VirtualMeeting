package com.example.phoenix;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class Profile extends Fragment {
	private String username, name, surname,job, image;
	private TextView vname, vsurname, vjob;
	private ImageView imageview;
	private String url;

	public Profile(String username, String name, String surname,
			String job, String image) {
		this.name = name;
		this.surname = surname;
		this.job = job;
		this.image = image;
		this.username = username;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.profile, container, false);
		vname = (TextView) view.findViewById(R.id.textView1);
		vsurname = (TextView) view.findViewById(R.id.textView2);
		vjob = (TextView) view.findViewById(R.id.textView4);
		imageview = (ImageView) view.findViewById(R.id.imageview);
		url = "http://192.168.1.55/media/uploads/" + username + "/" + image;
		System.out.println(url);
		new Image().execute();
		vname.setText("Ýsim          :     " + name);
		vsurname.setText("Soyisim   :     " + surname);
		vjob.setText("Meslek     :     " + job);
		
		return view;
	}

	public static Bitmap getBitmapFromURL(String src) {
		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public class Image extends AsyncTask<String, Bitmap, String> {

		@Override
		protected String doInBackground(String... params) {
			Bitmap bmp = getBitmapFromURL(url);
			publishProgress(bmp);
			return null;
		}

		@Override
		protected void onProgressUpdate(Bitmap... values) {
			super.onProgressUpdate(values);
			Bitmap bmp = values[0];
			imageview.setImageBitmap(bmp);
		}

	}

}
