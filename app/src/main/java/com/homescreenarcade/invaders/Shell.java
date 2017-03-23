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

class Shell {
    private int x, y, py;
	private boolean up;
	Shell(int X, int Y, boolean UP ){
		x=X; y=Y;py=Y; up = UP;
	}
	
	boolean progress(int levelHeight ){
		int move = 4;
		
		if( up ){
			move *= -1;
		}
		py = y;
		y += move;
		
		if(y > levelHeight || y< 0){
			return false;
		}
		return true;
	}
	Coordinate pos(){
		return new Coordinate(x,y);
	}
	boolean collisionDetect( GridObject obj ){
	
		Coordinate co;
		obj.reset();
		
		while((co=obj.next())!=null){
			if(co.x == x){
				if( y >= py ){
					if( co.y <= y && co.y >= py ){
                        obj.registerHit();
						return true;
					}
				} else {
					if( co.y <= py && co.y >= y ){
                        obj.registerHit();
						return true;
					}
					
				}
			} 
		}
		
		return false;
	}
	public boolean isUp(){
		return up;
	}
	
}
