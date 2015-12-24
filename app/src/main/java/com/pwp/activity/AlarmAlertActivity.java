package com.pwp.activity;

import java.io.IOException;

import android.R.integer;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
  
public class AlarmAlertActivity extends Activity { 
	private Dialog builder;
	private MediaPlayer mp;
	private Vibrator vibrator;
	private long longtime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tixing);
        longtime = getIntent().getLongExtra("endtime", 3000)-getIntent().getLongExtra("time", 0);
        System.out.println(longtime+"--------------------longtime");
        mp = new MediaPlayer();
        //控制响铃震动时长
        new Thread(){
  			public void run(){
  				try {
					Thread.sleep(longtime);
					if(mp!=null){
			    		mp.stop();
			    		vibrator.cancel();  
			    	}else if(vibrator!=null){
			    		vibrator.cancel(); 
			    	}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
  			}
  		}.start();
        try {
        	//播放当前默认铃声
			mp.setDataSource(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
			 mp.prepare();
			 mp.setLooping(true);
			 mp.start();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//设置震动
		vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE); 
		long [] pattern = {700,1300,700,1300};   
        vibrator.vibrate(pattern,2);
        String content=getIntent().getExtras().getString("content");
        String title = getIntent().getExtras().getString("title");
        TextView dialog_content=(TextView) findViewById(R.id.dialog_content);
        TextView dialog_title=(TextView) findViewById(R.id.notify_title);
        
		Button dialog_button_cancel=(Button)findViewById(R.id.dialog_button_cancel);
		dialog_content.setText(content);
		dialog_title.setText(title);
  		dialog_button_cancel.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				finish();
			}
		});
    } 
    @Override
    protected void onStop() {
    	ScheduleViewActivity.setAlart(AlarmAlertActivity.this);
    	if(mp!=null){
    		mp.stop();
    		vibrator.cancel();  
    	}else if(vibrator!=null){
    		vibrator.cancel(); 
    	}
    	super.onStop();
    }
    
}
