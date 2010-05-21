package com.bluerender.game;

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
import android.util.Log;
import android.view.KeyEvent;

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
    String msg;
    RectF mWalls[];
    
    int mPGoals = 0;
    int mEGoals = 0;
    int mGoalBreak = 10;
    
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
		try{
			mWalls[0] = new RectF(mEnv.playArea.left-20, mEnv.playArea.top,
					mEnv.playArea.left, mEnv.playArea.bottom);
			mWalls[1] = new RectF(mEnv.playArea.right, mEnv.playArea.top,
					mEnv.playArea.right+20, mEnv.playArea.bottom+3);
			
			mWalls[2] = new RectF(mEnv.playArea.left, mEnv.playArea.top-20,
					mEnv.playArea.left + 100, mEnv.playArea.top+3);
			mWalls[3] = new RectF(mEnv.playArea.left + 185, mEnv.playArea.top-20,
					mEnv.playArea.right, mEnv.playArea.top+3);
			
			mWalls[4] = new RectF(mEnv.playArea.left, mEnv.playArea.bottom,
					mEnv.playArea.left+105, mEnv.playArea.bottom+20);
			mWalls[5] = new RectF(mEnv.playArea.left + 185, mEnv.playArea.bottom,
					mEnv.playArea.right, mEnv.playArea.bottom+20);
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
			
			if(--mGoalBreak == 0)
			{
				//mGameState = GameState.STATE_RUNNING;
				mGoalBreak = 10;
				
				start();
			}
		}
		
		//Draw Game Stats...
		paint.setStrokeWidth(3);
		paint.setTextSize(15);
		paint.setColor(Color.RED);
		canvas.drawText("Goal "+mEGoals, 20, 15, paint);
		//Draw Enemy Goals...
		paint.setColor(Color.GREEN);
		canvas.drawText("Goal "+ mPGoals, 20, mEnv.playArea.bottom + 20, paint);
		
		paint.setStrokeWidth(1);
		paint.setColor(Color.GRAY);
		//canvas.drawText("Message: [" + msg + "]", 50, 200, paint);
		
		//Phys2DUtility.drawContacts(canvas, contacts, contacts.length);
    }
    
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
    	
    	gpEnemy.setPosition(mEnv.canvasWidth/2 - gpEnemy.width/2 , 0);
    	gpPlayer.setPosition(mEnv.canvasWidth/2- gpPlayer.width/2 ,
    			mEnv.canvasHeight-25);
    	
    	gpEnemy.drawSprite(canvas);
    	gpPlayer.drawSprite(canvas);
    }
    
    
    /**
     * 
     * Updates the game.
     */
    public void updateGame() {
        
    	if (mGameState == GameState.STATE_RUNNING) {
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
    	
    	
		int count = collider3.collide(contacts, player.getBody(), ball.getBody());
		
		if( count > 0)
    	//if( mCollided=player.collision(ball.getBound()) && !mCollided)
    	{
    		ball.updateAfterCollision(player);
    		player.ApplyBreak();
    	}
    	//Check for goal collision...
    	if(gpEnemy.getBound().intersect(ball.getBound()) )
    	{
    		mPGoals++;
    		mGameState = GameState.STATE_GOAL;
    	}
    	else if(gpPlayer.getBound().intersect(ball.getBound()) )
    	{
    		mEGoals++;
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
		if( ballBound.intersect(mWalls[0]))
		{
			ball.setLocation((int)mEnv.playArea.left, (int)ballBound.top);
			ball.m_velocity.X = -ball.m_velocity.X;
		}
		if( ballBound.intersect(mWalls[2]) || ballBound.intersect(mWalls[3]))
		{
			ball.setLocation((int)ballBound.left, (int)mEnv.playArea.top);
			ball.m_velocity.Y = -ball.m_velocity.Y;
		}
		if( ball.getBound().intersect(mWalls[1]))
		{
			ball.setLocation((int)mEnv.playArea.right - ball.width, (int)ballBound.top);
			ball.m_velocity.X = -ball.m_velocity.X;
		}
		if( ballBound.intersect(mWalls[4]) || ballBound.intersect(mWalls[5]))
		{
			ball.setLocation((int)ballBound.left, (int)mEnv.playArea.bottom - ball.height);
			ball.m_velocity.Y = -ball.m_velocity.Y;
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

