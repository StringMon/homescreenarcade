package com.homescreenarcade.mazeman;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.homescreenarcade.ArcadeCommon;


// direction notes: 1 = up, 2 = down, 3 = right, 4 = left
/*
 * GameEngine class is the controller of the game. GameEngine oversees updates 
 * 		models(maze, pacmon, monster) as well as call drawing.
 * 		
 */

public class GameEngine implements Runnable {
	private final static int    MAX_FPS = 50;
	// maximum number of frames to be skipped
	private final static int    MAX_FRAME_SKIPS = 5;
	// the frame period
	final static int    FRAME_PERIOD = 1000 / MAX_FPS;
	static final int  RIGHT = 1, LEFT = 2, UP = 4, DOWN = 8;
	static final int RD = 9, LD = 10, RU = 5, LU = 6, RDU = 13, LDU = 14, RLD = 11, RLU = 7, RLUD = 15;
	
	final static int 	READY = 0,RUNNING = 1, GAMEOVER = 2, WON = 3, DIE = 4;
	
	private Maze maze;
	private Thread mThread;
	Pacmon pacmon;
	ArrayList<Monster> ghosts;
	
	int playerScore;
	int timer; int timerCount;
	int lives;
	
	int gameState;    // ready = 0; running = 1; lost == 2; won = 3;
	
	int powerMode;		// for count down of power speed
	int pNormalSpeed;	// pacmon normal moving speed
	int pPowerSpeed;	// pacmon power moving speed, when eat Power
	int inputDirection;
	int pX, pY;
	int newDirection;
	
	
	ArrayList<Integer> ghostArray[];
	int directionMaze[][];
	int mazeArray[][];
	int blockSize = 32;
	int mazeRow, mazeColumn;
	
	boolean isRunning;
	
	private int gX, gY;
	public int level=-1, status=-1;
	
	
	//timer
	private long beginTime; // the time when the cycle begun
	private long timeDiff; // the time it took for the cycle to execute
	private int sleepTime; // ms to sleep (<0 if we're behind)
	private int framesSkipped; // number of frames being skipped
	
	
	private long readyCountDown;
	
	private SoundEngine soundEngine;

    private final LocalBroadcastManager statusMgr;

    //Constructor create players, ghosts and Maze
	public GameEngine(Context context, SoundEngine soundEngine, int level) {
        this.soundEngine = soundEngine;

        //soundEngine.playReady();

        timer = 120;
        timerCount = 0;
        gameState = 0;

        statusMgr = LocalBroadcastManager.getInstance(context);
        resetGameplay(level);


        if (context instanceof GameActivity) {
            isRunning = true;
            mThread = new Thread(this);
            mThread.start();
        }
    }

    void resetGameplay(int level) {
        playerScore = 0;
        this.level = level;
        loadMaze(level);
        lives = pacmon.getpLives();
        
        updateStatus(true);
    }

    void loadMaze(int level) {
        // maze stuff

        maze = new Maze();
        mazeArray = maze.getMaze(level);
        mazeRow = maze.getMazeRow();
        mazeColumn = maze.getMazeColumn();
        directionMaze = maze.getDirectionMaze(level);
        ghostArray = maze.getGhostArray();

        ghosts = new ArrayList<Monster>();

        int gX = maze.getGhostSpawnLocX();
        int gY = maze.getGhostSpawnLocY();
        ghosts.add(new Monster());
        ghosts.add(new Monster());
        ghosts.add(new Monster());

        pacmon = new Pacmon();  // new pacmon

        pNormalSpeed = pacmon.getpNormalSpeed();
        pPowerSpeed = pacmon.getpPowerSpeed();
    }

    public void updateStatus(boolean resetScore) {
        Intent statusIntent = new Intent(ArcadeCommon.ACTION_STATUS)
                .putExtra(ArcadeCommon.STATUS_LEVEL, level)
                .putExtra(ArcadeCommon.STATUS_LIVES, lives)
                .putExtra(ArcadeCommon.STATUS_RESET_SCORE, resetScore);
        statusMgr.sendBroadcast(statusIntent);
    }

    //update
	public void update(){
		updateTimer();
		updatePac();
		updateGhost();
	}
	
