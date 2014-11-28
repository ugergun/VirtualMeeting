package com.example.phoenix;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;


public class ImageLoader {
	String urm;
	ImageView view;
	ProgressDialog pDialog;
	public ImageLoader(String urm,ImageView view){
		this.urm=urm;
		this.view=view;
		new Image().execute();
	}
	public static Bitmap getBitmapFromURL(String src) {
	    try {
	    	
	        URL url = new URL(src);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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
	public class Image extends
	AsyncTask<String, String, String> {
		Bitmap bmp;
		@Override
		protected String doInBackground(String... params) {
			bmp=getBitmapFromURL(urm);
			return null;
		}

		protected void onPostExecute(String file_url) {
			view.setImageBitmap(bmp);
		}
	
	}
	
}
