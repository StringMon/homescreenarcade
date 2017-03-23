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

public class Shield extends GridObject {

	Shield(boolean[][] b) {
		super(b);
	}	
	Shield(boolean[][] b, int Xco, int Yco) {
		super(b, Xco, Yco);
	}
	public void collision(Shell s){
		int shellX = s.pos().x;
		int index = shellX - x;
		if(index < 0 || index >= grid[0].length ){
			return;
		}
		if(s.isUp()){
			for(int i=grid.length-1;i>=0;i--){
				if(grid[i][index]){
					explode(i, index);
					return;
				}
			}
		} else {
			for(int i = 0;i< grid.length;i++){
				if(grid[i][index]){
					explode(i, index);
					return;
				}
			}
		}
	}

	private void explode(int i, int j){
		if(i>grid.length-1 ||
		   i < 0 ||
		   j > grid[i].length-1 ||
		   j< 0){
			return;
		}
		grid[i][j]=false;
		if(i != 0){
			grid[i-1][j]=false;
			if(j !=0){
				grid[i-1][j-1]=false;
			}
			if(j != grid[i].length-1){
				grid[i-1][j+1]=false;
			}
		}
		if(i != grid.length-1){
			grid[i+1][j]=false;
			if(j !=0){
				grid[i+1][j-1]=false;
			}
			if(j != grid[i].length-1){
				grid[i+1][j+1]=false;
			}
		}
		if(j !=0){
			grid[i][j-1]=false;
		}
		if(j != grid[i].length-1){
			grid[i][j+1]=false;
		}
	}

}
