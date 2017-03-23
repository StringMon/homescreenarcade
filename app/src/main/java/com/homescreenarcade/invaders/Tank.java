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

public class Tank extends GridObject {
	
	protected boolean mMoveable = true; 
	protected int mLevelWidth;
	int mFireCount = 0;
	Tank(boolean[][] b) {
		super(b);

	}
	Tank(boolean[][] b,int Xco, int Yco, int Width) {
		super(b,Xco,Yco);
		mLevelWidth=Width;
	}
	public void horizontalMove( boolean right , int Width){
		int move = 5;
		if(mMoveable){
			if(!right){
				move*=-1;
			}
			x+=move;

			if(x<0){
				x=0;
			}else if(x + ObjectManager.TANK_WIDTH > Width ){
				x= Width - ObjectManager.TANK_WIDTH;
			}
		}
		mMoveable=false;
	}
	public void allowNewMove(){
		mMoveable=true;
		if(mFireCount > 0){mFireCount--;}
	}
	public Shell update(int xtouch, int ytouch){

		if(xtouch < mLevelWidth/3){
			horizontalMove(false,mLevelWidth);
		} else if(xtouch > mLevelWidth*2/3){
			horizontalMove(true ,mLevelWidth);
		} else {
			if( mFireCount == 0 ){
				mFireCount += 10;
				return new Shell(x + ObjectManager.TANK_WIDTH/2,
						y - 1 , true);
			}
		}
		return null;
		
	}
	
	public Shell update(boolean fire, boolean move, boolean left){

		if(move){
			if(left){
				horizontalMove(false,mLevelWidth);
			} else {
				horizontalMove(true ,mLevelWidth);
			}
		}
		if(fire){
			if( mFireCount == 0 ){
				mFireCount += 10;
				return new Shell(x + ObjectManager.TANK_WIDTH/2,
						y - 1 , true);
			}
		}
		return null;

	}
}
