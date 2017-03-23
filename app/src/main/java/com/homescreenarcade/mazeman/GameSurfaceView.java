package com.homescreenarcade.mazeman;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.homescreenarcade.R;

import java.util.ArrayList;

//deals with rendering the game data
public class GameSurfaceView extends SurfaceView implements Runnable {

	private final static int    MAX_FPS = 50;
	// maximum number of frames to be skipped
	private final static int    MAX_FRAME_SKIPS = 5;
	// the frame period
	private final static int    FRAME_PERIOD = 1000 / MAX_FPS;
	static final int  RIGHT = 1, LEFT = 2, UP = 4, DOWN = 8;
	final static int 	READY = 0;
    final static int RUNNING = 1;
    final static int GAMEOVER = 2;
    final static int WON = 3;
    final static int DIE = 4;
	private final static String textOver = "GAME OVER", textCongrats = "You Won"
								, textNextLevel = "You unlocked next level", textReady = "Ready Go";
	
	
	private SurfaceHolder surfaceHolder;
	private Thread surfaceThread = null;
	boolean isRunning = false;
	
	int currentFrame = 0; 	// for drawing sprite
	int mCurrentFrame = 0;
	int movingTextX, movingTextY;   // for ready and gameover screen
	
	private Pacmon pacmon;
	
	private GameEngine gameEngine;
	private ArrayList<Monster> ghosts;
	
	// bitmap
	private Bitmap pac_img, wall, door, bluey_img, redy_img, yellowy_img, food, power ;
	
	//maze info
	private int[][] mazeArray;
	private int mazeRow, mazeColumn;
	private float blockSize;
	
	private Maze maze;
	
	private Paint paint, paint2, paint3;
	
	private Context mContext;
	
	private int gameState;
	private Rect srcRect;
	private Rect dstRect;
	private Rect[] pSrcUp = new Rect[3];
	private Rect[] pSrcDown = new Rect[3];
	private Rect[] pSrcLeft = new Rect[3];
	private Rect[] pSrcRight = new Rect[3];
	private Rect[] pDst = new Rect[12];
	
	private Rect[] gSrcUp = new Rect[2];
	private Rect[] gSrcDown = new Rect[2];
	private Rect[] gSrcLeft = new Rect[2];
	private Rect[] gSrcRight = new Rect[2];
	private Rect[] gDst = new Rect[8];
	
	// draw timing data
	private long beginTime; // the time when the cycle begun
	private long timeDiff; // the time it took for the cycle to execute
	private int sleepTime; // ms to sleep (<0 if we're behind)
	private int framesSkipped; // number of frames being skipped

	private SoundEngine soundEngine; // sound manager
	private boolean isPlayOn;
	
	private float screenWidth;
	private float screenHeight;
	private float blockScaleFactor;
	private float sentenceWidth, drawTextStartingX;
	
	private int counterForSprite=0;
	
	public GameSurfaceView(Context context, GameEngine gameEngine, int sWidth, int sHeight) {
		
		super(context);
		
		this.gameEngine = gameEngine;
		this.pacmon = gameEngine.pacmon;
		gameState = READY;
        
        gameEngine.updateStatus(false);
        
		mContext = context;
		
		soundEngine = new SoundEngine(context);
		isPlayOn = true;
		
		screenWidth = sWidth;
		screenHeight = sHeight;
		
		blockSize = screenWidth / 15.f;  // size of block
		blockScaleFactor = blockSize / 32.f;  // scale factor for block
		
		maze = gameEngine.getMaze();
		mazeArray = gameEngine.getMazeArray();
		mazeRow = maze.getMazeRow();
		mazeColumn = maze.getMazeColumn();

		initBitmap();  // init all Bitmap and its components
		initSprite();  // init spite
		
		ghosts = gameEngine.ghosts;
		
		surfaceHolder = getHolder();
		isRunning = true;
		setKeepScreenOn(true);
	}
	
	private void initBitmap(){
		wall = BitmapFactory.decodeResource(getResources(), R.drawable.wall);
		door = BitmapFactory.decodeResource(getResources(), R.drawable.ghost_door);
		food = BitmapFactory.decodeResource(getResources(), R.drawable.food);
		power = BitmapFactory.decodeResource(getResources(), R.drawable.power);
		pac_img = BitmapFactory.decodeResource(getResources(), R.drawable.pacmon_sprite_green);
		bluey_img = BitmapFactory.decodeResource(getResources(), R.drawable.bluey_sprite);
		redy_img = BitmapFactory.decodeResource(getResources(), R.drawable.redy_sprite);
		yellowy_img = BitmapFactory.decodeResource(getResources(), R.drawable.yellowy_sprite);
		
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);
		paint.setTextSize((int)(blockSize/(1.5)));
		
		paint2 = new Paint();
		paint2.setAntiAlias(true);
		paint2.setColor(Color.WHITE);
		paint2.setTextSize(blockSize*2); // 2 times the size of block width
		
