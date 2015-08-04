package com.example.moddroid;

import java.util.Set;
import java.util.TreeSet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class ModSettings extends Activity {

	private SharedPreferences preferences; 
	private EditText ip;
	private EditText addresses;
	private Button save,delFace;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mod_settings);

		//get all the references to the data
		ip = (EditText)findViewById(R.id.ipTextField);
		addresses = (EditText)findViewById(R.id.addresses);
		save = (Button)findViewById(R.id.saveSettings);
		delFace = (Button)findViewById(R.id.deleteFace);

		//hide the keyboard
		View view = this.getCurrentFocus();
		if (view != null) {  
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}

		//get the preferences thing
		preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
		ip.setText(preferences.getString("ip", ""));
		addresses.setText(preferences.getStringSet("addresses", new TreeSet<String>()).toString());

		//del face
		if(preferences.getInt("FIRST_LAUNCH", -1)==-1) 
			delFace.setEnabled(false);

		//make a listener
		delFace.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				preferences.edit().putInt("FIRST_LAUNCH", CameraActivity.FIRST_LAUNCH).apply();;
				delFace.setEnabled(false);
			}
		});

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


}