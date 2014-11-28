package com.example.phoenix;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.phoenix.R.drawable;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LazyAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	public final static String NAME = "name";
	public final static String JOB = "job";
	public final static String PICTURE = "picture";
	public final static String NUMBER = "number";
	public ImageLoader imageLoader;

	public LazyAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.list_row, null);

		TextView title = (TextView) vi.findViewById(R.id.title); // title
		TextView artist = (TextView) vi.findViewById(R.id.artist); // artist
																	// name
		TextView duration = (TextView) vi.findViewById(R.id.duration); // duration
		ImageView thumb_image = (ImageView) vi.findViewById(R.id.list_image); // thumb
																				// image

		HashMap<String, String> song = new HashMap<String, String>();
		song = data.get(position);

		// Setting all values in listview
		title.setText(song.get(NAME));
		artist.setText(song.get(JOB));
		duration.setText(song.get(NUMBER));
		if (song.get(PICTURE) == null)
			thumb_image.setImageResource(R.drawable.grup);
		else
			imageLoader = new ImageLoader(song.get(PICTURE), thumb_image);
		return vi;
	}

}