package com.bluerender.game;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.admob.android.ads.AdManager;
import com.admob.android.ads.AdView;

public class NewGameActivity extends Activity {
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        		
        setContentView(R.layout.newgame);
        
        addMainClickListener(R.id.normalGame);
        addMainClickListener(R.id.timeTrialGame);
        addMainClickListener(R.id.networkGame);
        addMainClickListener(R.id.practiceGame);
        addMainClickListener(R.id.backToMain);
        
        //AdManager.setPublisherId("a14be2b0eeb26aa");
        AdManager.setTestDevices( new String[] {                 
        	     AdManager.TEST_EMULATOR,             // Android emulator
        	     //"E83D20734F72FB3108F104ABC0FFC738",  // My T-Mobile G1 Test Phone
        	     } );  
    }
    
    public void addMainClickListener(int resId)
    { 
    	NewGameClickListener cListener = new NewGameClickListener();
    	Button btn = (Button) findViewById(resId);
        btn.setOnClickListener(cListener);
    }
    
    class NewGameClickListener implements OnClickListener
    {
    	public void onClick(View v) {
    		Button btnClicked = (Button)v;
    		Utility.showShortToast(NewGameActivity.this, btnClicked.getText());  
        	
        	if(btnClicked.getId() == R.id.normalGame)
        	{
        		//Intent myIntent = new Intent(NewGameActivity.this, com.bluerender.game.NewGameActivity.class);
        		//startActivity(myIntent);   
        		setContentView(R.layout.gameview);
        	}
        }    
    }
}
