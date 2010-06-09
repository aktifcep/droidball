package com.bluerender.game;

import java.util.Timer;
import java.util.TimerTask;

import net.phys2d.raw.Contact;
import net.phys2d.raw.collide.BoxCircleCollider;
import net.phys2d.raw.collide.CircleCircleCollider;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.KeyEvent;

import com.bluerender.game.DroidBall.BallDrawMode;
import com.bluerender.game.GameView.GameThread;
import com.bluerender.game.GoalPost.GoalSide;

public class GameManager {
		
	GameState mGameState;
	Environment mEnv;
	Context mContext;
	private Bitmap mGroundImage;
	//game objects
    DroidPlayer player;
    DroidEnemy enemy;
    DroidBall ball;
    Phys2DPlayer p2;
    GoalPost gpPlayer;
    GoalPost gpEnemy;
    GoalPost currentGP;
    String msg;
    RectF mWalls[];
    
    int mPGoals = 0;
    int mEGoals = 0;
    int mGoalBreak = 10;
    //Round time in minutes...
    float roundTime = 1;
    long mRoundStartTime ;
    long mElapseTime;
    
	public GameManager(Environment env, Context context, GameThread gThread)
	{
		this.mEnv = env;
		this.mContext = context;
		mGroundImage = BitmapFactory.decodeResource(mContext.getResources(), 
				R.drawable.ground);
	}
	
	public void Init()
	{
		this.ball = new DroidBall(mContext);
		this.player = new DroidPlayer(mContext);
		this.enemy = new DroidEnemy(mContext);
		
		this.p2 = new Phys2DPlayer();
		gpPlayer = new GoalPost(mContext, GoalSide.PLAYER);
		gpEnemy = new GoalPost(mContext, GoalSide.ENEMY);
		
		//Create walls..
		createWalls();
		
		mGameState = GameState.STATE_INIT;
		
		Timer timer = new Timer();
        timer.schedule(new RoundTimerTask(), ((long)roundTime * 60 * 1000));
        
        mRoundStartTime = System.currentTimeMillis();
	}
	