	public void updatePac(){
		int pNormalSpeed = pacmon.getpNormalSpeed();
		int XmodW, YmodH;
		int boxX, boxY;
		pX = pacmon.getpX();
		pY = pacmon.getpY();
		
		XmodW = pX % blockSize;
		YmodH = pY % blockSize;
		boolean movable = true;
		
		// check direction and change if it is allowed
		if(XmodW == 0 && YmodH == 0){
			boxX = pX / blockSize;
			boxY = pY / blockSize;

			if (inputDirection == LEFT){  // move left allowed if can move to left
		        if (boxX > 0 )
		            if (boxX == 0 || mazeArray[boxY][boxX - 1] != 0)
		                newDirection = inputDirection;
			}
			if (inputDirection == RIGHT){   // move right
		        if (boxX < mazeColumn )
		            if (boxX == mazeColumn - 1 || mazeArray[boxY][boxX + 1] != 0) 
		                newDirection = inputDirection;
			}	
			if (inputDirection == DOWN){ // move down
				if (boxY < mazeRow)
					if (mazeArray[boxY + 1][boxX] != 0 && mazeArray[boxY + 1][boxX] != 3)
						newDirection = inputDirection;
			}
			if (inputDirection == UP) { // move up
		        if (boxY > 0 )
		            if (mazeArray[boxY - 1][boxX] != 0 && mazeArray[boxY - 1][boxX] != 3)
		                newDirection = inputDirection;
			}
		} else {  // change opposite direction
			if (newDirection != inputDirection){
		        if (((inputDirection == UP) || (inputDirection == DOWN)) && (XmodW==0) && (YmodH!=0)){
		            newDirection = inputDirection;
		        }
		        if (((inputDirection == RIGHT) || (inputDirection == LEFT)) && (YmodH==0) && (XmodW!=0) ){
		            newDirection = inputDirection;
		        }
		    }
		}

		pacmon.setDir(newDirection);
		
		//evaluate at intersection, collision detection
		if(XmodW == 0 && YmodH == 0){
			
			boxX = pX / blockSize;
			boxY = pY / blockSize;
			
			boxX %= mazeColumn;
			boxY %= mazeRow;
			eatFoodPower(boxX, boxY);
			
			movable = true;
			
			if (newDirection == LEFT){  // move left
                if (boxX > 0 )
                    if ( mazeArray[boxY][boxX - 1] == 0){
                        movable = false;
                    }
			}
			
			if (newDirection == RIGHT){   // move right
                if (boxX < mazeColumn -1 ) 
                    if ( mazeArray[boxY][boxX + 1] == 0) {
                        movable = false;
                    }
			}	
			
			if (newDirection == DOWN){ // move down
				if (boxY < mazeRow - 1)
					if (mazeArray[boxY + 1][boxX] == 0 || mazeArray[boxY + 1][boxX] == 3){
						movable = false;
				}
			}
			if (newDirection == UP) { // move up
                if (boxY > 0 ) 
					if (mazeArray[boxY - 1][boxX] == 0 || mazeArray[boxY - 1][boxX] == 3){
						movable = false;
					}
			}

		}
		
		if( movable){
			if (newDirection == UP) { // up
				if (powerMode > 0) 		pY = pY - pPowerSpeed; // in power mode
				else			pY = pY - pNormalSpeed;		   // normal mode
			}
			else if (newDirection == DOWN){  // down
				if (powerMode > 0) 		pY = pY + pPowerSpeed; // power mode
				else			pY = pY + pNormalSpeed;		   // normal mode
			}
			else if (newDirection == RIGHT){ // right
				if (powerMode > 0) 		pX = pX + pPowerSpeed; // power mode
				else			pX = pX + pNormalSpeed;		   // normal mode
			}
			else if (newDirection == LEFT) {// left
				if (powerMode > 0) 		pX = pX - pPowerSpeed;	// power mode
				else			pX = pX - pNormalSpeed;			// normal mode
			}
		}
		
		if(pX == 448)
			pX = 4;
		if(pX == 0)
			pX = 444;
		
		pacmon.setpX(pX);
		pacmon.setpY(pY);
		
	}
	


