package com.bluerender.game;

import android.graphics.RectF;

public class Environment {
	private static Environment me = new Environment();
	public static Environment get()
	{
		return me;
	}
	public boolean[] keys = new boolean[10];
	public int canvasWidth;
    public int canvasHeight; 
    public RectF playArea;
    public float timeElapsed;
}
