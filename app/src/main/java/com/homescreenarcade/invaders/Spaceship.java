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

public class Spaceship extends GridObject {

	public Spaceship(boolean[][] b) {
		super(b);
	}
	public Spaceship(boolean[][] b, int Xco, int Yco) {
		super(b, Xco, Yco);
	}
	public void advance(){
		x-=3;
	}
	public boolean isValid(){
		if(x + ObjectManager.SPACESHIP_WIDTH < 0){
			return false;
		}
		return true;
	}

    @Override
    public void registerHit() {
        ObjectManager.incrementScore(100);
    }
}
