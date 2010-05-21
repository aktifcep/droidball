package com.bluerender.game;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class GoalPost {
	private Vector m_position = new Vector();
   
    public int width = 80;
    public int height= 25;
    GoalSide mSide;
    
    public GoalPost(Context context, GoalSide side){    
    	//mBallImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.ball);
    	mSide = side;
    }

	public void drawSprite(Canvas canvas) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		//canvas.drawText("Ball: ["+r1.left +", "+r1.top+", "+r1.right+", "+r1.bottom+", "+ ySpeed +", "+isCollided+"]", 50, 100, p);
		//canvas.drawBitmap(mBallImage, m_position.X, m_position.Y, paint);
		paint.setColor(Color.RED);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(2);
		
		//canvas.drawRect(getBound(), paint);
	}
	public RectF getBound()
	{
		return new RectF(m_position.X, m_position.Y, 
				m_position.X+width, m_position.Y+height);
	}
	
	public void setPosition(int x, int y)
	{
		m_position.setVector(x, y);
	}

	public enum GoalSide {
		PLAYER,
		ENEMY
	}
}
