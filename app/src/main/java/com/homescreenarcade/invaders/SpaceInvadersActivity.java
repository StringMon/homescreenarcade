/*
 *  Space Invaders
 *
 *  Copyright (C) 2012 Glow Worm Applications
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Street #330, Boston, MA 02111-1307, USA.
 */

package com.homescreenarcade.invaders;

import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;

public class SpaceInvadersActivity extends Activity implements SensorEventListener {
    /** Called when the activity is first created. */
    
    private InvaderView mView = null;
    
    private PowerManager mPowerManager;
    private SensorManager mSensorManager;
//    private WakeLock mWakeLock;
    private Sensor mAccelerometer;
    
    private Timer mUpdate;
    private InvaderTimer mEvent;
    private boolean mUseAccelerometer = false;
    private int mAccelerometerThreshold = 20;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Get an instance of the SensorManager
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        
        // Get an instance of the PowerManager
        mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
       

        // Create a bright wake lock
//        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
//        							getClass().getName());

        
        

        mView = new InvaderView(getApplicationContext());
        setContentView(mView);
        
        mUpdate = new Timer();
        mEvent = new InvaderTimer();
        mUpdate.schedule(mEvent, 0 ,80);
        
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mWakeLock.acquire();
        if(mView!=null){
        	mView.updatePrefs();
        }
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
    	super.onPause();
//    	mWakeLock.release();
        mSensorManager.unregisterListener(this);
    }
    
    
    class InvaderTimer extends TimerTask {
        @Override
        public void run() {
         runOnUiThread(new Runnable() {
          public void run() {
           mView.postInvalidate();
          }
         });
        }
       }

    @Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		//Do Nothing
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
  		if(mUseAccelerometer){
  			accelerometerControl(event);
  			return;
  		} else {
  			return;
  		}
	}

	private void accelerometerControl(SensorEvent event) {
		float xSens;
		
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
            return;

        xSens = event.values[1];
//        if(xSens > mAccelerometerThreshold){
//        	mMove = true;
//        	mMoveLeft = false;
//        	mTouch = true;
//        	return;
//        }
//        if(xSens < -mAccelerometerThreshold){
//        	mMove = true;
//        	mTouch = true;
//        	mMoveLeft = true;
//        	return;
//        }
//        mMove = false;
//        if(mFire == false){
//        	mTouch = false;
//        }
	}
}
