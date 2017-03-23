package com.homescreenarcade.mazeman;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Display;

public class GameActivity extends Activity implements SensorEventListener{
	final int  RIGHT = 1, LEFT = 2, UP = 4, DOWN = 8;
	
	private com.homescreenarcade.mazeman.GameSurfaceView gameView;
	private SensorManager mySensorManager;
	private Sensor myAccelerometer;
	
	//change in x and y of pac-mon
	private float xAccel;
	private float yAccel;
	private com.homescreenarcade.mazeman.GameEngine gameEngine;
	private com.homescreenarcade.mazeman.SoundEngine soundEngine;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        myAccelerometer = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mySensorManager.registerListener(this, myAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    
        int level = getIntent().getIntExtra("level", 1);

        soundEngine = new com.homescreenarcade.mazeman.SoundEngine(this);
        gameEngine = new com.homescreenarcade.mazeman.GameEngine(this, soundEngine, level);
        
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        gameView = new com.homescreenarcade.mazeman.GameSurfaceView(this, gameEngine, width, height);

        setContentView(gameView);
       
        
    }

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		gameEngine.pause();
		gameView.pause();
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		gameEngine.resume();
		gameView.resume();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mySensorManager.unregisterListener(this);
		super.onDestroy();
		//gameView.pause();
	}
	

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		
		 Intent intent = new Intent();

			 intent.putExtra("level", gameEngine.level);
		     intent.putExtra("status", gameEngine.status);

		        setResult(RESULT_OK, intent);
		  
		soundEngine.endMusic();
		super.finish();
	}


	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Do you want to quit?").setCancelable(false)
				.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						GameActivity.this.finish();
					}
				})
				.setNegativeButton("Resume", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	//get values of accelerometer
	public void onSensorChanged(SensorEvent event) {
		
		try {
			Thread.sleep(16);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		xAccel = event.values[0];
		yAccel = event.values[1];
		//float z = event.values[2];
		
		if(yAccel < 2.8F ){ // tilt up
			gameEngine.setInputDir(UP);
			//gameView.setDir(1);
		}
		if(yAccel > 7.5F ){ // tilt down
			gameEngine.setInputDir(DOWN);
			//gameView.setDir(2);
		}
		if (xAccel < -1.8F ) { // tilt to
																	// right
			gameEngine.setInputDir(RIGHT);
			//gameView.setDir(3);
		}
		if (xAccel > 1.8F ) { // tilt to
																	// left
			gameEngine.setInputDir(LEFT);
			//gameView.setDir(4);
		}
	
	}
    
}
