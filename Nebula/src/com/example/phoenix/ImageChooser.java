package com.example.phoenix;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageChooser extends Activity {

	private ImageView image;
	private Button uploadButton;
	private Bitmap bitmap;
	private Button saveButton;
	private String username,password,name,surname,job;
	private static final int PICK_IMAGE = 1;
	private String imagename="";
	private ProgressDialog pDialog;
	private static final String TAG_SUCCESS = "success";
	JSONParser jsonParser = new JSONParser();
	private static String url_create_product = "http://192.168.1.55/listenphp/createprofile.php";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imagechooser);
		Intent i = getIntent();
		username = i.getStringExtra("username");
	    password = i.getStringExtra("password");
		name = i.getStringExtra("name");
		surname = i.getStringExtra("surname");
		job = i.getStringExtra("job");
		image = (ImageView) findViewById(R.id.uploadImage);
		uploadButton = (Button) findViewById(R.id.uploadButton);
		saveButton = (Button) findViewById(R.id.saveButton);
		
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new ImageUploadTask().execute();
				new CreateNewProduct().execute();
 
			}
		});
		saveButton.setVisibility(View.INVISIBLE);
		uploadButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectImageFromGallery();
				
			}
		});
	}

	public void selectImageFromGallery() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"),
				PICK_IMAGE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == PICK_IMAGE && resultCode == RESULT_OK
				&& null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			int index = picturePath.lastIndexOf('/');
			imagename = picturePath.substring(index+1);
			String name = picturePath.substring(index);
			System.out.println(imagename+" "+ name);
			cursor.close();
			
			decodeFile(picturePath);
			saveButton.setVisibility(View.VISIBLE);
		}
	}

	public void decodeFile(String filePath) {
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, o);
		final int REQUIRED_SIZE = 1024;

		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 1;
		while (true) {
			if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
				break;
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		bitmap = BitmapFactory.decodeFile(filePath, o2);

		image.setImageBitmap(bitmap);
	}

	class ImageUploadTask extends AsyncTask<Void, Void, String> {
		private String webAddressToPost = "http://192.168.1.55/media/uploadimage.php";
		private ProgressDialog dialog = new ProgressDialog(ImageChooser.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Uploading...");
			dialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				HttpClient httpClient = new DefaultHttpClient();
				HttpContext localContext = new BasicHttpContext();
				HttpPost httpPost = new HttpPost(webAddressToPost);

				MultipartEntity entity = new MultipartEntity(
						HttpMultipartMode.BROWSER_COMPATIBLE);

				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				bitmap.compress(CompressFormat.JPEG, 100, bos);
				byte[] data = bos.toByteArray();
				String file = Base64.encodeBytes(data);
				entity.addPart("uploaded", new StringBody(file));
				entity.addPart("username", new StringBody(username));
				entity.addPart("path", new StringBody(imagename));
				httpPost.setEntity(entity);
				HttpResponse response = httpClient.execute(httpPost,
						localContext);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(
								response.getEntity().getContent(), "UTF-8"));

				String sResponse = reader.readLine();
				return sResponse;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			Toast.makeText(getApplicationContext(), "Kaydýnýz Oluþturuldu",
					Toast.LENGTH_LONG).show();
		}

	}
	class CreateNewProduct extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ImageChooser.this);
			pDialog.setMessage("Kaydiniz Olusturuluyor...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {

			
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("username", username));
			params.add(new BasicNameValuePair("password", password));
			params.add(new BasicNameValuePair("name", name));
			params.add(new BasicNameValuePair("surname", surname));
			params.add(new BasicNameValuePair("job", job));
			params.add(new BasicNameValuePair("image",imagename));
			JSONObject json = jsonParser.makeHttpRequest(url_create_product,
					"POST", params);
			Log.d("Create Response", json.toString());
			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					Intent i = new Intent(getApplicationContext(), Login.class);
					startActivity(i);
					finish();
				} else {
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
		}
	}

}