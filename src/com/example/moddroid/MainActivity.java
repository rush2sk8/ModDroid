package com.example.moddroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {


	private Button settings;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		settings = (Button)findViewById(R.id.settingsButton);
		settings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			 startActivity(new Intent(getApplicationContext(),ModSettings.class));

			}
		});


	}


}
