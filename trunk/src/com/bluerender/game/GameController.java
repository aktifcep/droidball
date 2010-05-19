package com.bluerender.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GameController extends View {
	Context mContext;
	String mText;
	
	ControllerButton left;
	ControllerButton leftUp;
	ControllerButton up;
	ControllerButton upRight;
	ControllerButton right;
	Bitmap dpad_center;
	
	public GameController(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        mContext = context;
        //setFocusable(true);        
        setupButton();
        
        dpad_center = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.dpad_center);
	}
	
	private void setupButton()
	{
		left = new ControllerButton(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.left));
		leftUp = new ControllerButton(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.left_up));
		up = new ControllerButton(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.up));
		upRight = new ControllerButton(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.up_right));
		right = new ControllerButton(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.right));
		
		left.setPosition(25, 40);
		leftUp.setPosition(25, 0);
		up.setPosition(65, 0);
		upRight.setPosition(105, 0);
		right.setPosition(105, 40);
	}
	
	private void updateControlState(boolean []keys)
	{
		keys[GameControl.KEY_LEFT] = left.pressed | leftUp.pressed;
		
		//keys[GameControl.KEY_LEFT] = leftUp.pressed;
		//keys[GameControl.KEY_UP] = leftUp.pressed;
		
		keys[GameControl.KEY_UP] = up.pressed | leftUp.pressed | upRight.pressed;
		
		//keys[GameControl.KEY_UP] = upRight.pressed;
		//keys[GameControl.KEY_RIGHT] = upRight.pressed;
		
		keys[GameControl.KEY_RIGHT] = right.pressed | upRight.pressed;
		
		//Vibrator vib = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
		//vib.vibrate(500);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean result = false;
		float x = event.getRawX();
		float y = event.getRawY() - this.getTop(); 
		
		//Update Button state..
		left.updateState(x, y, event.getAction());
		leftUp.updateState(x, y, event.getAction());
		up.updateState(x, y, event.getAction());
		upRight.updateState(x, y, event.getAction());
		right.updateState(x, y, event.getAction());
		
    	if (event.getAction() == MotionEvent.ACTION_DOWN) {
    		String msg = "[DOWN("+x+","+y+")]";
    		mText = msg;
    		result = true;
    		
    	}
    	if (event.getAction() == MotionEvent.ACTION_MOVE) {
    		String msg = "[MOVE("+ x +","+ y +")]";
    		mText = msg;
    		
    		result = true;
    	}
    	if (event.getAction() == MotionEvent.ACTION_UP) {
    		String msg = "[UP("+ x +","+ y +")]";
    		mText = msg;
    		
    		result = true;
    	}
    	updateControlState(Environment.get().keys);
    	
    	this.invalidate();

    	return result;
    }
	
	@Override
	protected void onDraw (Canvas canvas)
	{
		Paint paint = new Paint();
    	// empty canvas
    	canvas.drawARGB(255, 201, 194, 180);
    	
    	//Draw the controls here...
    	left.onDraw(canvas);
		leftUp.onDraw(canvas);
		up.onDraw(canvas);
		upRight.onDraw(canvas);
		right.onDraw(canvas);
		
		paint.setAlpha(100);
		canvas.drawBitmap(dpad_center, 65, 40, paint);
    	//Draw Ground Border...
    	paint.setColor(Color.GRAY);
    	paint.setStyle(Paint.Style.STROKE);
    	//paint.setStrokeWidth(3);
    	//canvas.drawRect(0, 0, canvas.getWidth()-1, this.getHeight()-2, paint);
    	canvas.drawText("Message: ["+ mText +"]", 50, 10, paint);
	}
	
	//Controller Button Class that have logic of button...
	class ControllerButton{
		
		Bitmap image;
		
		float x, y, width = 40, height = 40;
		RectF rect;
		boolean pressed;
		
		public ControllerButton(Bitmap image)
		{   
			this.image = image;
	    	rect = new RectF(x, y, x+width, y+height);
		}
		
		public void setPosition(float x, float y)
		{
			this.x = x;
			this.y = y;
			rect = new RectF(x, y, x+width, y+height);
		}
		
		public void updateState(float x, float y, int action)
		{
    		boolean contains = rect.contains((int)x, (int)y);
    		
    		if(action == MotionEvent.ACTION_DOWN)
    		{
    			pressed = contains;
    		}
    		else if(action == MotionEvent.ACTION_MOVE)
    		{
    			pressed = contains;
    		}
    		else if(action == MotionEvent.ACTION_UP)
    		{
    			pressed = false;
    		}
		}
		
		public void onDraw(Canvas canvas)
		{
			Paint paint = new Paint();
   	
	    	//Draw the controls here...
			if(pressed)
				paint.setAlpha(50);
			else
				paint.setAlpha(100);
			//Draw the controller rectangle...
			//canvas.drawRect(rect, paint);	
			canvas.drawBitmap(image, x, y, paint);
		}
	}

}
