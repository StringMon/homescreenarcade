import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class FroggerGui extends Thread implements KeyListener, MouseListener {

	public final static int NUMCARS = 25;
	public final static int VEHSPACE = 800;
	public final static int NUMWATER = 16;
	public final static int NUMWATERROWS = 4;
	public final static int FROGLIVES = 5;
	public final static int NUMHOMES = 5;
	public final static boolean NODEATH = false;
	public final static boolean NOTIME = false;
	public final static int INITTIME = 1000;

	public final static int NUMVEHROWS = 5;
	public final static int WATERSPACE = 256;
	private Container myPanel;
	
	private int alligatorCount = 0;
	private int homeCount = 0;
	private int componentNum;

	private long sleepy;

	private JLabel froglabel;

	private JLabel tfrogLab;
	
	private JLabel myLives;
	
	private JLabel timerLabel;
	private JPanel timerBar;
	private JLabel timeScore;

	private Frog myFrog;
	private JLabel gameOverLab;
	private testFrog mytFrog;
	private int totalTime =0;
	
	private int theTime = INITTIME;
	private boolean gameOver = true;
	
	private boolean isSafe;

	private Rectangle waterBounds = new Rectangle(0, 10, 800, (6 * 37));

	private Rectangle roadBounds = new Rectangle(0, 0, FroggerClient.WINWIDTH,
			FroggerClient.WINHEIGHT);

	private Vehicle[] vehArray;
	private WaterObj[] waterArray;
	private GatorHead[] headArray;
	private HomeObject[] homeArray;

	/** Contructior* */
	public FroggerGui(Container myPanel, int num, long sleepy) {

		if (Debug.debugOn == true) {
			System.out.println("<<< In FroggerGui's constructor  >>>");

		}
		this.myPanel = myPanel;
		this.componentNum = num;
		this.sleepy = sleepy;

		
		initVehicles();
	
		

		// make the frog;
		myFrog = new Frog();
		// myFrog.setPosition(new Dimension(400,532));
		froglabel = myFrog.getObjLabel();

		myPanel.add(froglabel);
		
		initHomes();
		initWaterObjs();
		timeInit();

		if (Debug.debugOn == true) {
			Component test = myPanel.getComponent(0);
			Component test2 = myFrog.getObjLabel();
			System.out.println("<<Test1 Label data:" + test.getBounds());
			System.out.println("<<Test1 Label image:" + test.toString());

		}
	}

	/**
	 * @param args
	 */

	public void run() {

		if (Debug.debugOn == true) {
			System.out.println("<<In the FroggerGui thread>>");
			// System.out.println("compCount ="+compCount);
			System.out.println(myPanel);
		}
		int gatorTime = 0;
		int i = 0;
		while (true) {

			try {
				sleep(sleepy);
				totalTime++;
				timeScore.setText(""+totalTime);
				moveVehicles();
				moveWaters();
				moveFrog();	
				moveGators();
				if (gatorTime == GatorHead.GATORTIME){
					gatorOpen();
					gatorTime =0;
				}
				gatorTime ++;
				timeCount();
				
				if ((myFrog.getLifes())<= 0){
					gameOver();
				}
				
			
				if (Debug.debugOn == true) {
					// System.out.println(froglabel);
				}
				// myPanel.repaint();

			} catch (InterruptedException e) {
				System.out.println("<<FroggerGui Thread interupted>>");
			}

		}

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

	/** Handle key events * */
	public void keyPressed(KeyEvent e) {
		int keyChr = e.getKeyCode();
		if (Debug.debugOn == true) {
			System.out.println("***KeyPressed: " + keyChr + " ****");
		}

		if (keyChr == 38) {
			myFrog.move(Frog.UP);
		}
		if (keyChr == 39) {
			myFrog.move(Frog.RIGHT);
		}
		if (keyChr == 37) {
			myFrog.move(Frog.LEFT);
		}
		if (keyChr == 40) {
			myFrog.move(Frog.DOWN);
		}
		//moveFrog();
	}

	public void keyReleased(KeyEvent e) {
		int keyChr = e.getKeyCode();
		if (Debug.debugOn == true) {
			System.out.println("***KeyReleased: " + keyChr + " ****");
		}
	}

	public void keyTyped(KeyEvent e) {
		char keyChr = e.getKeyChar();
		if (Debug.debugOn == true) {
			System.out.println("***KeyTyped: " + keyChr + " ****");
		}
	}

	/** intialize the vehicles * */
	public void initVehicles() {

		if (Debug.debugOn == true) {
			System.out.println("<<<<In initVehicles >>>>>");
			;

		}
		int i;
		int j;

		int vehRow = 0;
		int vehType;
		int initialX;
		vehArray = new Vehicle[NUMCARS];
		for (i = 0; i < NUMCARS; i++) {
			if (Debug.debugOn == true) {
				System.out.println("@@@ in outer loop in initVehicles @@@");

			}
//			Increment verRox
			if ((i % NUMVEHROWS) == 0) {
				vehRow++;
			}
			//Choose which vehicle to display
			vehType = MyRand.choose(0, 3);
			//Even numbered rows get even cars
			if((vehRow % 2)==0){
				if (!((vehType % 2) == 0)){
					vehType -= 1;
				}
			}
			//Odd numbered rows get odd cars
			if(!((vehRow % 2)==0)){
				if ((vehType %2 )==0){
					vehType += 1;
				}
			}
			
			if (Debug.debugOn == true) {
				System.out.println("vehType :" + vehType);
				System.out.println(" *******//vehRow : " + vehRow);
				System.out.println("******// i: " + i);

			}
			vehArray[i] = new Vehicle(vehType, vehRow);
			vehiclePlace(i);
			
			//Vehicle Label stuff
			vehArray[i].resetLabel();
			JLabel jlHolder = new JLabel();
			jlHolder = vehArray[i].getObjLabel();
			myPanel.add(jlHolder);

			if (Debug.debugOn == true) {
				System.out.println("!!!Vehicle added : " + vehArray[i]);
				System.out.println("!!!Vehicle's label : " + jlHolder);
			}
			

		}
		if (Debug.debugOn == true) {
			System.out.println("!!!vehicle array length : " + vehArray.length);
			
		}
		
	
	}
	
	private void vehiclePlace(int i){
		int initialX = MyRand.choose(30, 700);
		vehArray[i].x = initialX;
		
		//Make sure vehicles don't overlap
		if (i > 0){
			for (int j = 0; j < i; j++){
							
				if(vehArray[i].intersects(vehArray[j])){
					vehiclePlace(i);
					if (Debug.debugOn){
						System.out.println("Vehicle placed");
					}
					
			
				}
				else{
					if(Debug.debugOn){
						System.out.println("Vehicle overlaps");
						System.out.println("vehArray[i]:"+vehArray[i]);
						System.out.println("vehArray[j]:"+vehArray[j]);
					}
				}
			}
		}
	}
	
	
	private void initWaterObjs(){
		waterArray = new WaterObj[NUMWATER];
		headArray = new GatorHead[(NUMWATER / NUMWATERROWS)];
		int i;
		int j;
		int initialX = 0;
		int gatorCount = 0;
		int waterType;
		int waterRow = 0;
		JLabel jlHolder = new JLabel();
		
		for (i = 0; i < NUMWATER; i++){
			
			if ((i % NUMWATERROWS)== 0){
				waterRow++;
				initialX = 0;
			}
			
			//create the water object
			
			waterArray[i] = new WaterObj(waterRow-1, waterRow);
			
			//create a gator head
			
			
			
			//water label stuff
			
			//initialX = MyRand.choose(0, 800);
			waterArray[i].x = initialX;
			initialX += WATERSPACE;
			
			if (Debug.debugOn == true){
				System.out.println ("<<<!!!Water InitialX:"+initialX);
			}
			waterArray[i].resetLabel();
			
			if ((waterRow-1) == WaterObj.ALLIGATOR_BACK ){
				if (Debug.debugOn){
					System.out.println("$%$%$%$%$% Creating a gator head #"+ gatorCount);
					System.out.println("@#@#@#@#@ waterArray in GatorHead passed constructor:" + waterArray[i]);
				
				}
				headArray[gatorCount] = new GatorHead(waterArray[i]);
				headArray[gatorCount].resetLabel();
			
				jlHolder = headArray[gatorCount].getObjLabel();
				if ((Debug.debugOn == true)){
					System.out.println("Head Label info"+jlHolder);
					System.out.println("WaterRow :"+ waterRow);
				}
				gatorCount++;
				myPanel.add(jlHolder);
			}
			
			jlHolder = waterArray[i].getObjLabel();
			if ((Debug.debugOn == true)){
				System.out.println("Water Label info"+jlHolder);
				System.out.println("WaterRow :"+ waterRow);
			}
			myPanel.add(jlHolder);
			
		}
		
	}

	private void moveWaters (){
		int i; 
		//Move all of the water objects
		for (i = 0; i < NUMWATER; i++) {
			waterArray[i].move();
			if (Debug.debugOn == true) {
				//System.out.println("!!!i :" + i);
			}
			// Check to see if the water objects hit the end and then wrap them around
			
			
			if (!(waterArray[i].intersects(roadBounds))) {
				if ((waterArray[i].getWaterDirection() == WaterObj.LEFT)) {
					waterArray[i].setX((FroggerClient.WINWIDTH));
				}
				if ((waterArray[i].getWaterDirection() == WaterObj.RIGHT)) {
					waterArray[i].setX((0 - waterArray[i].width));
				}

			}
			
			
		}
	}
	
	private void moveGators(){
		int i; 
		//Move all of the gatorHeads
		for (i = 0; i < (NUMWATER/NUMWATERROWS); i++) {
			headArray[i].move();
			if (Debug.debugOn == true) {
				//System.out.println("!!!i :" + i);
			}
			// Check to see if the water objects hit the end and then wrap them around
			
			
			if (!(headArray[i].intersects(roadBounds))) {
				
					headArray[i].resetHead();
				}

			
		}
			
		
	}
	
	private void gatorOpen (){
		int i;
		for (i = 0; i <(NUMWATER/NUMWATERROWS);i++ ){
			headArray[i].switchOpen();
		}
	}
	private void moveVehicles (){
		int i; 
		//Move all of the vehicles
		for (i = 0; i < NUMCARS; i++) {
			vehArray[i].move();
			if (Debug.debugOn == true) {
				//System.out.println("!!!i :" + i);
			}
			// Check to see if the cars went off of the screen and wrap them around
			
			if (!(vehArray[i].intersects(roadBounds))) {
				if ((vehArray[i].getDirection() == Vehicle.LEFT)) {
					vehArray[i].setX((FroggerClient.WINWIDTH));
				}
				if ((vehArray[i].getDirection() == Vehicle.RIGHT)) {
					vehArray[i].setX((0 - vehArray[i].width));
				}

			}
			
			//Check to see if they hit a frog
			if (vehArray[i].intersects(myFrog)){
				
				deathToFrog();
			}
		}
	}
	
	private void moveFrog(){
		boolean isOnSafeObj = false;
		boolean isOnSafeHead = false;
		boolean inthehome = false;
		int i;
		int waterOn = 0;
		int headOn = 0;
		for (i = 0; i < NUMWATER; i++){
			if (waterArray[i].intersects(myFrog)){
			isOnSafeObj = true;
			waterOn = i;
			//myFrog.setCenterX(waterArray[waterOn].getCenterX());
			myFrog.setCenterY(waterArray[waterOn].getCenterY());
			}
			
		}
		for (i = 0; i < (NUMWATER/NUMWATERROWS); i++){
			if ((headArray[i].intersects(myFrog)) && !(headArray[i].headOpen())){
			isOnSafeHead = true;
			headOn = i;
			//myFrog.setCenterX(waterArray[waterOn].getCenterX());
			myFrog.setCenterY(headArray[headOn].getCenterY());
			}
		}
		
		if (Debug.debugOn == true){
			System.out.println ("<<<isOnSafeObj : " + isOnSafeObj);
			
		}
		inthehome = checkHomes();
			
		if ((myFrog.intersects(waterBounds))
				& !(isOnSafeObj) & !(isOnSafeHead) & !(inthehome)) {
			deathToFrog();
		}

		if (isOnSafeHead) {
			myFrog.moveOnLog(headArray[headOn].getGator());

		}
		
		if (isOnSafeObj && !isOnSafeHead) {
			myFrog.moveOnLog(waterArray[waterOn]);

		}
		
		

		froglabel.setBounds(myFrog);
	}
	private void initHomes (){
		homeArray = new HomeObject[NUMHOMES];
		int startX = 75;
		int homeY =30;
		int homeSpacing = 150;
		for (int i = 0; i < NUMHOMES;  i++){
			homeArray[i] = new HomeObject();
			homeArray[i].myLabel = new JLabel(FroggerClient.homeImg);
			homeArray[i].setSize(homeArray[i].myLabel.getPreferredSize());
			homeArray[i].setPosition(new Dimension(startX, homeY));
			homeArray[i].resetLabel();
			myPanel.add(homeArray[i].getObjLabel());
			startX +=homeSpacing;
			if(Debug.debugOn){
				System.out.println("homeArray "+i+":"+homeArray[i]);
				System.out.println("homeArray label"+i+":"+homeArray[i].myLabel);
			}
		}
	
		
	}
	private boolean checkHomes(){
		boolean isinhome = false;
		for (int i = 0; i < NUMHOMES; i++){
			if (myFrog.intersects(homeArray[i])){
				isinhome = true;
				if (homeArray[i].isTaken()){
					myFrog.move(Frog.DOWN);
				}
				else if(!homeArray[i].isTaken()){
					JLabel homey = new JLabel(FroggerClient.myFrogImg);
					homey.setBounds(homeArray[i]);
					
					myPanel.add(homey);
					int compCount = myPanel.getComponentCount();
					myPanel.setComponentZOrder(homey,compCount - compCount);
					
					if(Debug.debugOn){
						System.out.println("homey :"+homey);
						
						
					}
					homeArray[i].nowTaken(true);
					homeCount++;
					myFrog.resetFrog();
					myFrog.resetLabel();
					myPanel.repaint();
					checkWin();
					resetTime();
				}
				
			}
			
			
		}
		return isinhome;
	}
	
	private void checkWin(){
		boolean gameWon = true;
		boolean prevWon = true;
		for (int i = 0; i < NUMHOMES; i++){
			if (!homeArray[i].isTaken()){
				gameWon = false;
			}
			
			prevWon = homeArray[i].isTaken();
		}
		if(Debug.debugOn){
			System.out.println("Game Won:" + gameWon);
		}
		if (gameWon){
			gameWin();
		}
	}
	private void timeInit(){
		timerLabel = new JLabel("TIME");
		int numComponents = myPanel.getComponentCount();
		Dimension size  = timerLabel.getPreferredSize();
		timerLabel.setBounds(200, FroggerClient.WINHEIGHT - 25, size.width, size.height);
		timerLabel.setForeground(Color.RED);
		timerLabel.setBackground(Color.RED);
		myPanel.add(timerLabel);
		myPanel.setComponentZOrder(timerLabel, numComponents -3);
		
		JLabel livesLabel = new JLabel("Lives:");
		numComponents = myPanel.getComponentCount();
		size  = livesLabel.getPreferredSize();
		livesLabel.setBounds(10, FroggerClient.WINHEIGHT - 25, size.width, size.height);
		livesLabel.setForeground(Color.BLUE);
		myPanel.add(livesLabel);
		myPanel.setComponentZOrder(livesLabel, numComponents -3);
		
		myLives = new JLabel(""+ myFrog.getLifes());
		numComponents = myPanel.getComponentCount();
		size  = myLives.getPreferredSize();
		myLives.setBounds(60, FroggerClient.WINHEIGHT - 25, size.width, size.height);
		myLives.setForeground(Color.GREEN);
		myPanel.add(myLives);
		myPanel.setComponentZOrder(myLives, numComponents -3);
		
		timeScore = new JLabel(""+ totalTime);
		numComponents = myPanel.getComponentCount();
		size  = timeScore.getPreferredSize();
		timeScore.setBounds(90, FroggerClient.WINHEIGHT - 25, 200, size.height);
		timeScore.setForeground(Color.GREEN);
		
		myPanel.add(timeScore);
		myPanel.setComponentZOrder(timeScore, numComponents -3);
		
		timerBar = new JPanel();
		timerBar.setBounds(250, FroggerClient.WINHEIGHT - 30, theTime/2, 25);
		timerBar.setBackground(Color.RED);
		numComponents = myPanel.getComponentCount();
		myPanel.add(timerBar);
		myPanel.setComponentZOrder(timerBar, numComponents -4);
	}
	private void timeCount(){
		if (!NOTIME)
		theTime --;
		
		timerBar.setSize(theTime/2, timerBar.getSize().height);
		if (theTime == 0){
			deathToFrog();
		}
	}
	
	private void resetTime(){
		theTime = INITTIME;
		timerBar.setSize(theTime/2, timerBar.getSize().height);
	}
	
	private void deathToFrog(){
		
		if (!NODEATH){
		JLabel dfHold = myFrog.killFrog();
		if ((myFrog.getLifes())<=0)
			gameOver=true;
		myPanel.add(dfHold);
		myLives.setText(""+myFrog.getLifes());
		int numComponents = myPanel.getComponentCount();
		if (Debug.debugOn == true){
			System.out.println("before dfHold z order" + myPanel.getComponentZOrder(dfHold));
		}
		myPanel.setComponentZOrder(dfHold, numComponents -2);
		if (Debug.debugOn == true){
			System.out.println("after dfHold z order" + myPanel.getComponentZOrder(dfHold));
		}
		
		//reset the Time
		myPanel.repaint();
		resetTime();
		}
	}
	
	private void gameOver(){
		
		gameOverLab = new JLabel("Game Over!\n Click for New Game");
		
		Dimension size = gameOverLab.getPreferredSize();
		gameOverLab.setBounds(FroggerClient.WINWIDTH/2- size.width/2, FroggerClient.WINHEIGHT/2 - size.height/2, size.width, size.height);
		int numComp = myPanel.getComponentCount();
		myPanel.add(gameOverLab);
		myPanel.setComponentZOrder(gameOverLab, numComp - 3);
		myPanel.addMouseListener(this);
		myPanel.repaint();
		this.suspend();
		
	}
	
private void gameWin(){
		
		gameOverLab = new JLabel("Game Won!\n Click for New Game");
		
		
		
		Dimension size = gameOverLab.getPreferredSize();
		gameOverLab.setBounds(FroggerClient.WINWIDTH/2- size.width/2, FroggerClient.WINHEIGHT/2 - size.height/2, size.width, size.height);
		int numComp = myPanel.getComponentCount();
		myPanel.add(gameOverLab);
		myPanel.setComponentZOrder(gameOverLab, numComp - 3);
		myPanel.addMouseListener(this);
		myPanel.repaint();
		this.suspend();
	}
	private void resetVehicles(){	int i;
	int j;

	int vehRow = 0;
	int vehType;
	int initialX;
	int numComps = myPanel.getComponentCount();
	
	for (i = 0; i < NUMCARS; i++) {
		if (Debug.debugOn == true) {
			System.out.println("@@@ in outer loop in initVehicles @@@");

		}
//		Increment verRox
		if ((i % NUMVEHROWS) == 0) {
			vehRow++;
		}
		//Choose which vehicle to display
		vehType = MyRand.choose(0, 3);
		//Even numbered rows get even cars
		if((vehRow % 2)==0){
			if (!((vehType % 2) == 0)){
				vehType -= 1;
			}
		}
		//Odd numbered rows get odd cars
		if(!((vehRow % 2)==0)){
			if ((vehType %2 )==0){
				vehType += 1;
			}
		}
		
		if (Debug.debugOn == true) {
			System.out.println("vehType :" + vehType);
			System.out.println(" *******//vehRow : " + vehRow);
			System.out.println("******// i: " + i);

		}
		
		vehiclePlace(i);
		
		//Vehicle Label stuff
		vehArray[i].resetLabel();
		//JLabel jlHolder = new JLabel();
		//jlHolder = vehArray[i].getObjLabel();
		//myPanel.add(jlHolder);

		if (Debug.debugOn == true) {
			System.out.println("!!!Vehicle added : " + vehArray[i]);
			//System.out.println("!!!Vehicle's label : " + jlHolder);
		}
	}
		
	}
	private void gameReset(){
		myFrog.setLifes(FROGLIVES +1);
		myFrog.resetFrog();
		resetVehicles();
		resetTime();
		
		
		for (int i = 0; i < NUMHOMES; i++){
			homeArray[i].nowTaken(false);
			
			
		}
		for (int i =0; i < homeCount; i++){
			myPanel.remove(myPanel.getComponent(0));
		}
		totalTime = 0;
		homeCount = 0;
		myPanel.repaint();
		
	}
	public void mouseClicked(MouseEvent e){
		if (Debug.debugOn){
			System.out.println("Mouse event:" + e.getPoint());
		}
		
		gameReset();
		try{
			
		this.resume();
		}catch (IllegalThreadStateException ev){
			
		}
		
		
		myPanel.remove(gameOverLab);
		myPanel.removeMouseListener(this);
		//myPanel.requestFocus();
	}
	public void mouseExited(MouseEvent e){
		
	}
	
	public void mouseEntered(MouseEvent e){
		
	}
	public void mousePressed(MouseEvent e){
		
	}
	
	public void mouseReleased(MouseEvent e){
		
	}

}
