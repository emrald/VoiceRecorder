package com.clarusapps.voicerecorder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogRecord;


import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;
import android.os.Handler;

public class RecordPlay extends Activity {

	ImageButton imbtnprevious, imbtnplay, imbtnnext;
	TextView recordname,recordtime,filesize,starttime,endtime;
	SeekBar sek;
	File file;
	Intent i;
	String filename, filetime, recordsize,recordpath;
	MediaController mediaController; 
	MediaPlayer player = new MediaPlayer();

	private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);

		recordname = (TextView) findViewById(R.id.player_tv_recordname);
		recordtime = (TextView) findViewById(R.id.player_tv_recordtime);
		filesize = (TextView) findViewById(R.id.player_tv_filesize);
		starttime=(TextView)findViewById(R.id.player_tv_starttime);
		endtime=(TextView)findViewById(R.id.player_tv_endtime);

	//	imbtnprevious = (ImageButton) findViewById(R.id.player_imbtnplay_previous);
	//	imbtnplay = (ImageButton) findViewById(R.id.player_imbtn_play);
	//	imbtnnext = (ImageButton) findViewById(R.id.player_imbtnplay_next);
	//	sek = (SeekBar) findViewById(R.id.player_seekBar);

		String filepath = Environment.getExternalStorageDirectory().getPath();

		file = new File(filepath, AUDIO_RECORDER_FOLDER);

		i = getIntent();
		filename = i.getStringExtra("filename");
		filetime = i.getStringExtra("recordtime");
		recordsize = i.getStringExtra("filesize");

		recordname.setText(filename);
		recordtime.setText(filetime);
		filesize.setText(recordsize);
		Log.e("file", filename);

		recordpath = file.getAbsolutePath() + "/" + filename;
		Log.e("recordpath", recordpath);
		
	

		//mHandler.post(updateUI);
		imbtnplay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*try {
					Uri myUri1 = Uri.parse(recordpath);
					player.setDataSource(getApplicationContext(), myUri1);
					// player.setDataSource(filename);

					player.prepareAsync();
					player.setOnPreparedListener(new OnPreparedListener() {

						public void onPrepared(MediaPlayer mp) {
							int duration = player.getDuration();
							// player.requestFocus();
							mp.start();
							//sek.setProgress(player.getCurrentPosition());
						}
					});

					// player.prepare();
					// player.start();
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
				 */				
				

			}
		});

		/*		player.setOnCompletionListener(new OnCompletionListener() {           
			public void onCompletion(MediaPlayer mp) {          
				Log.i("Completion Listener","Song Complete");
				mp.stop();
				//   mp.reset();

			}
		});
		ScheduledExecutorService myScheduledExecutorService = Executors.newScheduledThreadPool(1);

		myScheduledExecutorService.scheduleWithFixedDelay(
				new Runnable(){
					@Override
					public void run() {
						monitorHandler.sendMessage(monitorHandler.obtainMessage());
					}}, 
					200, //initialDelay
					200, //delay
					TimeUnit.MILLISECONDS);


	}

	Handler monitorHandler = new Handler(){

		public void handleMessage(Message msg) {
			mediaPlayerMonitor();
		}

	};

	private void mediaPlayerMonitor(){

		sek.setVisibility(View.VISIBLE);

		int mediaDuration = player.getDuration();
		int mediaPosition = player.getCurrentPosition();
		sek.setMax(mediaDuration);
		Log.e("dur",mediaDuration+"");
		sek.setProgress(mediaPosition);
		starttime.setText(String.valueOf((float)mediaPosition/1000));
		endtime.setText(String.valueOf((float)mediaDuration/1000));

	}*/
	}

}





