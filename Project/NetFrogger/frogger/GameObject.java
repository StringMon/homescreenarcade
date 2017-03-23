package com.homescreenarcade.frogger;

import android.graphics.Bitmap;

import java.awt.*;
import javax.swing.*;

import and.awt.Dimension;
import and.awt.Rectangle;
import and.awt.geom.Rectangle2D;


public class GameObject extends Rectangle {

	// The label that will display the object
	protected String myLabel;
	protected Bitmap myImage;
	protected int rowNum;
	
	/** Returns a String, for display on the screen**/
	public GameObject(){
		
	}
	public String getObjLabel (){
			
		 return this.myLabel;	
	}
	public void draw(Container myPanel){
		myPanel.add(myLabel);
	}
   /** Sets the position and size of the object **/
    public void setBounds(Rectangle boundRect){
		
    }
	/** Sets just the position of the object **/
    public void setPosition(Dimension myPosition){
		x = myPosition.width;
		y = myPosition.height;
		resetLabel();
    }	
	  public void setX(int myX){
			x = myX;
			
			resetLabel();
	    }
	  public void setY(int myY){
			y = myY;
			
			resetLabel();
	    }
	/** Returns the position of the object **/
    public Dimension getPosition(){
		Dimension tmpDim = new Dimension();
		tmpDim.height = this.y;
		tmpDim.width = this.x;
		
		return tmpDim;
		
    }
	/** Returns the bounds of the object **/
    public Rectangle getBounds(){
		return this;
		
    }
	
	public void setImage(String imgLocation){
		
		myImage = new Bitmap(imgLocation);
		myLabel.setIcon(myImage);
		
		
	}
	
	public void resetLabel(){
		myLabel.setBounds(this);
	}
	
	//Sets the center of the Rectangle
	
	public void setCenterX(double cX){
		this.setX((int)(cX - ((this.width)/2)));
	}
	
	public void setCenterY(double cY){
		this.setY((int)(cY - ((this.height)/2)));
	}
    
}
