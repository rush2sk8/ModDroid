package com.example.moddroid;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class LiveData extends Activity {

	private int address;

	private Modbus modbus;
	private String ip;
	private volatile boolean GO = true;
	private Thread dataThread;
	private GraphView graph;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_live_data);


		Bundle bundle = getIntent().getExtras();
		address = Integer.parseInt(((String) bundle.get("address")).trim());
		ip = bundle.getString("ip").trim();
		modbus = new Modbus(ip, Modbus.DEFAULT_PORT);

		graph = (GraphView)findViewById(R.id.graph);

		final LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();

		series.setOnDataPointTapListener(new OnDataPointTapListener() {

			@Override
			public void onTap(Series arg0, DataPointInterface arg1) {
				Toast.makeText(getApplicationContext(), "Value: "+arg1.getY(), Toast.LENGTH_SHORT).show();

			}
		});


		graph.addSeries(series);
		graph.getViewport().setYAxisBoundsManual(true);
		//graph.getViewport().setXAxisBoundsManual(true); //makes it all one part without removing data
		//graph.getViewport().setScrollable(true);
		dataThread = new Thread(new Runnable() {

			public void run() {	
				double time = 0;
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
									System.out.println(dp);
									graph.onDataChanged(true, false);
									graph.getViewport().setMinY(mn-.1);
									graph.getViewport().setMaxY(mx+.1);

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
		System.out.println(address);
	}

	public void onBackPressed() {
		//		Toast.makeText(getApplicationContext(), "Please Wait\nTerminating Thread", Toast.LENGTH_LONG).show();
		//		GO = false;
		//		finish();
		//		try {
		//			dataThread.join();
		//		} catch (InterruptedException e) {
		//			e.printStackTrace();
		//		}

	}

}
