package com.bluerender.game;

import net.phys2d.raw.Body;
import net.phys2d.raw.shapes.Circle;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Region.Op;

public class DroidEnemy {
	private Context mContext;
	private Bitmap mPlayerImage;
	
	/** The size of body D */
	private Body playerBody;
	
	private Vector m_position = new Vector();
	private Vector m_transPos = new Vector();
    public Vector m_velocity = new Vector();
    private float m_scalarVel = 0f;
    //angular properties
    private float m_angle;
    private float m_angularVelocity = 15;
    private float m_torque;
    //private float m_inertia;
    private int radius = 15;
    
    private int width;
    private int height;
        
    public DroidEnemy(Context context)
	{
    	this.mContext = context;
		m_position.setVector(50, 50);

		mPlayerImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.droid_grey);
		width = mPlayerImage.getWidth();
    	height = mPlayerImage.getHeight();
    	
    	playerBody = new Body(new Circle(width/3), 0);
	}
    
    public void setLocation(int x, int y)
    {
    	m_position.setVector(x, y);
    }
    
    public void setRotation(int angle)
    {
    	this.m_angle = angle;
    }
    
    public Body getBody()
    {
    	playerBody.setPosition(m_transPos.X , m_transPos.Y );
    	//playerBody.
    	return playerBody;
    }

	public void drawSprite(Canvas canvas) {
		
		Paint paint = new Paint();
		paint.setColor(Color.GRAY);
		//canvas.drawText("Plate: ["+ m_position +", "+ m_angle +", "+ m_torque +"]", 20, 20, paint);
		
		//store transform, (like opengl's glPushMatrix())
        Matrix mat1 = canvas.getMatrix();
        //transform into position
        
        canvas.rotate(m_angle, m_position.X+radius, m_position.Y+radius);
        
        float []pointsToTrans =  new float[]{m_position.X+ width/2, m_position.Y+ height/2};
        canvas.getMatrix().mapPoints(pointsToTrans);
        m_transPos.setVector(pointsToTrans[0], pointsToTrans[1]);
		
        //Draw Player Here...        
        canvas.drawBitmap(mPlayerImage, m_position.X, m_position.Y, paint);
                
		paint.setColor(Color.RED);
		paint.setStyle(Paint.Style.STROKE);
		//restore transform
		canvas.setMatrix(mat1);
		
		//Draw Collision Rect..
		//paint.setColor(Color.DKGRAY);
		//canvas.drawPath(colRect, paint);
		//Phys2DUtility.drawCircleBody(canvas, this.getBody(), false);
	}
	
	public RectF getBound()
	{
		return new RectF(m_position.X, m_position.Y, 
				m_position.X+width, m_position.Y+height);
	}
	
	public void updatePhysics(Environment env, DroidBall ball) {
		float old_scalarVel = m_scalarVel;
		
//		if(env.keys[GameControl.KEY_LEFT])
//		{			
//			m_angle -= m_angularVelocity;
//		}
//		if(env.keys[GameControl.KEY_RIGHT])
//		{
//			m_angle += m_angularVelocity;
//		}
//		if(m_angle >= 360)
//		{
//			m_angle -= 360;
//		}
//		else if(m_angle < 0)
//		{
//			m_angle = 360 + m_angle;
//		}
//		if( env.keys[GameControl.KEY_UP])
//		{
//			if(m_scalarVel < 20)
//			{
//				//m_velocity.setVector(m_velocity.X, m_velocity.Y-2);
//				m_scalarVel += 5;
//			
//			}
//		}
//		else
//		{
//			if(m_scalarVel > 0)
//			{
//				//m_velocity.setVector(m_velocity.X, m_velocity.Y+1);
//				m_scalarVel -= 2;
//			}
//			else
//			{
//				//m_velocity.setVector(m_velocity.X, 0);
//				m_scalarVel = 0;
//			}
//		}
//		if( env.keys[GameControl.KEY_DOWN] )
//		{
//			if(m_velocity.Y > 0)
//			{
//				//m_velocity.setVector(m_velocity.X, m_velocity.Y+2);
//				m_scalarVel -= 5;
//			}
//		}
		
		if( !updatePosition(env))
		{
			m_scalarVel = old_scalarVel;
		}
	}
	
	public void ApplyBreak()
	{
		if(m_scalarVel > 0)
		{
			//m_velocity.setVector(m_velocity.X, m_velocity.Y+1);
			m_scalarVel -= 2;
			if(m_scalarVel < 0)m_scalarVel=0;
		}
	}
	
	
	private boolean updatePosition(Environment env)
	{
		//float worldSpaceVelocity = m_scalarVel;
		float wsVelocityX = m_scalarVel;
		float wsVelocityY = m_scalarVel;
		float l_angle = m_angle;
		if(m_angle < 0)l_angle = 360 - m_angle;
		
		int angle90 =  (int)Math.abs(l_angle)%90;
		//if Mod 90 of angle is zero than make it 90...
		if(Math.abs(l_angle) > 0)			
			angle90 = angle90==0?90:angle90;
		
		m_torque = angle90/90f ;
		
		if( m_angle >= 0 && m_angle <= 90 )
		{			
			wsVelocityX = wsVelocityX * m_torque;
			wsVelocityY = -wsVelocityY * (1-m_torque);
		}
		else if( m_angle > 90 && m_angle <= 180 )
		{
			wsVelocityX = wsVelocityX * (1-m_torque);
			wsVelocityY = wsVelocityY * m_torque;
		}
		else if( m_angle > 180 && m_angle <= 270 )
		{
			wsVelocityX = -wsVelocityX * m_torque;
			wsVelocityY = wsVelocityY * (1-m_torque);
		}
		else if( m_angle > 270 && m_angle <= 360 )
		{
			wsVelocityX = -wsVelocityX * (1-m_torque);
			wsVelocityY = -wsVelocityY * m_torque;
		}
		
		//Check for walls collision...
		if( (m_position.X + wsVelocityX) < env.playArea.left )
		{
			m_position.setVector(env.playArea.left, m_position.Y + wsVelocityY);
		}
		if( (m_position.Y + wsVelocityY) < env.playArea.top )
		{
			m_position.setVector(m_position.X + wsVelocityX, env.playArea.top);
		}
		if((m_position.X+ width + wsVelocityX) > env.playArea.right)
		{
			m_position.setVector(env.playArea.right - width, m_position.Y + wsVelocityY);
		}
		if((m_position.Y+ height + wsVelocityY) > env.playArea. bottom )
		{
			m_position.setVector(m_position.X + wsVelocityX, env.playArea. bottom - height);
		}
		else
		{
			m_velocity.setVector(wsVelocityX, wsVelocityY);
			//Now update the position...
			m_position.setVector(m_position.X + wsVelocityX, m_position.Y + wsVelocityY);

			//m_velocity.setVector(-5, 0);
		}
		
		return true;
	}

	public boolean collision(RectF rect)
	{		
		return false;
	}
	
}
