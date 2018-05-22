package com.clarusapps.voicerecorder;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MusicPlayerActivity extends Activity{

	private ImageButton btnPlay;
	private ImageButton btnForward;
	private ImageButton btnBackward;
	private ImageButton btnNext;
	private ImageButton btnPrevious;
	private ImageButton btnplaybegin;
	private Button btnback;
	private ImageButton btnRepeat;
	private SeekBar songProgressBar;
	private TextView songTitleLabel;
	private TextView songCurrentDurationLabel;
	private TextView songTotalDurationLabel;
	private TextView tvlessontitle;
	// Media Player
	private  MediaPlayer mp;
	// Handler to update UI timer, progress bar etc,.
	private Handler mHandler = new Handler();
//	private SongsManager songManager;
	private Utilities utils;
	private int seekForwardTime = 5000; // 5000 milliseconds
	private int seekBackwardTime = 5000; // 5000 milliseconds
	private int currentSongIndex = 0; 
	private boolean isShuffle = false;
	private boolean isRepeat = false;
	private static final int INTIAL_KB_BUFFER =  96*10/8;
	private long mediaLengthInKb, mediaLengthInSeconds;
	private int totalKbRead = 0;
	private File downloadingMediaFile; 
	private TextView tvrecordtime,tvrecordsize,tvrecordname;
	HttpURLConnection c;
	String url;
	String data;
	StringBuffer sb;
	File tempFile;
	//FileInputStream inputStream;
	//InputStream  stream1;
	//InputStream stream ;
	String FILENAME ="hello.txt";
	FileInputStream fis; 
	StringBuffer fileContent = new StringBuffer("");
	ByteArrayOutputStream buffer;
	//byte[] bytes= null;
	byte[] resultBuff;
	private static final String MP3_FOLDER = "MP3 Files";
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	ProgressDialog pd;
	public static boolean FLAG_MP_PAUSED = false;
	String filepath = Environment.getExternalStorageDirectory().getPath();
	File file;
	Uri uri;
	Intent i;
	AlertDialog alert;
	String recname,rectime,recsize,Recordingname;
	Button btnlistview,btnsetting,btnedit,btndelete,btnshare;
	boolean FLAG_DUPNAME=false;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);


		// All player buttons
		btnPlay = (ImageButton) findViewById(R.id.player_imbtn_play);
		btnForward = (ImageButton) findViewById(R.id.player_imbtnplay_next);
		btnBackward = (ImageButton) findViewById(R.id.player_imbtnplay_previous);
		btnlistview=(Button)findViewById(R.id.player_btn_listview);
		btnsetting=(Button)findViewById(R.id.player_btn_settings);
		btnedit=(Button)findViewById(R.id.player_edit);
		btndelete=(Button)findViewById(R.id.player_delete);
		btnshare=(Button)findViewById(R.id.player_share);
		songProgressBar = (SeekBar) findViewById(R.id.player_seekBar);

		songCurrentDurationLabel = (TextView) findViewById(R.id.player_tv_starttime);
		songTotalDurationLabel = (TextView) findViewById(R.id.player_tv_endtime);
		songCurrentDurationLabel = (TextView) findViewById(R.id.player_tv_starttime);
		songTotalDurationLabel = (TextView) findViewById(R.id.player_tv_endtime);
		tvrecordname=(TextView)findViewById(R.id.player_tv_recordname);
		tvrecordtime=(TextView)findViewById(R.id.player_tv_recordtime);
		tvrecordsize=(TextView)findViewById(R.id.player_tv_filesize);
		// Mediaplayer
		
		i=getIntent();
		recname=i.getExtras().getString("filename");
		rectime=i.getExtras().getString("recordtime");
		recsize=i.getExtras().getString("filesize");
		
		tvrecordname.setText(recname);
		tvrecordtime.setText(rectime);
		tvrecordsize.setText(recsize);
		//new Authentication().execute(EpisodeActivity.listurl);
		
		mp = new MediaPlayer();
		mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
		utils = new Utilities();

		// Listeners
		songProgressBar.setOnSeekBarChangeListener(seekbarListner); // Important
		mp.setOnCompletionListener(mpCompletionListner);
		
		playSong();
	
		btnedit.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FLAG_DUPNAME=false;
				AlertDialog.Builder builder =new AlertDialog.Builder(MusicPlayerActivity.this);
				builder.setMessage("Edit Recoding Name");
				final EditText etrecordname =new EditText(MusicPlayerActivity.this);
				builder.setView(etrecordname);

				builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						Recordingname=etrecordname.getText().toString();
						for(int rec=0;rec<RecordList.recordarr.size();rec++)
						{
							if(RecordList.recordarr.get(rec).recordname.equalsIgnoreCase(Recordingname))
							{
								Toast.makeText(MusicPlayerActivity.this,"Record Name already exist",Toast.LENGTH_SHORT).show();
								Log.e(Recordingname,"duplicate");
								FLAG_DUPNAME=true;
							}
						}
						if(FLAG_DUPNAME==false)
						{
							File from = new File(RecordList.recordpath + recname);
							Log.e("from",RecordList.recordpath + recname);
							Log.e("to", RecordList.recordpath +Recordingname);
							File to=new File(RecordList.recordpath +Recordingname);
							from.renameTo(to);					
							tvrecordname.setText(Recordingname);
						}
					}
				});
			
				builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
				alert=builder.create();
				alert.show();
			
			}
		});		
		
		btndelete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder =new AlertDialog.Builder(MusicPlayerActivity.this);
				builder.setMessage("Are you sure you want to delete record?");
				
				builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						File file = new File(RecordList.recordpath + recname);
						boolean deleted = file.delete();
						Log.e("deleted",recname);
						finish();
					}
				});
			
				builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
				alert=builder.create();
				alert.show();
				
			
			}
		});
			
		btnshare.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				File recording = new File(RecordList.recordpath + recname);
		
				Intent intent=new Intent(android.content.Intent.ACTION_SEND);
				intent.setType("audio/*");
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				intent.putExtra(Intent.EXTRA_STREAM,Uri.parse("file:///"+recording));
				startActivity(Intent.createChooser(intent, "Share Sound File"));
			}
		});
		btnlistview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				finish();
			}
		});
		
		btnsetting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			
				Intent i=new Intent(MusicPlayerActivity.this,Setting.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				
			}
		});
		
		btnPlay.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(mp.isPlaying()){
					mp.pause();
					FLAG_MP_PAUSED = true;
					btnPlay.setImageResource(R.drawable.play);

				}else{
					// Resume song
					if(mp!=null){
						/*if(FLAG_MP_PAUSED){
							
						}else{
							playSong();
						}*/
						mp.start();
						btnPlay.setImageResource(R.drawable.pausebtn);
					}

				}

			}
		});

		

		btnForward.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// get current song position				
				int currentPosition = mp.getCurrentPosition();
				// check if seekForward time is lesser than song duration
				if(currentPosition + seekForwardTime <= mp.getDuration()){
					// forward song
					mp.seekTo(currentPosition + seekForwardTime);
				}else{
					// forward to end position
					mp.seekTo(mp.getDuration());
				}
			}
		});


		btnBackward.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// get current song position				
				int currentPosition = mp.getCurrentPosition();
				// check if seekBackward time is greater than 0 sec
				if(currentPosition - seekBackwardTime >= 0){
					// forward song
					mp.seekTo(currentPosition - seekBackwardTime);
				}else{
					// backward to starting position
					mp.seekTo(0);
				}

			}
		});


		
	}	
	OnCompletionListener mpCompletionListner = new OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer mp) {
			if(isRepeat){
				// repeat is on play same song again
				playSong();
			}
			else
				btnPlay.setImageResource(R.drawable.play);

		}
	};
	OnSeekBarChangeListener seekbarListner = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			mHandler.removeCallbacks(mUpdateTimeTask);
			int totalDuration = mp.getDuration();
			int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

			// forward or backward to certain seconds
			mp.seekTo(currentPosition);

			// update timer progress again
			updateProgressBar();

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			mHandler.removeCallbacks(mUpdateTimeTask);

		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub

		}
	};



	public void  playSong(){
		try {
			// create temp file that will hold byte array

			mp.reset();
			
		
			mp.setDataSource(RecordList.recordpath+RecordList.filename); // Throws!

			mp.prepare();
			mp.start();
			btnPlay.setImageResource(R.drawable.pausebtn);

			// set Progress bar values
			songProgressBar.setProgress(0);
			songProgressBar.setMax(100);
			updateProgressBar();
		} catch (IOException ex) {
			String s = ex.toString();
			ex.printStackTrace();
		}
	}

	/**
	 * Update timer on seekbar
	 * */
	public void updateProgressBar() {
		mHandler.postDelayed(mUpdateTimeTask, 100);        
	}	

	/**
	 * Background Runnable thread
	 * */
	private Runnable mUpdateTimeTask = new Runnable() {

		public void run() {
			long totalDuration = 0;
			try {
				totalDuration = mp.getDuration();
			} catch (IllegalStateException e) {
				FLAG_MP_PAUSED=false;
				finish();
			}
			long currentDuration = 0;
			try {
				currentDuration = mp.getCurrentPosition();
			} catch (IllegalStateException e) {
				FLAG_MP_PAUSED=false;
				finish();
			}
			// Displaying Total Duration time
			songTotalDurationLabel.setText(
					"" + utils.milliSecondsToTimer(totalDuration));
			// Displaying time completed playing
			songCurrentDurationLabel.setText(
					"" + utils.milliSecondsToTimer(currentDuration));

			// Updating progress bar
			int progress = (int) (utils.getProgressPercentage(currentDuration,
					totalDuration));
			// Log.d("Progress", ""+progress);
			songProgressBar.setProgress(progress);
			// Running this thread after 100 milliseconds
			mHandler.postDelayed(this, 100);
			// Log.d("AndroidBuildingMusicPlayerActivity","Runable  progressbar");
		}
	};

	@Override
	public void onDestroy(){
		super.onDestroy();
		mp.release();
	}

	
}


