package com.example.moddroid;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class LiveData extends Activity {

	private int address;
	private TextView label;
	private Modbus modbus;
	private String ip;
 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_live_data);

		
		
		Bundle bundle = getIntent().getExtras();
		address = Integer.parseInt((String) bundle.get("address"));
		ip = bundle.getString("ip");
		modbus = new Modbus(ip, Modbus.DEFAULT_PORT);
		label = (TextView)findViewById(R.id.label);

		
		
		System.out.println(address);
	}


}
