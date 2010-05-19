package com.bluerender.game;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.RectF;
import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.Contact;
import net.phys2d.raw.collide.BoxCircleCollider;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.Circle;

public class Phys2DPlayer {

	/** The size of body B */
	private Body dynamicBox = new Body(new Box(50,50), 0);
	/** The size of body D */
	private Body dynamicCircle = new Body(new Circle(20), 0);
	
	/** The contacts array used whe nsearching for collisions */
	private Contact[] contacts = new Contact[] {new Contact(), new Contact()};
	
	public Phys2DPlayer()
	{
		dynamicBox.setPosition(150,150);
		//dynamicBox.setRotation(0.4f);
		dynamicCircle.setPosition(165,165);
		dynamicCircle.setRotation(0.3f);
	}
	
	public void update(Environment env)
	{
		ROVector2f vec =  dynamicBox.getPosition();
		if(env.keys[GameControl.KEY_LEFT])
		{				
			dynamicBox.setPosition(vec.getX()-5, vec.getY());
		}
		if(env.keys[GameControl.KEY_RIGHT])
		{
			dynamicBox.setPosition(vec.getX()+5, vec.getY());
		}
		if(env.keys[GameControl.KEY_UP])
		{
			dynamicBox.setPosition(vec.getX(), vec.getY()-5);
		}
		if(env.keys[GameControl.KEY_DOWN])
		{
			dynamicBox.setPosition(vec.getX(), vec.getY()+5);
		}
	}
	
	public void drawSprite(Canvas canvas) {
		Paint paint = new Paint();

		drawCircleBody(canvas, dynamicCircle, false);
		drawBoxBody(canvas, dynamicBox, false);
		
		BoxCircleCollider collider3 = new BoxCircleCollider();
		int count = collider3.collide(contacts, dynamicBox, dynamicCircle);
		boolean hit2 = count > 0;
		
		paint.setColor(Color.MAGENTA);
      	canvas.drawText("Message: ["+ count +"]", 50, 400, paint);
		if(hit2)
		{
			drawContacts(canvas, contacts, count);
		}
		
	}
	
	/**
	 * Draw a box onto the display
	 * 
	 * @param g The graphics context on which to draw
	 * @param body The body to draw
	 * @param fill True if we should draw the body filled, indicates a 
	 * collision.
	 */
	protected void drawBoxBody(Canvas g, Body body, boolean fill) {
		Paint paint = new Paint();
		Box box = (Box) body.getShape();
		Vector2f[] pts = box.getPoints(body.getPosition(),body.getRotation());
		
		Vector2f v1 = pts[0];
		Vector2f v2 = pts[1];
		Vector2f v3 = pts[2];
		Vector2f v4 = pts[3];
		
		Path pol = new Path();
		pol.moveTo((int) v1.x,(int) v1.y);
		pol.lineTo((int) v2.x,(int) v2.y);
		pol.lineTo((int) v3.x,(int) v3.y);
		pol.lineTo((int) v4.x,(int) v4.y);
		pol.lineTo((int) v1.x,(int) v1.y);
		
		if (fill) {
			g.drawPath(pol, paint);
		} else {
			paint.setStyle(Paint.Style.STROKE);
			g.drawPath(pol, paint);
		}
		//g.setColor(Color.gray);
		//drawAABody(g,body,body.getShape().getBounds());
	}
	
	/**
	 * Draw a circle body 
	 * 
	 * @param g The graphics context on which to draw
	 * @param body The body to be drawn
	 * @param fill True if we should draw it filled (indicates a collision)
	 */
	protected void drawCircleBody(Canvas g, Body body, boolean fill) {
		Circle circle = (Circle) body.getShape();
		drawCircleBody(g,body,circle,fill);
		//drawAABody(g,body,body.getShape().getBounds());
	}
	
	/**
	 * Draw a circle body 
	 * 
	 * @param g The graphics context on which to draw
	 * @param body The body to be drawn
	 * @param circle The shape to be drawn
	 * @param fill True if we should draw it filled (indicates a collision)
	 */
	protected void drawCircleBody(Canvas g, Body body, Circle circle, boolean fill) {
		Paint paint = new Paint();
		float x = body.getPosition().getX();
		float y = body.getPosition().getY();
		float r = circle.getRadius();
		float rot = body.getRotation();
		float xo = (float) (Math.cos(rot) * r);
		float yo = (float) (Math.sin(rot) * r);
		
		
		if (fill) {
			g.drawOval(new RectF((x-r), (y-r), (r*2)-(x-r), (r*2)-(y-r)), paint);
		} else {
			paint.setStyle(Paint.Style.STROKE);
			//g.drawOval(new RectF((x-r), (y-r), (x-r)-(r*2), (y-r)-(r*2)), paint);
			g.drawCircle(x, y, r, paint);
		}
		g.drawLine((int) x,(int) y,(int) (x+xo),(int) (y+yo), paint);
	}
	
	/**
	 * Draw the contact points onto the display
	 * 
	 * @param g The graphics context on which to draw
	 * @param contacts The contacts to be drawn
	 * @param count The number of contacts to be drawn
	 */
	protected void drawContacts(Canvas g, Contact[] contacts, int count) {
		Paint paint = new Paint();
		for (int i=0;i<count;i++) {
			paint.setColor(Color.RED);
			int x = (int) contacts[i].getPosition().getX();
			int y = (int) contacts[i].getPosition().getY();
			g.drawOval(new RectF(x-3,y-3, x-3+6, y-3+6), paint);
			
			float dx = contacts[i].getNormal().getX();
			float dy = contacts[i].getNormal().getY();
			paint.setColor(Color.GREEN);
			g.drawLine(x,y,(int) (x+(dx*20)),(int) (y+(dy*20)), paint);
			
			float sep = contacts[i].getSeparation();
			paint.setColor(Color.YELLOW);
			g.drawLine(x,y,(int) (x+(dx*sep)),(int) (y+(dy*sep)), paint);
		}
	}
}
