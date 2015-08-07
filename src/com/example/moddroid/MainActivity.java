package com.example.moddroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {


	private Button settings, viewLive;
	private TextView tv;
	private ImageView iv;

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

		//fade tudor
		iv = (ImageView)findViewById(R.id.tudorFace);
		tv = (TextView)findViewById(R.id.nameLabel);
		tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final Animation fade = new AlphaAnimation(1, 0);
				fade.setInterpolator(new AccelerateInterpolator());
				fade.setDuration(1000);
				fade.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
 

					}

					@Override
					public void onAnimationRepeat(Animation animation) {
 

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						iv.setVisibility(View.INVISIBLE);

					}
				});
			
				Animation fade1 = new AlphaAnimation(0,1);
				fade1.setInterpolator(new AccelerateInterpolator());
				fade1.setDuration(1000);

				fade1.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
				 

					}

					@Override
					public void onAnimationRepeat(Animation animation) {
 

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						iv.startAnimation(fade);

					}
				});


				iv.startAnimation(fade1);
			}
		});

		viewLive = (Button)findViewById(R.id.viewButton);
		viewLive.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),ViewGrid.class));
		 
			}
		});
	}


}
