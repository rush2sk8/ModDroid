package com.example.moddroid;

import java.util.Set;
import java.util.TreeSet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class ModSettings extends Activity {

	private SharedPreferences preferences; 
	private EditText ip;
	private EditText addresses;
	private Button save;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mod_settings);

		//get all the references to the data
		ip = (EditText)findViewById(R.id.ipTextField);
		addresses = (EditText)findViewById(R.id.addresses);
		save = (Button)findViewById(R.id.saveSettings);

		//hide the keyboard
		hideKeyboard(getCurrentFocus());
		
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);
		layout.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
			
				hideKeyboard(v);
				return false;
			}
		});
		
		//get the preferences thing
		preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
		ip.setText(preferences.getString("ip", ""));
		addresses.setText(preferences.getStringSet("addresses", new TreeSet<String>()).toString());


		final SharedPreferences.Editor editor = preferences.edit();

		save.setOnClickListener(new OnClickListener() {


			public void onClick(View v) {
				editor.putString("ip", ip.getText().toString());

				Set<String> ads = new TreeSet<String>();

				//put the addresses
				for(String address: addresses.getText().toString().split(",")) {
					if(address.contains("["))
						ads.add(address.trim().replace("[", ""));
					else if(address.contains("]"))
						ads.add(address.trim().replace("]", ""));
					else 
						ads.add(address);
				}

				//puts the data in the preferences set
				editor.putStringSet("addresses", ads);
				editor.apply();

				//start the activity
				startActivity(new Intent(getApplicationContext(),MainActivity.class));
				finish();
			}
		});

	}
	private void hideKeyboard(View v) {

		InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		in.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

}