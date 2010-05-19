package com.bluerender.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region.Op;
import android.util.Log;


/**
 * @author Shaheryar
 *
 */
public class DroidBall {
	
	private Bitmap []mBallImage;
	private int image_seq;
	private Vector m_position = new Vector();
    public Vector m_velocity = new Vector();
    //private float m_scalarVel = 0f;
    
    public int width;
    public int height;
	
    public DroidBall(Context context){  
    	mBallImage = new Bitmap[4];
    	mBallImage[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ball_1);
    	mBallImage[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ball_2);
    	mBallImage[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ball_3);
    	mBallImage[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ball_4);
    	
    	m_position.setVector(50, 150);
    	width = mBallImage[0].getWidth();
    	height = mBallImage[0].getHeight();
    }
    
    public void setLocation(int x, int y)
    {
    	m_position.setVector(x, y);
    }

	public void drawSprite(Canvas canvas) {
		Paint paint = new Paint();
		
		paint.setColor(Color.YELLOW);
    	paint.setTextSize(10);
		RectF r1 = getBound();
		canvas.drawText("Ball: [("+r1.left +", "+r1.top+", "+r1.right+", "+r1.bottom+"), ("+ m_velocity.X +", "+m_velocity.Y+")]",
						15, 100, paint);
		
		canvas.drawBitmap(mBallImage[image_seq++], m_position.X, m_position.Y, paint);
		if(image_seq == 4)
		{
			image_seq = 0;
		}
		else if((int)m_velocity.X == 0 && (int)m_velocity.Y == 0)
		{
			image_seq = 0;
		}
	}
	public RectF getBound()
	{
		return new RectF(m_position.X, m_position.Y, 
				m_position.X+width, m_position.Y+height);
	}

	public void updatePhysics(Environment env) {
		boolean sideStruck = false;
		
		if(Math.abs(m_velocity.X) > 0)
			m_velocity.X = (int)(Math.abs(m_velocity.X)-1)*(m_velocity.X/Math.abs(m_velocity.X));
		else
			m_velocity.X = 0;
		if(Math.abs(m_velocity.Y) > 0)
			m_velocity.Y = (int)(Math.abs(m_velocity.Y)-1)*(m_velocity.Y/Math.abs(m_velocity.Y));
		else
			m_velocity.Y = 0;
		

		//if(!sideStruck)
			m_position.offset(m_velocity.X, m_velocity.Y);
	}
	
	public void updateAfterCollision(DroidPlayer player)
	{
		this.m_velocity.setVector(player.m_velocity.X+1, player.m_velocity.Y+1);
		boolean isCollided = false;
		float x = m_position.X;
		float y = m_velocity.Y;
		RectF rect = player.getBound();
		if(x > rect.left)
		{
			if(y > rect.top)
			{
				
			}
		}	
		
	}
	
	public boolean collision()
	{		
		return false;
	}

}
