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

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;

import com.homescreenarcade.ArcadeCommon;


public class ObjectManager {
	
    private static LocalBroadcastManager scoreMgr;
	
	// Default, Classic, Psychadelic, Redvolution
	private int mColourScheme = 0;
	
	private int[] ShellColour =  
		{0xFFEEEEEC,0xFF00FF00,0xFFFFFF00,0xFFFF0000};
	private int[] SpaceshipColour =    
		{0xFF75507B,0xFFFF0000,0xFFFF4400,0xFF770000};
	private int[] ShieldColour =       
		{0xFFEEEEEC,0xFF00FF00,0xFFFFFF66,0xFFFF3333};
	private int TankColour[] =         
		{0xFFBABDB6,0xFF00FF00,0xFFFFBB00,0xFF770000};
	private int redFlashTankColour[] = 
		{0xFFEF2929,0xFFFFFFFF,0xFF00BBFF,0xFFFF4444};
	private int[][] InvaderColour=
		{
		 {0xFFCC0000,0xFFFFFFFF,0xFF880088,0xFFFF0000},
		 {0xFF73D216,0xFFFFFFFF,0xFF770099,0xDDDD0000},
		 {0xFF3465A4,0xFFFFFFFF,0xFF6600AA,0xBBBB0000},
		 {0xFFEDD400,0xFFFFFFFF,0xFF5500BB,0x99990000},
		 {0xFFF57900,0xFFFFFFFF,0xFF4400CC,0x77770000}
		};
	
	public static int INVADER_WIDTH = 16;
	public static int INVADER_HEIGHT = 10;

    public void init(Context context) {
        scoreMgr = LocalBroadcastManager.getInstance(context.getApplicationContext());
    }
    
	public void setColourScheme(String scheme){
		if(scheme.contentEquals("Default")){
			mColourScheme = 0;
			return;
		}
		if(scheme.contentEquals("Classic")){
			mColourScheme = 1;
			return;
		}
		if(scheme.contentEquals("Psychadelic")){
			mColourScheme = 2;
			return;
		}
		if(scheme.contentEquals("Red-volution")){
			mColourScheme = 3;
			return;
		}
	}
	
	
	Bitmap Background = null;
	int CurrentBg = 0xFFFFFF;
	int CurrentBgWidth = 0xFFFFFF;
	
	
	
	public static int TANK_WIDTH = 16;
	public static int TANK_HEIGHT = 10;
	private char[][] Tank = 
	{
	    "       XX       ".toCharArray(),
		"      XXXX      ".toCharArray(),
		"      XXXX      ".toCharArray(),
		"      XXXX      ".toCharArray(),
		" XXXXXXXXXXXXXX ".toCharArray(),
		"XXXXXXXXXXXXXXXX".toCharArray(),
		"XXXXXXXXXXXXXXXX".toCharArray(),
		"XXXXXXXXXXXXXXXX".toCharArray(),
		"XXXXXXXXXXXXXXXX".toCharArray(),
		"XXXXXXXXXXXXXXXX".toCharArray()
	};

	private char[][][] Invaders = 
		{
			{	
				"     XX     ".toCharArray(),
				"    XXXX    ".toCharArray(),
				"   XXXXXX   ".toCharArray(),
				"  XX XX XX  ".toCharArray(),
				"  XXXXXXXX  ".toCharArray(),
				"    X  X    ".toCharArray(),
				"   X XX X   ".toCharArray(),
				"  X X  X X  ".toCharArray()
			},
			{	
				"   X    X   ".toCharArray(),
				"  XXXXXXXX  ".toCharArray(),
				" XX XXXX XX ".toCharArray(),
				"XXX XXXX XXX".toCharArray(),
				"X XXXXXXXX X".toCharArray(),
				"X XXXXXXXX X".toCharArray(),
				"  X      X  ".toCharArray(),
				"   XX  XX   ".toCharArray()
			},
			{	
				"    XXXX    ".toCharArray(),
				" XXXXXXXXXX ".toCharArray(),
				"XXX XXXX XXX".toCharArray(),
				"XXX  XX  XXX".toCharArray(),
				"XXXXXXXXXXXX".toCharArray(),
				"   XX  XX   ".toCharArray(),
				"  XX XX XX  ".toCharArray(),
				"XX        XX".toCharArray()
			},
			{	
				"   X    X   ".toCharArray(),
				"    X  X    ".toCharArray(),
				" X XXXXXX X ".toCharArray(),
				" X X XX X X ".toCharArray(),
				" XXXXXXXXXX ".toCharArray(),
				"  XXXXXXXX  ".toCharArray(),
				"   X XX X   ".toCharArray(),
				" XX      XX ".toCharArray()
			},			
			{	
				"   X    X   ".toCharArray(),
				"    X  X    ".toCharArray(),
				"   XXXXXX   ".toCharArray(),
				"   X XX X   ".toCharArray(),
				"  XX XX XX  ".toCharArray(),
				" XXXXXXXXXX ".toCharArray(),
				" X XXXXXX X   ".toCharArray(),
				"   X    X    ".toCharArray()
			}
		};
	