	//update ghost movements and locations
	public void updateGhost(){
		int gNormalSpeed = ghosts.get(0).getNormalSpeed();
		int XmodW, YmodH;
		int boxX, boxY;
		int gX, gY;
		
		
		for (int i = 0; i < ghosts.size(); i++) {
			gX = ghosts.get(i).getX();
			gY = ghosts.get(i).getY();
			XmodW = gX % blockSize;
			YmodH = gY % blockSize;
			
			
			// check direction and change if it is allowed
			if (XmodW == 0 && YmodH == 0) {
				int crossing;
				boxX = gX / blockSize;
				boxY = gY / blockSize;
				
				//check if at crossing using directional maze and update new direction with smart movement
				crossing = directionMaze[boxY][boxX];
				if (crossing > 0){
					if (timer % 8 - (level) == i){
						if (crossing == 1) moveGhostSmart(RD, ghosts.get(i));
						if (crossing == 2) moveGhostSmart(LD, ghosts.get(i));
						if (crossing == 3) moveGhostSmart(RU, ghosts.get(i));
						if (crossing == 4) moveGhostSmart(LU, ghosts.get(i));
						if (crossing == 5) moveGhostSmart(RDU, ghosts.get(i));
						if (crossing == 6) moveGhostSmart(LDU, ghosts.get(i));
						if (crossing == 7) moveGhostSmart(RLD, ghosts.get(i));
						if (crossing == 8) moveGhostSmart(RLU, ghosts.get(i));
						if (crossing == 9) moveGhostSmart(RLUD, ghosts.get(i));
					} else {
						if (crossing == 1) moveGhostRandom(RD, ghosts.get(i));
						if (crossing == 2) moveGhostRandom(LD, ghosts.get(i));
						if (crossing == 3) moveGhostRandom(RU, ghosts.get(i));
						if (crossing == 4) moveGhostRandom(LU, ghosts.get(i));
						if (crossing == 5) moveGhostRandom(RDU, ghosts.get(i));
						if (crossing == 6) moveGhostRandom(LDU, ghosts.get(i));
						if (crossing == 7) moveGhostRandom(RLD, ghosts.get(i));
						if (crossing == 8) moveGhostRandom(RLU, ghosts.get(i));
						if (crossing == 9) moveGhostRandom(RLUD, ghosts.get(i));
				
					}
				}

			}

			
			
			//get direction after calculate
			int ghostCurDir = ghosts.get(i).getDir();

			if (ghostCurDir == UP) // up
				gY = gY - gNormalSpeed;
			if (ghostCurDir == DOWN) // down
				gY = gY + gNormalSpeed;
			if (ghostCurDir == RIGHT) // right
				gX = gX + gNormalSpeed;
			if (ghostCurDir == LEFT) // left
				gX = gX - gNormalSpeed;
			
			// set new location of ghost after moving
			ghosts.get(i).setX(gX);
			ghosts.get(i).setY(gY);
			
			checkCollision(gX, gY);

		}
	}
	
	private void moveGhostSmart(int index, Monster ghost){
		int pX = pacmon.getpX();
		int pY = pacmon.getpY();
		if (ghost.getY() > pY && ghostArray[index].contains(UP))
			ghost.setDir(UP);
		else if (ghost.getY() < pY && ghostArray[index].contains(DOWN))
			ghost.setDir(DOWN);
		else if (ghost.getX() > pX && ghostArray[index].contains(LEFT))
			ghost.setDir(LEFT);
		else if (ghost.getX() < pX && ghostArray[index].contains(RIGHT))
			ghost.setDir(RIGHT);
		else // if no possible smart direction, move ghost randomly
			moveGhostRandom(index, ghost);
		
	}
	
	// move ghost using directional array
	private void moveGhostRandom(int index, Monster ghost){
		int n = (int)(Math.random() * ghostArray[index].size()); // randomize
		int d = ghostArray[index].get(n);  //apply random to get direction
		ghost.setDir(d);
	
	}
	
	//check if ghost touch player
	private void checkCollision(int gX, int gY){
		int pX = pacmon.getpX();
		int pY = pacmon.getpY();
		int radius = 10;
		
		if (Math.abs(pX - gX) + Math.abs(pY - gY) < radius) //ghost touches player
			diePacmon();
	}
	
	//when ghost touches player, pacmon dies
	private void diePacmon(){
		lives--;
		gameState = DIE;

        Intent statusIntent = new Intent(ArcadeCommon.ACTION_STATUS)
                .putExtra(ArcadeCommon.STATUS_LIVES, lives);
        statusMgr.sendBroadcast(statusIntent);
        
		//reset
		pacmon.reset();
		for (int i = 0; i < ghosts.size(); i++){
			ghosts.get(i).reset();
		}
		
		soundEngine.stopMusic();
		if (lives < 0) {
			gameState = GAMEOVER;
			soundEngine.playGameOver();
		} else {
            soundEngine.playDie();
        }
	}
	
	// eat food ==> score and power ==> speed
	private void eatFoodPower(int boxX, int boxY) {
		
		if (mazeArray[boxY][boxX] == 1){
			mazeArray[boxY][boxX] = 5;
			playerScore++;   // increase score

            Intent statusIntent = new Intent(ArcadeCommon.ACTION_STATUS)
                    .putExtra(ArcadeCommon.STATUS_INCREMENT_SCORE, 1);
            statusMgr.sendBroadcast(statusIntent);

            soundEngine.playEatFood();
			
			if (playerScore == maze.getFoodCount()){
				gameState = WON;
				soundEngine.stopMusic();
			}
		}
		
		// eat power
		if (mazeArray[boxY][boxX] == 2){
			mazeArray[boxY][boxX] = 5;
			this.powerMode = 5;// blank
gameState = WON;
		}
	}
	
