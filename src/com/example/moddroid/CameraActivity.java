package com.example.moddroid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

public class CameraActivity extends Activity {

	public static final int FIRST_LAUNCH  = 1;
	private static final int CAPTURE_IMAGE_REQUEST_CODE = 0;
	private static final int AUTH_FACE = 2;
	private SharedPreferences.Editor editor;
	private SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);

		/*	if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
			Toast.makeText(getApplicationContext(), "You dont have a camera now exiting", Toast.LENGTH_LONG).show();
			System.exit(0);
		}
		readFirstTime();*/
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
		finish();
	}

	private void readFirstTime(){

		preferences = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
		editor = preferences.edit();

		int l = preferences.getInt("FIRST_LAUNCH", 0);

		//it is the first launch
		if(l != FIRST_LAUNCH) {

			//capture the face
			Toast.makeText(getApplicationContext(), "Take a Picture of your face for authentication", Toast.LENGTH_LONG).show();

			getFace(true);

		}else 
			getFace(false);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if(resultCode == RESULT_OK && requestCode == CAPTURE_IMAGE_REQUEST_CODE) {

			Bitmap bmp = (Bitmap)data.getExtras().get("data");
			ContextWrapper cw = new ContextWrapper(getApplicationContext());
			File dir = cw.getDir("imageDir", Context.MODE_PRIVATE);
			File path = new File(dir,"authPic.jpg");

			System.out.println(path.getAbsolutePath());

			//save path to picture
			editor.putString("picPath", path.getAbsolutePath());
			editor.apply();
			try {
				//save face
				FileOutputStream fos = new FileOutputStream(path);
				bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.close();	
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), "saving problem", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}

		}
		else if(resultCode == RESULT_OK && requestCode == AUTH_FACE) {

			try {

				Bitmap img1 = (Bitmap) data.getExtras().get("data");

				String imgPath = preferences.getString("picPath", "null");

				Bitmap img2 = BitmapFactory.decodeStream(new FileInputStream(new File(imgPath)));

				int width1 = img1.getWidth();
				int width2 = img2.getWidth();
				int height1 = img1.getHeight();
				int height2 = img2.getHeight();

				if ((width1 != width2) || (height1 != height2)) {
					Toast.makeText(getApplicationContext(), "not the same size", Toast.LENGTH_SHORT).show();				
					getFace(false);
				}

				//percent difference
				long diff = 0;
				for (int y = 0; y < img1.getHeight(); y++) {
					for (int x = 0; x < img1.getWidth(); x++) {
						int rgb1 = img1.getPixel(x, y);
						int rgb2 = img2.getPixel(x, y);
						int r1 = (rgb1 >> 16) & 0xff;
						int g1 = (rgb1 >>  8) & 0xff;
						int b1 = (rgb1      ) & 0xff;
						int r2 = (rgb2 >> 16) & 0xff;
						int g2 = (rgb2 >>  8) & 0xff;
						int b2 = (rgb2      ) & 0xff;
						diff += Math.abs(r1 - r2);
						diff += Math.abs(g1 - g2);
						diff += Math.abs(b1 - b2);
					}
				}
				double n = width1 * height1 * 3;
				double p = (diff / n / 255.0)*100;
				System.out.println("diff percent: " + (p * 100.0));
				Toast.makeText(getApplicationContext(), "Percent Diff: "+ p, Toast.LENGTH_SHORT).show();		

				if(p>20) {
					final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
					tg.startTone(ToneGenerator.TONE_PROP_BEEP);
					Toast.makeText(getApplicationContext(), "AUTHENTICATION FAILED TRY AGAIN",Toast.LENGTH_SHORT).show();
					getFace(false);
				}
				else {
					startActivity(new Intent(getApplicationContext(), MainActivity.class));
					finish();
				}


			} catch (FileNotFoundException e) {
				Toast.makeText(getApplicationContext(), "Pic Not found", Toast.LENGTH_SHORT).show();				
				e.printStackTrace();
			}

		}
		else if(resultCode == RESULT_CANCELED) {
			if (requestCode== CAPTURE_IMAGE_REQUEST_CODE) 
				getFace(true);
			else
				getFace(false);

		}

		editor.putInt("FIRST_LAUNCH", FIRST_LAUNCH);
		editor.apply();
	}

	private void getFace(boolean capture) {

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if(capture)
			startActivityForResult(intent, CAPTURE_IMAGE_REQUEST_CODE);
		else
			startActivityForResult(intent, AUTH_FACE);
	}

}
