package com.bluerender.game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.main);
        
        
        addMainClickListener(R.id.newGame);
        addMainClickListener(R.id.resumeGame);
        addMainClickListener(R.id.highScore);
        addMainClickListener(R.id.settings);
        addMainClickListener(R.id.about);
        addMainClickListener(R.id.exitGame);
        addMainClickListener(R.id.newGame);
        
    }
    public void addMainClickListener(int resId)
    {
    	MainClickListener cListener = new MainClickListener();
    	Button btn = (Button) findViewById(resId);
        btn.setOnClickListener(cListener);
    }
    
    
    class MainClickListener implements OnClickListener
    {
    	public void onClick(View v) {
    		Button btnClicked = (Button)v;
        	Utility.showShortToast(MainActivity.this, btnClicked.getText());  
        	
        	if(btnClicked.getId() == R.id.newGame)
        	{
        		Intent myIntent = new Intent(MainActivity.this, com.bluerender.game.NewGameActivity.class);
        		startActivity(myIntent);   
        	}
        }    
    }
}