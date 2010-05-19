package com.bluerender.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;


public class DroidSplash extends View{
    
    private Bitmap mSplashImage;
    
    
    public DroidSplash(Context context) {
        super(context);
        mSplashImage= BitmapFactory.decodeResource(context.getResources(), R.drawable.ball_seq1);
    }
    
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawARGB(255, 255, 255, 255);

        Paint paint = new Paint();

        paint.setAntiAlias(true);

        canvas.drawBitmap(mSplashImage, 0, 0, paint);
    }
}
