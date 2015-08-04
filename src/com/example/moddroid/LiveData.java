package com.example.moddroid;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ScaleDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class LiveData extends Activity {

	private int address;

	private Modbus modbus;
	private String ip;
	private volatile boolean GO = true;
	private Thread dataThread;
	private GraphView graph;
	private Boolean scaleable = true;
	private LineGraphSeries<DataPoint> series;
	private double time = 0;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_live_data);

		Bundle bundle = getIntent().getExtras();
		address = Integer.parseInt(((String) bundle.get("address")).trim());
		ip = bundle.getString("ip").trim();
		modbus = new Modbus(ip, Modbus.DEFAULT_PORT);

		graph = (GraphView)findViewById(R.id.graph);

		series = new LineGraphSeries<DataPoint>();
		
		series.setOnDataPointTapListener(new OnDataPointTapListener() {

			@Override
			public void onTap(Series arg0, DataPointInterface arg1) {
				Toast.makeText(getApplicationContext(), "Value: "+arg1.getY(), Toast.LENGTH_SHORT).show();

			}
		});

		graph.addSeries(series);
		graph.getViewport().setYAxisBoundsManual(true);

		dataThread = new Thread(new Runnable() {

			public void run() {	

				float min = Float.MAX_VALUE;
				float max = 1;

				while(GO) {
					try {

						Thread.sleep(5000);
						final float data = modbus.getDataFromInputRegister(address);
						final DataPoint dp = new DataPoint(time, data);

						if(data>max)
							max = data;
						if(data<min&&data!=-1)
							min = data;

						final float mn = min;
						final float mx = max;

						runOnUiThread(new Runnable() {

							@Override
							public void run() {

								if(data!=-1) {
									series.appendData(dp, true, 50);

									graph.onDataChanged(true, false);

									synchronized(scaleable) {

										if(scaleable) {
											graph.getViewport().setMinY(mn-.1);
											graph.getViewport().setMaxY(mx+.1);	

										}else {
											graph.getViewport().setMinY(4);
											graph.getViewport().setMaxY(20);	
										}
									}
								}
							}
						});

						time += 5;
					} catch (InterruptedException e) {

						e.printStackTrace();
					}
				}
			}
		});

		dataThread.start();
	}

	public void onBackPressed() {
		Toast.makeText(getApplicationContext(), "Please Wait\nTerminating Thread", Toast.LENGTH_LONG).show();
		GO = false;

		try {
			dataThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
		finish();

		super.onBackPressed();
	}
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		synchronized (scaleable) {
			if (item.getItemId() == R.id.fourTwenty) 
				scaleable = false;
			else if(item.getItemId() == R.id.scaled) 
				scaleable = true;
		}

		return true;
	}
}