	class RoundTimerTask extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			mGameState = GameState.STATE_ROUND_END;
		}
	
	}
	public void start()
	{
		mGameState = GameState.STATE_RUNNING;
		//Set initial object postion...
		this.player.setLocation(140, (int)330);
		this.ball.setLocation((int)30, (int)190);
		this.enemy.setLocation(140, (int)50);
		this.enemy.setRotation(180);
		//Set initial object velocity...
		this.player.m_velocity.setVector(0, 0);
		this.ball.m_velocity.setVector(20, 0);
	}
	
	private void createWalls()
	{
		if(mEnv.playArea == null)
			return;
		
		mWalls = new RectF[6];
		int wallWidth = 40;
		try{
			mWalls[0] = new RectF(mEnv.playArea.left-wallWidth, mEnv.playArea.top,
					mEnv.playArea.left, mEnv.playArea.bottom);
			mWalls[1] = new RectF(mEnv.playArea.right, mEnv.playArea.top,
					mEnv.playArea.right+wallWidth, mEnv.playArea.bottom+3);
			
			mWalls[2] = new RectF(mEnv.playArea.left, mEnv.playArea.top-wallWidth,
					mEnv.playArea.left + 100, mEnv.playArea.top+3);
			mWalls[3] = new RectF(mEnv.playArea.left + 185, mEnv.playArea.top-wallWidth,
					mEnv.playArea.right, mEnv.playArea.top+3);
			
			mWalls[4] = new RectF(mEnv.playArea.left, mEnv.playArea.bottom,
					mEnv.playArea.left+105, mEnv.playArea.bottom+wallWidth);
			mWalls[5] = new RectF(mEnv.playArea.left + 185, mEnv.playArea.bottom,
					mEnv.playArea.right, mEnv.playArea.bottom+wallWidth);
			}
			catch(Exception e)
			{
				Log.d("", e.getMessage());
			}
	}
	
	 /**
     * Draws the ship, fuel/speed bars, and background to the provided
     * Canvas.
     */
    public void doDraw(Canvas canvas) {
    	Paint paint = new Paint();
    	paint.setAntiAlias(true); 
    	// empty canvas
    	//canvas.drawARGB(255, 0, 0, 0);
    	
		// Draw Ground Border...
		canvas.drawBitmap(mGroundImage, 0, 0, paint);

		// draw walls...
		drawWalls(canvas);

		// Draw Goal Post...
		drawGoalPost(canvas);

		ball.drawSprite(canvas);
		player.drawSprite(canvas);
		enemy.drawSprite(canvas);
		//p2.drawSprite(canvas);

		if (mGameState == GameState.STATE_GOAL)
		{
			//Draw Game Stats...
			paint.setStrokeWidth(3);
			paint.setTextSize(50);
			paint.setColor(Color.MAGENTA);
			canvas.drawText("Goal....!", 100, 200, paint);
						
			if(currentGP != null)
			{
				ball.setDrawMode(BallDrawMode.Goal);
				
				RectF bound = currentGP.getBound();
				float x = (bound.left + bound.right)/2 - ball.width/2; 
				float y = (bound.top + bound.bottom)/2 - ball.height/2; 
				ball.setLocation((int)x, (int)y);
			}
			
			if(--mGoalBreak == 0)
			{
				//mGameState = GameState.STATE_RUNNING;
				mGoalBreak = 10;
				
				start();
			}
		}
		else if(mGameState == GameState.STATE_ROUND_END)
		{
			String winner="";
			if(mPGoals == mEGoals)
			{
				winner = "Game Draw!";
			}
			else if(mPGoals > mEGoals)
			{
				winner = "Player One Wins!";
			}
			else
				winner = "Player Two Wins!";
			
			this.ball.m_velocity.setVector(0, 0);
			//Draw Game Stats...
			paint.setStrokeWidth(4);
			paint.setStyle(Style.FILL_AND_STROKE);
			//paint.se
			paint.setTextSize(35);
			paint.setColor(Color.parseColor("#357EC7"));
			float tWidth = paint.measureText(winner);
			canvas.drawText(winner + "", canvas.getWidth()/2 - tWidth/2, 200, paint);
		}
		
		//Draw Game Stats...
		paint.setStrokeWidth(2);
		paint.setStyle(Style.FILL_AND_STROKE);
		paint.setTextSize(15);
		paint.setColor(Color.RED);
		canvas.drawText("Goal "+mEGoals, 20, 15, paint);
		//Draw Enemy Goals...
		paint.setColor(Color.GREEN);
		canvas.drawText("Goal "+ mPGoals, 20, mEnv.playArea.bottom + 20, paint);
		//Draw Round Time
		paint.setColor(Color.parseColor("#7E354D"));
		
		canvas.drawText("Time " + mElapseTime, 250, 15, paint);
		
		paint.setStrokeWidth(1);
		paint.setColor(Color.GRAY);
		//canvas.drawText("Message: [" + msg + "]", 50, 200, paint);
		
		//Phys2DUtility.drawContacts(canvas, contacts, contacts.length);
    }
    
    
    @SuppressWarnings("unused")
	private void drawWalls(Canvas canvas)
    {
    	Paint paint = new Paint();
    	if(mWalls == null)
    	{
    		createWalls();
    	}
    	else {
			paint.setColor(Color.RED);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(1);
			for (RectF r : mWalls) {
				canvas.drawRect(r, paint);
			}
		}
    }
    
    private void drawGoalPost(Canvas canvas)
    {
    	Paint paint = new Paint();
    	paint.setStrokeWidth(1);    	
    	paint.setColor(Color.GREEN);
    	paint.setTextSize(15);
    	//canvas.drawText("Game State: "+_gameState, 50, 40, paint);
    	
    	gpEnemy.setPosition(mEnv.canvasWidth/2 - gpEnemy.width/2 , -15);
    	gpPlayer.setPosition(mEnv.canvasWidth/2- gpPlayer.width/2 ,
    			mEnv.canvasHeight-40);
    	
    	gpEnemy.drawSprite(canvas);
    	gpPlayer.drawSprite(canvas);
    }
    
    
    /**
     * 
     * Updates the game.
     */
    public void updateGame() {
        
    	if (mGameState == GameState.STATE_RUNNING) 
    	{
    		//calculate elapse time...
    		mEnv.timeElapsed = mElapseTime = (System.currentTimeMillis() - mRoundStartTime)/1000;
    		
			player.updatePhysics(mEnv);			
			ball.updatePhysics(mEnv);
			enemy.updatePhysics(mEnv, ball);
			//p2.update(mEnv);

			checkCollision();
					
    	}
    	
    	//Release KeyEvent...
        mEnv.keys[GameControl.KEY_DOWN] = false;
        mEnv.keys[GameControl.KEY_UP] = false;
        mEnv.keys[GameControl.KEY_RIGHT] = false;
        mEnv.keys[GameControl.KEY_LEFT] = false;

    }
    
    boolean mCollided = false;
    /** The contacts array used whe nsearching for collisions */
	Contact[] contacts = new Contact[] {new Contact(), new Contact()};
    public void checkCollision()
    {
    	CircleCircleCollider collider3 = new CircleCircleCollider();
    	
    	//Check for player ball collision....
		int count = collider3.collide(contacts, player.getBody(), ball.getBody());
		if( count > 0)
    	{
    		ball.updateAfterCollision(player);
    		player.ApplyBreak();
    	}
		
		//Check for enemy ball collision....
		count = collider3.collide(contacts, enemy.getBody(), ball.getBody());
		if( count > 0)
    	{
    		ball.updateAfterCollision(enemy);
    		//enemy.ApplyBreak();
    	}
		
		//Check for enemy player collision....
		count = collider3.collide(contacts, enemy.getBody(), player.getBody());
		if( count > 0)
    	{
    		//ball.updateAfterCollision(enemy);
    		//enemy.ApplyBreak();
			//float velX;
			//float velY;
			//if(player.m_velocity.X > enemy.m_velocity.X)
			//	velX = player.m_velocity.X > enemy.m_velocity.X;
    	}
		
    	//Check for goal collision...
    	if(gpEnemy.getBound().contains(ball.getBound()) )
    	{
    		mPGoals++;
    		currentGP = gpEnemy;
    		mGameState = GameState.STATE_GOAL;
    	}
    	else if(gpPlayer.getBound().contains(ball.getBound()) )
    	{
    		mEGoals++;
    		currentGP = gpPlayer;
    		mGameState = GameState.STATE_GOAL;
    	}
    	//check ball boundry...
    	if(mEnv.playArea != null && mWalls != null)
    		checkBallBoundry();
    	//Set Msg....
    	msg = "Collided, "+mCollided;
    }
    
    private void checkBallBoundry()
    {
    	RectF ballBound = ball.getBound();
    	float wallForce = 1.5f;
		if( ballBound.intersect(mWalls[0]))
		{
			ball.setLocation((int)mEnv.playArea.left, (int)ballBound.top);
			ball.m_velocity.X = -ball.m_velocity.X * wallForce;
			//ball
		}
		if( ballBound.intersect(mWalls[2]) || ballBound.intersect(mWalls[3]))
		{
			ball.setLocation((int)ballBound.left, (int)mEnv.playArea.top);
			ball.m_velocity.Y = -ball.m_velocity.Y * wallForce;
		}
		if( ball.getBound().intersect(mWalls[1]))
		{
			ball.setLocation((int)mEnv.playArea.right - ball.width, (int)ballBound.top);
			ball.m_velocity.X = -ball.m_velocity.X * wallForce;
		}
		if( ballBound.intersect(mWalls[4]) || ballBound.intersect(mWalls[5]))
		{
			ball.setLocation((int)ballBound.left, (int)mEnv.playArea.bottom - ball.height);
			ball.m_velocity.Y = -ball.m_velocity.Y * wallForce;
		}
    }
    
    
    //process keyboard input
    private void ProcessInput()
    {

    }
    
    /**
     * Handles a key-down event.
     * 
     * @param keyCode the key that was pressed
     * @param msg the original event object
     * @return true
     */
    boolean doKeyDown(int keyCode, KeyEvent msg) {
    	boolean handled = false;       	
        return handled;
        
    }

    /**
     * Handles a key-up event.
     * 
     * @param keyCode the key that was pressed
     * @param msg the original event object
     * @return true if the key was handled and consumed, or else false
     */
    boolean doKeyUp(int keyCode, KeyEvent msg) {
    	boolean handled = false;
        return handled;        
    
    }
	
}

