package com.bluerender.game;

import net.phys2d.raw.Body;
import net.phys2d.raw.Contact;
import net.phys2d.raw.collide.CircleCircleCollider;
import net.phys2d.raw.shapes.Circle;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
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
	
	public enum BallDrawMode
	{
		Normal,Goal
	}
	private Bitmap []mBallImage;
	private int image_seq;
	private BallDrawMode drawMode = BallDrawMode.Normal;
	
	public Vector m_position = new Vector();
	public Vector m_lastPosition = new Vector();
    public Vector m_velocity = new Vector();
    //private float m_scalarVel = 0f;
    
    /** The size of body D */
	private Body ballBody;
	
    public int width;
    public int height;
	
    public DroidBall(Context context){  
    	mBallImage = new Bitmap[4];
    	mBallImage[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ball_seq1);
    	mBallImage[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ball_seq2);
    	mBallImage[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ball_seq3);
    	mBallImage[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ball_seq4);
    	
    	m_position.setVector(50, 150);
    	width = mBallImage[0].getWidth();
    	height = mBallImage[0].getHeight();
    	
    	ballBody = new Body(new Circle(width/2), 0);
    }
    
    public void setLocation(int x, int y)
    {
    	m_position.setVector(x, y);
    }
    
    public void setDrawMode(BallDrawMode mode)
    {
    	this.drawMode = mode;
    }
    
    public Body getBody()
    {
    	ballBody.setPosition(m_position.X + width/2, m_position.Y + height/2);
    	return ballBody;
    }

	public void drawSprite(Canvas canvas) {
		Paint paint = new Paint();
		
		paint.setColor(Color.YELLOW);
    	paint.setTextSize(10);
//		RectF r1 = getBound();
//		canvas.drawText("Ball: [("+r1.left +", "+r1.top+", "+r1.right+", "+r1.bottom+"), ("+ m_velocity.X +", "+m_velocity.Y+")]",
//						15, 100, paint);
		
    	if(drawMode == BallDrawMode.Normal)
    	{
    		canvas.drawBitmap(mBallImage[image_seq++], m_position.X, m_position.Y, paint);
    		if((int)m_velocity.X == 0 && (int)m_velocity.Y == 0)
    		{
    			image_seq = 0;
    		}
    	}
    	else
    	{
    		canvas.drawBitmap(mBallImage[image_seq++], m_position.X, m_position.Y, paint);

    	}
    	//Check for Index limit...
    	if(image_seq == mBallImage.length)
    	{
    		image_seq = 0;
    	}
		Phys2DUtility.drawCircleBody(canvas, this.getBody(), false);
	}
	public RectF getBound()
	{
		return new RectF(m_position.X, m_position.Y, 
				m_position.X+width, m_position.Y+height);
	}

	public void updatePhysics(Environment env) {
		//save last position...
		m_lastPosition.setVector(m_position.X, m_position.Y);
		
		if(Math.abs(m_velocity.X) > 0)
			m_velocity.X = (int)(Math.abs(m_velocity.X)-2)*(m_velocity.X/Math.abs(m_velocity.X));
		else
			m_velocity.X = 0;
		if(Math.abs(m_velocity.Y) > 0)
			m_velocity.Y = (int)(Math.abs(m_velocity.Y)-2)*(m_velocity.Y/Math.abs(m_velocity.Y));
		else
			m_velocity.Y = 0;
		
		m_position.offset(m_velocity.X, m_velocity.Y);
	}
	
	public void updateAfterCollision(DroidPlayer player)
	{
		this.m_velocity.setVector(player.m_velocity.X * 2 + this.m_velocity.X/2,
					player.m_velocity.Y * 2 + this.m_velocity.Y/2);

		float x = m_lastPosition.X;
		float y = m_lastPosition.Y;
		float lx = m_lastPosition.X;
		float ly = m_lastPosition.Y;
		RectF rect = player.getBound();
		float lpx = rect.left;
        float lpy = rect.top;
		Vector bVel  = new Vector(m_velocity.X, m_velocity.Y);
		Vector pVel  = new Vector(player.m_velocity.X, player.m_velocity.Y);
		Body bBody = new Body(new Circle(width/2), 0);
		Body pBody = new Body(new Circle(width/3), 0);
		
		for(int i=5;i<=100;i+=5)
		{
			bBody.setPosition(x + bVel.X * i/100, y + bVel.Y * i/100);
			//store transform, (like opengl's glPushMatrix())
	        Matrix mat1 = new Matrix();
	        //transform into position
	        float dx = rect.left + pVel.X * i/100;
	        float dy = rect.top + pVel.Y * i/100;
	        mat1.postRotate(player.m_angle,dx+15, dy+15);
	        
	        float []pointsToTrans =  new float[]{dx+ (rect.right-rect.left)/2, dx+ (rect.bottom-rect.top)/2};
	        mat1.mapPoints(pointsToTrans);
	        
			pBody.setPosition(pointsToTrans[0], pointsToTrans[1]);
			
			CircleCircleCollider collider3 = new CircleCircleCollider();
	    	
	    	//Check for player ball collision....
			Contact[] contacts = new Contact[] {new Contact(), new Contact()};
			int count = collider3.collide(contacts, pBody, bBody);
			if(count > 0)
			{
				//this is collision point...
				player.m_position.setVector(lpx, lpy);
				this.m_position.setVector(lx, ly);
				break;
			}
			lx = x + bVel.X * i/100;
			ly = y + bVel.Y * i/100;
			lpx = dx;
			lpy = dy;
		}
		
		//right bottom..
//		if(x > rect.right && y > rect.bottom)
//		{
//			m_position.X = rect.right+1;
//			m_position.Y = rect.bottom+1;
//		}	
//		else if(x > rect.right && y < rect.top)//right top..
//		{
//			m_position.X = rect.right+1;
//			m_position.Y = rect.top - height -1;
//		}
//		else if(x > rect.right && y < rect.bottom && y > rect.top)//right..
//		{
//			m_position.X = rect.right+1;
//			//m_position.Y = rect.bottom+1;
//		}
//		else if(x > rect.left && x < rect.right && y < rect.top)//top..
//		{
//			//m_position.X = rect.right+1;
//			m_position.Y = rect.bottom+1;
//		}
//		
//		else if(x < rect.left && y < rect.top)//Top left
//		{
//			m_position.X = rect.right- width -1;
//			m_position.Y = rect.top- height - 1;
//		}	
//		else if(x < rect.left && y > rect.top && y <rect.bottom)//left..
//		{
//			m_position.X = rect.right- width -1;
//			//m_position.Y = rect.bottom+1;
//		}
//		else if(x < rect.left && y > rect.bottom )//left bottom..
//		{
//			m_position.X = rect.right- width -1;
//			m_position.Y = rect.bottom+1;
//		}
//		else if(x > rect.left && x < rect.right && y < rect.bottom)//bottom..
//		{
//			//m_position.X = rect.right+1;
//			m_position.Y = rect.bottom+1;
//		}
		
		
	}
	public void updateAfterCollision(DroidEnemy player)
	{
		this.m_velocity.setVector(player.m_velocity.X * 2f + this.m_velocity.X/2,
				player.m_velocity.Y * 2f + this.m_velocity.Y/2);
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
