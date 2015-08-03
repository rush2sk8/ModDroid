package com.example.moddroid;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class LiveData extends Activity {

	private int address;
	private TextView label;
	private Modbus modbus;
	private String ip;
	private volatile boolean GO = true;
	private Thread dataThread;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_live_data);


		Bundle bundle = getIntent().getExtras();
		address = Integer.parseInt(((String) bundle.get("address")).trim());
		ip = bundle.getString("ip").trim();
		modbus = new Modbus(ip, Modbus.DEFAULT_PORT);
		label = (TextView)findViewById(R.id.label);
		label.setMovementMethod(new ScrollingMovementMethod());


		dataThread = new Thread(new Runnable() {

			public void run() {
				while(GO) {
					try {
						Thread.sleep(5000);
						final float data = modbus.getDataFromInputRegister(address);
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								if(data!=-1)
									label.setText(label.getText().toString().concat("\n").concat(data+""));
							}
						});

					} catch (InterruptedException e) {

						e.printStackTrace();
					}
				}
			}
		});
		dataThread.start();
		System.out.println(address);
	}
	@Override
	public void onBackPressed() {

		GO = false;
		try {
			dataThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