	// count down timer once per MAX_FPS
	private void updateTimer(){
		timerCount++;
		if (timerCount % 40 == 0){
			timer--;
			timerCount = 0;
			powerMode--;
		}
		if (timer == -1){
			gameState = GAMEOVER;  // LOST
			soundEngine.stopMusic();
			soundEngine.playGameOver();
		}
		
	}
	

	public void run() {
		while (isRunning){
			if (gameState == READY)    updateReady();
			if (gameState == RUNNING)  updateRunning();
			if (gameState == GAMEOVER) updateGameOver();
			if (gameState == WON)	   updateWon();
			if (gameState == DIE)	   updateDie();
		}
	}
	
	public void updateDie() {
		beginTime = System.currentTimeMillis();
	
		sleepTime = (int) (FRAME_PERIOD - timeDiff);

		if (sleepTime > 0) {
			// if sleepTime > 0 we're OK
			try {
				// send the thread to sleep for a short period
				// very useful for battery saving
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
			}
		}
		
		timeDiff += System.currentTimeMillis() - beginTime;
		if(timeDiff >= 300)
			gameState = READY;
	}
	
	// loop through ready if gameState is READY
    void updateReady(){
		beginTime = System.currentTimeMillis();

		readyCountDown = 1L - timeDiff/1000;		
		sleepTime = (int) (FRAME_PERIOD - timeDiff);

		if (sleepTime > 0) {
			// if sleepTime > 0 we're OK
			try {
				// send the thread to sleep for a short period
				// very useful for battery saving
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
			}
		}
		
		timeDiff += System.currentTimeMillis() - beginTime;
		if(timeDiff >= 1000) {
			gameState = RUNNING;
			soundEngine.playMusic();
		}
		
	}
	
	// loop through running if gameState is RUNNING
    void updateRunning(){
		beginTime = System.currentTimeMillis();
		framesSkipped = 0; // resetting the frames skipped
		
		update();
	
		// calculate how long did the cycle take
		timeDiff = System.currentTimeMillis() - beginTime;
		// calculate sleep time
		sleepTime = (int) (FRAME_PERIOD - timeDiff);

		if (sleepTime > 0) {
			// if sleepTime > 0 we're OK
			try {
				// send the thread to sleep for a short period
				// very useful for battery saving
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
			}
		}
		
	}
	
	void updateGameOver(){
		pause();
	}
	
	void updateWon(){
		this.level+=1;
		this.status=1;
//		pause();

        Intent statusIntent = new Intent(ArcadeCommon.ACTION_STATUS)
                .putExtra(ArcadeCommon.STATUS_LEVEL, level);
        statusMgr.sendBroadcast(statusIntent);
	}
	
	private void updateLevel(int level, int status)
	{
		
		
		
		//DatabaseHandler db = new DatabaseHandler(this);
		
		
		//SQLiteDatabase levelDB = SQLiteDatabase.openOrCreateDatabase("level.db", SQLiteDatabase.OPEN_READWRITE);
	//	SQLiteDatabase levelDB = this.openOrCreateDatabase("level.db", SQLiteDatabase.OPEN_READONLY, null);		
//		ContentValues cv = new ContentValues();
//    	try{
//    	//for first level
//    	cv.put("finish", 1); 
//        levelDB.update("level", cv, "stage=" + i, null);
//    	}catch(SQLException e){
//			e.printStackTrace();
//		}
		
	}
	
	public void pause() {
		isRunning = false;
//        mThread = null;
    }
	
	public void resume() {
//        if (!isRunning) {
            isRunning = true;
//            mThread = new Thread(this);
//            mThread.start();
//        }
	}
	
	// using accelerometer to set direction of player
	public void setInputDir(int dir){
		this.inputDirection = dir;
	}
	
	public Maze getMaze(){
		return this.maze;
	}
	
	public int[][] getMazeArray(){
		return this.mazeArray;
	}

	public int getMazeRow() {
		return this.mazeRow;
	}

	public int getMazeColumn() {
		return this.mazeColumn;
	}

	public String getTimer() {
		return "time: " + timer;
	}

	public String getLives() {
		return "life: " + lives;
	}

	public String getPlayerScore() {
		return "score: " + playerScore;
	}
	
	public int getGameState(){
		return gameState;
	}

	public long getReadyCountDown(){
		return readyCountDown;
	}
	
	
	
}