	public static int SPACESHIP_WIDTH = 16;
	public static int SPACESHIP_HEIGHT = 8;
	
	
	private char[][] Ship = 
	{
		"     XXXXXX     ".toCharArray(),
		"   XXXXXXXXXX   ".toCharArray(),
		"  XXXXXXXXXXXX  ".toCharArray(),
		" XX  XX  XX  XX ".toCharArray(),
		"XXX  XX  XX  XXX".toCharArray(),
		"XXXXXXXXXXXXXXXX".toCharArray(),
		"  XXX  XX  XXX  ".toCharArray(),
		"   X        X   ".toCharArray()
	};
	
	public static int SHIELD_WIDTH = 24;
	public static int SHIELD_HEIGHT = 8;
	
	private char[][] Shield = 
	{
		"   XXXXXXXXXXXXXXXXXX   ".toCharArray(),
		"  XXXXXXXXXXXXXXXXXXXX  ".toCharArray(),
		" XXXXXXXXXXXXXXXXXXXXXX ".toCharArray(),
		"XXXXXXXXXXXXXXXXXXXXXXXX".toCharArray(),
		"XXXXX              XXXXX".toCharArray(),
		"XXXX                XXXX".toCharArray(),
		"XXXX                XXXX".toCharArray(),
		"XXXX                XXXX".toCharArray()
	};



	private boolean[][] convert(char[][] c){
		
		boolean[][] b = new boolean[c.length][c[0].length];
		
		for(int i = 0; i< c.length; i++){
			
			for(int j = 0; j< c[0].length; j++){
				
				b[i][j] = (c[i][j]!=' ');
			}
		}
	return b;
	}
	
	public boolean[][] tank(){
		return convert(Tank);
	}
	public int tankColour(boolean redFlash){
		if(redFlash){
			return redFlashTankColour[mColourScheme];
		}
		return TankColour[mColourScheme];
	}
	public int shellColour(){
		return ShellColour[mColourScheme];
	}
	public boolean[][] invader(int i){
		return convert(Invaders[i]);
	}
	public int invaderColour(int i){
		return InvaderColour[i][mColourScheme];
	}
	public boolean[][] shield(){
		return convert(Shield);
	}
	public int shieldColour(){
		return ShieldColour[mColourScheme];
	}
	public boolean[][] ship(){
		return convert(Ship);
	}
	public int shipColour(){
		return SpaceshipColour[mColourScheme];
	}
	public int maxInvaders(){
		return Invaders.length;
	}
	
	public static int NUM_BACKGROUNDS = 11;
	public Bitmap background(int id, int width, Resources r ){
		if(id !=CurrentBg || width != CurrentBgWidth ){
			loadBackground(id, width, r);
		}
		return Background;
	}
	protected void loadBackground(int id, int width, Resources r){
/*
		try{
			Bitmap tmp;
			switch(id){
			case 0:
				tmp = BitmapFactory.decodeResource( r, R.drawable.background1);
				break;
			case 1:
				tmp = BitmapFactory.decodeResource( r, R.drawable.background2);
				break;
			case 2:
				tmp = BitmapFactory.decodeResource( r, R.drawable.background3);
				break;
			case 3:
				tmp = BitmapFactory.decodeResource( r, R.drawable.background4);
				break;
			case 4:
				tmp = BitmapFactory.decodeResource( r, R.drawable.background5);
				break;
			case 5:
				tmp = BitmapFactory.decodeResource( r, R.drawable.background6);
				break;
			case 6:
				tmp = BitmapFactory.decodeResource( r, R.drawable.background7);
				break;
			case 7:
				tmp = BitmapFactory.decodeResource( r, R.drawable.background8);
				break;
			case 8:
				tmp = BitmapFactory.decodeResource( r, R.drawable.background9);
				break;
			case 9:
				tmp = BitmapFactory.decodeResource( r, R.drawable.background10);
				break;
			case 10:
				tmp = BitmapFactory.decodeResource( r, R.drawable.background11);
				break;
			default:
				tmp = BitmapFactory.decodeResource( r, R.drawable.background1);
				break;

			}
			Background = Bitmap.createScaledBitmap(tmp, width, width, false);
		}catch(OutOfMemoryError e){
			//if out of memory, either reuse current bitmap
			//or make fake one if there is not a bitmap previously
			// do not set variables so we re-attempt to load the bitmap ASAP
			if(Background == null){
				Background = Bitmap.createBitmap( 1, 1, Bitmap.Config.ARGB_8888);
			}
			return;
		}
//*/
        CurrentBgWidth = width;
        CurrentBg = id;
	}

    public static void incrementScore(int i) {
        if (scoreMgr != null) {
            Intent scoreIntent = new Intent(ArcadeCommon.ACTION_STATUS)
                    .putExtra(ArcadeCommon.STATUS_INCREMENT_SCORE, i);
            scoreMgr.sendBroadcast(scoreIntent);
        }
    }
}
