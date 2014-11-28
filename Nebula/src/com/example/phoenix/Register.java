package com.example.phoenix;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

public class Register extends Activity {
	private Button select, save;
	private EditText edit1, edit2, edit3, edit4, edit6;
	private ScrollView view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		edit1 = (EditText) findViewById(R.id.editText1);
		edit2 = (EditText) findViewById(R.id.editText2);
		edit3 = (EditText) findViewById(R.id.editText3);
		edit4 = (EditText) findViewById(R.id.editText4);
		edit6 = (EditText) findViewById(R.id.editText6);
		view = (ScrollView) findViewById(R.id.svScroll);
		view.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
						0);

				return true;
			}
		});
		save = (Button) findViewById(R.id.button2);
		save.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				String username = edit1.getText().toString();
				String password = edit2.getText().toString();
				String name = edit3.getText().toString();
				String surname = edit4.getText().toString();
				String job = edit6.getText().toString();
				if (null == username || username.length() == 0) {
					Toast.makeText(getApplicationContext(),
							"Kullanýcý adý giriniz", Toast.LENGTH_LONG).show();
					edit1.requestFocus();
				}
				else if (null == password || password.length() == 0) {
					Toast.makeText(getApplicationContext(),
							"Þifre Giriniz", Toast.LENGTH_LONG).show();
					edit2.requestFocus();
				}
				else if (null == name || name.length() == 0) {
					Toast.makeText(getApplicationContext(),
							"Ýsminizi Giriniz", Toast.LENGTH_LONG).show();
					edit3.requestFocus();
				}
				else if (null == surname || surname.length() == 0) {
					Toast.makeText(getApplicationContext(),
							"Soyisminizi Giriniz", Toast.LENGTH_LONG).show();
					edit4.requestFocus();
				}
				else if (null == job || job.length() == 0) {
					Toast.makeText(getApplicationContext(),
							"Mesleðinizi Giriniz", Toast.LENGTH_LONG).show();
					edit6.requestFocus();
				}
				else{
				Intent in = new Intent(getApplicationContext(),
						ImageChooser.class);
				in.putExtra("username", username);
				in.putExtra("password", password);
				in.putExtra("name", name);
				in.putExtra("surname", surname);
				in.putExtra("job", job);
				startActivityForResult(in, 100);
				finish();
				}

			}
		});
	}
}
