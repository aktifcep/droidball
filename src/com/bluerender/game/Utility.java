package com.bluerender.game;

import android.content.Context;
import android.widget.Toast;

public class Utility {

	public static void showShortToast(Context context, int resText)
    {
    	Toast myToast = Toast.makeText(
    			context, 
    			resText, Toast.LENGTH_SHORT);
        myToast.show();
    }
    public static void showShortToast(Context context, CharSequence resText)
    {
    	Toast myToast = Toast.makeText(
    			context, 
    			resText, Toast.LENGTH_SHORT);
        myToast.show();
    }
    public static void showShortToast(Context context, String resText)
    {
    	Toast myToast = Toast.makeText(
    			context, 
    			resText, Toast.LENGTH_SHORT);
        myToast.show();
    }
}
