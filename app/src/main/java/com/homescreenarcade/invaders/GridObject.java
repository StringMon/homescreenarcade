/*
 *  Space Invaders
 *
 *  Copyright (C) 2012 Glow Worm Applications
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
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

public class GridObject {
	protected boolean grid[][];
	int x = 0;
	int y = 0;
	int i = 0, j = 0; 
	GridObject(boolean[][]b){
		this(b,0,0);
	}
	GridObject(boolean[][] b, int Xco, int Yco){
		x=Xco; y=Yco; grid=b;
	}
	public void reset(){
		i=0; j=0;
	}
	public int height(){
		return grid.length;
	}
	public int width(){
		return grid[0].length;
	}
	public Coordinate next(){
		
		for(;i<grid.length;i++){
			
			for(;j<grid[i].length;j++){
				
				if(grid[i][j]){
					int rX = j+x;
					int rY = i+y;
					if(j<grid[i].length){
						j++;
					} else {
						i++;
					}
					return new Coordinate(rX,rY);
				}
			}
			j=0;
		}
		return null;
	}
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}

    public void registerHit() {}
}