		paint3 = new Paint();
		paint3.setAntiAlias(true);
		paint3.setColor(Color.WHITE);
		paint3.setTextSize(30);
		
	}
	
	private void initSprite(){
		pSrcUp[0] = new Rect(0, 0, 32, 32);
		pSrcUp[1] = new Rect(32, 0, 64, 32);
		pSrcUp[2] = new Rect(64, 0, 96, 32);
		
		pSrcDown[0] = new Rect(0, 32, 32, 64);
		pSrcDown[1] = new Rect(32, 32, 64, 64);
		pSrcDown[2] = new Rect(64, 32, 96, 64);
		
		pSrcRight[0] = new Rect(0, 64, 32, 96);
		pSrcRight[1] = new Rect(32, 64, 64, 96);
		pSrcRight[2] = new Rect(64, 64, 96, 96);
		
		pSrcLeft[0] = new Rect(0, 96, 32, 128);
		pSrcLeft[1] = new Rect(32, 96, 64, 128);
		pSrcLeft[2] = new Rect(64, 96, 96, 128);
		
		gSrcUp[0] = new Rect(0, 0, 32, 32);
		gSrcUp[1] = new Rect(32, 0, 64, 32);
		
		gSrcDown[0] = new Rect(0, 32, 32, 64);
		gSrcDown[1] = new Rect(32, 32, 64, 64);
		
		gSrcRight[0] = new Rect(0, 64, 32, 96);
		gSrcRight[1] = new Rect(32, 64, 64, 96);
		
		gSrcLeft[0] = new Rect(0, 96, 32, 128);
		gSrcLeft[1] = new Rect(32, 96, 64, 128);
		
	}
	
	//thread to update and draw. Game loop
	public void run() {
		Canvas canvas;
		
		while (isRunning) {
			canvas = null;
			if (gameEngine.getGameState() == READY){
				if (isPlayOn){
				//	soundEngine.play(4);
					isPlayOn = false;
				}
				updateReady(canvas);

			}
			if (gameEngine.getGameState() == RUNNING)  updateRunning(canvas);
			if (gameEngine.getGameState() == GAMEOVER) updateGameOver(canvas);
			if (gameEngine.getGameState() == WON)	   updateWon(canvas);
			if (gameEngine.getGameState() == DIE)	   updateDie(canvas);
		}
	}
	
	// when game is in ready mode
	private void updateReady(Canvas canvas){
		try {
			canvas = surfaceHolder.lockCanvas();
			if (canvas == null) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				surfaceHolder = getHolder();
			} else {

				synchronized (surfaceHolder) {
                    drawGame(canvas);
					
					//long time = 5L - timeDiff/1000;

					//measure the text then draw it at center
					sentenceWidth = paint2.measureText(textReady);
				    drawTextStartingX = (screenWidth - sentenceWidth) / 2;
					canvas.drawText(textReady, drawTextStartingX , screenHeight/2, paint2);
					
					try {
						Thread.sleep(25);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}
		} finally {
			// in case of an exception the surface is not left in
			// an inconsistent state
			if (canvas != null) {
				surfaceHolder.unlockCanvasAndPost(canvas);
			}
		}
		
	}

    public void drawGame(Canvas canvas) {
        canvas.drawRGB(0, 0, 0);
        drawMaze(canvas); // draw updated maze
        drawPacmon(canvas);
        drawGhost(canvas);
//        drawScore(canvas);
    }

    void updateRunning(Canvas canvas){
		try {
			canvas = surfaceHolder.lockCanvas();
			if (canvas == null) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				surfaceHolder = getHolder();
			} else {

				synchronized (surfaceHolder) {
					beginTime = System.currentTimeMillis();
					framesSkipped = 0; // resetting the frames skipped

                    drawGame(canvas);
					
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
			}
		} finally {
			// in case of an exception the surface is not left in
			// an inconsistent state
			if (canvas != null) {
				surfaceHolder.unlockCanvasAndPost(canvas);
			}
		}
	}
	
	//dead animation
    void updateDie(Canvas canvas){
		try {
			canvas = surfaceHolder.lockCanvas();
			if (canvas == null) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				surfaceHolder = getHolder();
			} else {
				synchronized (surfaceHolder) {
                    drawGame(canvas);

					try {
						Thread.sleep(25);
					} catch (InterruptedException e) {

						e.printStackTrace();
					}
				}
			}
		} finally {
			// in case of an exception the surface is not left in
			// an inconsistent state
			if (canvas != null) {
				surfaceHolder.unlockCanvasAndPost(canvas);
			}
		}
	}
	
	void updateGameOver(Canvas canvas){
		canvas = surfaceHolder.lockCanvas();
		isRunning = false;
		
		//measure the text then draw it at center
		sentenceWidth = paint2.measureText(textOver);
	    drawTextStartingX = (screenWidth - sentenceWidth) / 2;
		canvas.drawText(textOver, drawTextStartingX , screenHeight/2 - blockSize*2, paint2);
		
		sentenceWidth = paint2.measureText(gameEngine.getPlayerScore());
	    drawTextStartingX = (screenWidth - sentenceWidth) / 2;
		canvas.drawText(gameEngine.getPlayerScore(), drawTextStartingX, screenHeight/2 + blockSize*2, paint2);
		
		surfaceHolder.unlockCanvasAndPost(canvas);
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		
		((Activity) mContext).finish();
	}
	
	void updateWon(Canvas canvas){
		canvas = surfaceHolder.lockCanvas();
		isRunning = false;
		
		//measure the text then draw it at center
		sentenceWidth = paint2.measureText(textCongrats);
	    drawTextStartingX = (screenWidth - sentenceWidth) / 2;
		canvas.drawText(textCongrats, drawTextStartingX , screenHeight/2 - blockSize*2, paint2);
		
//		sentenceWidth = paint2.measureText(textNextLevel);
//	    drawTextStartingX = (screenWidth - sentenceWidth) / 2;
//		canvas.drawText(textNextLevel, drawTextStartingX, screenHeight/2 , paint2);
		
		sentenceWidth = paint2.measureText(gameEngine.getPlayerScore());
	    drawTextStartingX = (screenWidth - sentenceWidth) / 2;
		canvas.drawText(gameEngine.getPlayerScore(), drawTextStartingX, screenHeight/2, paint2);
		
		surfaceHolder.unlockCanvasAndPost(canvas);
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		
		
		((Activity) mContext).finish();
	}
	

	// draw current location of ghosts
	private void drawGhost(Canvas canvas) {
		mCurrentFrame = ++mCurrentFrame % 2;
		for (int i = 0; i < gameEngine.ghosts.size(); i++) {
			int direction = ghosts.get(i).getDir();

			if (direction == UP)	srcRect = gSrcUp[mCurrentFrame];
			else if (direction == DOWN)		srcRect = gSrcDown[mCurrentFrame];
			else if (direction == RIGHT)		srcRect = gSrcRight[mCurrentFrame];
			else 	srcRect = gSrcLeft[mCurrentFrame];	
			
			int gX = Math.round(ghosts.get(i).getX() * blockScaleFactor);
			int gY = Math.round(ghosts.get(i).getY() * blockScaleFactor);
			
			Rect dst = new Rect(gX, gY, (int)(gX + blockSize), (int) (gY + blockSize));
				
			if (i == 0)
				canvas.drawBitmap(bluey_img, srcRect, dst, null);
			else if (i == 1)
				canvas.drawBitmap(redy_img, srcRect, dst, null);
			else if (i == 2)
				canvas.drawBitmap(yellowy_img, srcRect, dst, null);
		}
	}

	// draw pacmon 
	private void drawPacmon(Canvas canvas) {
		
		if(counterForSprite>90)
			counterForSprite=0;
		
		counterForSprite++;
		
		if(counterForSprite%6==0)
		{
		currentFrame = ++currentFrame % 3;
		}
		
		
		//currentFrame = ++currentFrame % 3;
		
		int direction = pacmon.getDir(); // get current direction of pacmon
		
		if (direction == UP)	srcRect = pSrcUp[currentFrame];
		else if (direction == DOWN)		srcRect = pSrcDown[currentFrame];
		else if (direction == RIGHT)		srcRect = pSrcRight[currentFrame];
		else 	srcRect = pSrcLeft[currentFrame];	
	
		int pX = Math.round(pacmon.getpX() * blockScaleFactor);
		int pY = Math.round(pacmon.getpY() * blockScaleFactor);

		Rect dst = new Rect(pX, pY, (int)(pX + blockSize), (int) (pY + blockSize));
		canvas.drawBitmap(pac_img, srcRect, dst, null);
		
	}
	
	// draw score
	public void drawScore(Canvas canvas){
		canvas.drawText(gameEngine.getLives(), 40, blockSize * 23, paint);
		
		canvas.drawText(gameEngine.getPlayerScore(), 40, blockSize * 24 , paint);
		canvas.drawText(gameEngine.getTimer(), screenWidth/2, blockSize * 23, paint);
	}
	

	public void pause() {
        isRunning = false;
        if (surfaceThread  != null) {
            while (true) {
                try {
                    surfaceThread.join();
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                break;
            }
            surfaceThread = null;
        }
	}
	
	public void resume() {
		isRunning = true;
		surfaceThread = new Thread(this);
		surfaceThread.start();
		setKeepScreenOn(true);
	}
	
	
	@Override
	public void draw(Canvas canvas) {
        super.draw(canvas);
	}
	
	// draw current maze with food
	public void drawMaze(Canvas canvas){
		for (int i = 0; i < mazeRow; i++){
			for (int j = 0; j < mazeColumn; j++){
				if (mazeArray[i][j] == 0)
					canvas.drawBitmap(wall, j*blockSize, i*blockSize, null);
				if (mazeArray[i][j] == 3)
					canvas.drawBitmap(door, j*blockSize, i*blockSize, null);
				if (mazeArray[i][j] == 1)
					canvas.drawBitmap(food, j*blockSize, i*blockSize, null);
				if (mazeArray[i][j] == 2)
					canvas.drawBitmap(power, j*blockSize, i*blockSize, null);
			}
		}
	}
}


