import android.os.Debug;

import com.homescreenarcade.frogger.GameObject;

import java.awt.*;

import javax.swing.*;

import and.awt.Dimension;
import and.awt.Rectangle;

/**
 * @author liney
 *
 */
public class Frog extends GameObject {
	public final static int IS_DEAD = 0;
	public final static int ON_LOG = 1;
	public final static int HIT_BY_CAR = 2;
	public final static int IS_DROWNED = 3;
	public final static int IN_HOME = 4;
	public final static int IS_ALIVE = 5;
	public final static int UP = 5;
	public final static int DOWN = 2;
	public final static int LEFT = 1;
	public final static int RIGHT = 3;
	public final static int MOVEMENT = 37;
	private Rectangle roadBounds = new Rectangle(0, 0, FroggerClient.WINWIDTH,
			FroggerClient.WINHEIGHT-37);
	private boolean onBoard;

	
	private int status;
	private int lives = FroggerGui.FROGLIVES;
	public Frog(){
		myLabel = new JLabel();
		//myImage = new ImageIcon("./images/froggy32.png");
		resetFrog();
		
		
	}
	
	public void resetFrog (){
		
		onBoard = true;
		myImage = FroggerClient.myFrogImg;
		myLabel.setIcon(myImage);
		height = myLabel.getPreferredSize().height;
		width = myLabel.getPreferredSize().width;
		
		setPosition(new Dimension(400, (FroggerClient.WINHEIGHT-(2*MOVEMENT)+5) ) );
		status = IS_ALIVE;
		
	}
	/** move frog, and calculate the location **/
	public void move(int direction){
		Rectangle beforeMove = new Rectangle();
			
		
		if ((status == IS_ALIVE)){
		switch (direction){
		case	UP: {
			
			beforeMove = (Rectangle)this.clone();
			beforeMove.y -= MOVEMENT;
			if ((roadBounds.contains(beforeMove)))
			this.y -= MOVEMENT;
			break;
		}
			
		case	DOWN: {
			beforeMove = (Rectangle)this.clone();
			beforeMove.y += MOVEMENT;
			if ((roadBounds.contains(beforeMove)))
			this.y = this.y + MOVEMENT;
			break;
			}
		
		case	LEFT: {
			beforeMove = (Rectangle)this.clone();
			beforeMove.x -= MOVEMENT;
			if ((roadBounds.contains(beforeMove)))
			this.x = this.x - this.width;
			break;
		}
			
		case	RIGHT:{
			beforeMove = (Rectangle)this.clone();
			beforeMove.x += MOVEMENT;
			if ((roadBounds.contains(beforeMove)))
			this.x = this.x + this.width;
			break;
		}
		}
		}
	}
	/** set the status**/
	public void setStatus(int frogStatus){
		status= frogStatus;
	}
	/** return the status**/
	public int getStatus(){
		return status;
	}
	/**moves the frog on the log **/
	public void moveOnLog(WaterObj log){
		
		
		if ((status == IS_ALIVE)&&(roadBounds.contains(this))){
			//ToDO: change so that it goes with the log's speed
			
		if (log.getWaterDirection() == WaterObj.RIGHT){	
		this.x  = this.x + log.getWaterSpeed();
		}
		if (log.getWaterDirection() == WaterObj.LEFT){
			this.x = this.x - log.getWaterSpeed();
		}
		}
	}
	public void setPlayer(int player){
		
	}
	public int getPlayer(){
		return 0;
	}
	public JLabel killFrog(){
		myLabel.setIcon(FroggerClient.myDeadFrogImg);
		status = IS_DEAD;
		JLabel deadFrog = new JLabel(FroggerClient.myDeadFrogImg);
		Dimension size = deadFrog.getPreferredSize();
		deadFrog.setBounds(this.x, this.y, size.width, size.height);
		if (Debug.debugOn == true){
			System.out.println("deadFrog Label: " + deadFrog);
		}
		lives --;
		if (lives > 0)
		resetFrog();
		return deadFrog;
		
	}
	public JLabel homeFrog(){
		
		status = IN_HOME;
		
		
	
		resetFrog();
		return myLabel;
		
	}
	public void setLifes(int myLives){
		lives = myLives;
		status = IS_ALIVE;
	}
	public int getLifes(){
		return lives;
	}
}
