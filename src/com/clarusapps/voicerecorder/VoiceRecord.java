package com.clarusapps.voicerecorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.os.SystemClock;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class VoiceRecord extends Activity  {

	//private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
	//	private MediaRecorder recorder = null;
	public static final String MY_BANNER_UNIT_ID ="a151d5014d5abc2";
	private int currentFormat = 0;
	CountDownTimer t;
	static TextView timer,tvstorage;
	boolean isPressed=false;
	//	boolean isRecording=false;
	boolean ispauseflag=false,started;
	ImageButton btnrecord,btndone;
	private long startTime = 0L; 
	private Handler myHandler = new Handler(); 
	RecordAudio recordTask;
	long timeInMillies = 0L; 
	long timeSwap = 0L; 
	long finalTime = 0L;
	//private Thread recordingThread = null;
	private static final int RECORDER_BPP = 16;
	private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
	private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
	private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
	private static final int RECORDER_SAMPLERATE = 8000;
	private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
	private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	private RealDoubleFFT transformer;
	//    private static final int AUDIO_SAMPLE_FREQ = 44100;
	//    private static final int AUDIO_BUFFER_SIZE = 200000; 

	private AudioRecord recorder;
	private int bufferSize = 0;
	private Thread recordingThread = null;
	private boolean isRecording = false;
	Button btnrecordlist, btnsetting;
	SharedPreferences pref;

	//   ImageButton start_record,pause_rec,stop_rec,play_rec,resume_rec;
	Handler handler;
	int read = 0,blockSize = 10;
	byte data[];
	int recordnm;
	Intent i;
	String text;
	ImageView imageView;
	Bitmap bitmap;
	Canvas canvas;
	Paint paint;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.voicerecord);

		timer=(TextView)findViewById(R.id.voicerecord_tv_time);
		tvstorage=(TextView)findViewById(R.id.voicerecord_tv_storagecapacity);
		btnrecord=(ImageButton)findViewById(R.id.voicerecord_btn_record);
		btndone=(ImageButton)findViewById(R.id.voicerecord_btn_done);
		btnrecordlist = (Button) findViewById(R.id.voicerecord_btn_listview);
		btnsetting = (Button) findViewById(R.id.voicerecord_btn_settings);

		transformer = new RealDoubleFFT(blockSize);

		imageView = (ImageView) this.findViewById(R.id.voicerecord_im_equilizer);
		bitmap = Bitmap.createBitmap((int)256,(int)100,Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bitmap);

		paint = new Paint();
		paint.setColor(Color.argb(200, 181, 111, 233));
		paint.setPathEffect(new DashPathEffect(new float[] {5,5}, 0));
		paint.setStrokeWidth(10f);
		paint.setAntiAlias(true);
		imageView.setImageBitmap(bitmap);
		
	
    	
		btnrecordlist.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				i = new Intent(VoiceRecord.this, RecordList.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}
		});


		btnsetting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				i=new Intent(VoiceRecord.this,Setting.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}
		});

		bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING);

	
		btnrecord.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(isPressed){

					btnrecord.setImageResource(R.drawable.recordbtn);
					stopRecording(false);
					started = false;
					recordTask.cancel(true);
					
					//startRecording(false);
					Toast.makeText(VoiceRecord.this, "Pause",Toast.LENGTH_SHORT).show();
					timeSwap += timeInMillies; 
					myHandler.removeCallbacks(updateTimerMethod);
					ispauseflag=true;
				}
				else{

					btnrecord.setImageResource(R.drawable.pause);

					if(ispauseflag)
					{
				 		//stopRecording(false);
						startRecording(true);
						ispauseflag=false;
						
				//		recordTask.cancel(false);
					}

					else
					{
						//Toast.makeText(VoiceRecord.this, "Recording",Toast.LENGTH_SHORT).show();
						startRecording(true);
						ispauseflag=false;
					//	recordTask.cancel(false);
					}

					started = true; 	
					recordTask=new RecordAudio();
					recordTask.execute();
					startTime = SystemClock.uptimeMillis();
					myHandler.postDelayed(updateTimerMethod, 0);
					Toast.makeText(VoiceRecord.this, "Recording",Toast.LENGTH_SHORT).show();

				}
				isPressed=!isPressed;

			}
		});

		btndone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(VoiceRecord.this, "Done Recording",Toast.LENGTH_SHORT).show();
			
				stopRecording(true);
				started = true;
				recordTask.cancel(true);
				
			    startTime=0;
				timeSwap=0;
				text="<font color=#FFFFFF>00 : </font><font color=#ff7d00>"+String.format("%02d", 0)+"</font>";
				timer.setText(Html.fromHtml(text));
				//timer.setText("" + 00 + ":" + String.format("%02d", 0));
				myHandler.removeCallbacks(updateTimerMethod);
				isPressed=false;
				ispauseflag=true;


			}
		});
		

	}	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		btnrecord.setImageResource(R.drawable.recordbtn);
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		long bytesAvailable = (long)stat.getBlockSize() *(long)stat.getBlockCount();
		long megAvailable = bytesAvailable / 1048576;
		megAvailable=1*megAvailable/2;
		megAvailable=megAvailable/60;
		tvstorage.setText("Storage capacity remaining: "+megAvailable + " hours");
	
		ActivityManager activityManager =  (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		activityManager.getMemoryInfo(mi);
		Log.e("memory free", "" + mi.availMem);
		
		Log.e("Thread name.......", "OnRecordPositionUpdateListener on thread: " + Thread.currentThread().getName());

		super.onResume();


	}
	
		 
		private String getFilename(){
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath,AUDIO_RECORDER_FOLDER);

		pref=getSharedPreferences("filedata", 0);
		recordnm=pref.getInt("filename",0);
		recordnm++;

		SharedPreferences.Editor editor=pref.edit();
		editor.putInt("filename",recordnm);
		editor.commit();

		if(!file.exists()){
			file.mkdirs();
		}

		//   return (file.getAbsolutePath() + "/" + "Ravindra" + AUDIO_RECORDER_FILE_EXT_WAV);
		return (file.getAbsolutePath() + "/" + Setting.recordnameprefix + recordnm + ".mp4");
		}
		private String getTempFilename(){
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath,AUDIO_RECORDER_FOLDER);

		if(!file.exists()){
			file.mkdirs();
		}

		File tempFile = new File(filepath,AUDIO_RECORDER_TEMP_FILE);

		if(tempFile.exists())
			tempFile.delete();

		return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
		}

		private void enableButton(int id, boolean isEnable) {
		((ImageButton) findViewById(id)).setEnabled(isEnable);
	}

	private void enableButtons(boolean isRecording) {
		enableButton(R.id.voicerecord_btn_record, !isRecording);
		enableButton(R.id.voicerecord_btn_done, isRecording);
	}


	private void startRecording(final boolean b){

		recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,RECORDER_SAMPLERATE, RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING, bufferSize);

		recorder.startRecording();
 	
		isRecording = true;

		recordingThread = new Thread(new Runnable() {

			@Override
			public void run() {
				writeAudioDataToFile(b);
			}
		},"AudioRecorder Thread");

		recordingThread.start();

	} 
	private void stopRecording(boolean b){
		if(recorder != null){
			isRecording = false;
			
			recorder.stop();
			
			recorder.release();
			
			recorder = null;
			recordingThread = null;
		}

		if(b == true){
			copyWaveFile(getTempFilename(),getFilename());
			deleteTempFile();
		}

	}

	private void deleteTempFile() {
		File file = new File(getTempFilename());

		file.delete();
	}

	private void writeAudioDataToFile(boolean b){
		data = new byte[bufferSize];
		String filename = getTempFilename();
		FileOutputStream os = null;

		try {
			os = new FileOutputStream(filename,b);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}



		if(os != null){
			while(isRecording){
				read = recorder.read(data, 0, bufferSize);

				/* int average = 0;
		                for (short s : data) {
		                	average += Math.abs(s); 
		                	 Log.e("average",average+"");
		                }
				 */
				
				if(AudioRecord.ERROR_INVALID_OPERATION != read){
					try {
						os.write(data);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}  

	private void copyWaveFile(String inFilename,String outFilename){
		FileInputStream in = null;
		FileOutputStream out = null;
		long totalAudioLen = 0;
		long totalDataLen = totalAudioLen + 44;
		long longSampleRate = RECORDER_SAMPLERATE;
		int channels = 2;
		long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels/8;

		data = new byte[bufferSize];

		try {
			in = new FileInputStream(inFilename);
			out = new FileOutputStream(outFilename);
			totalAudioLen = in.getChannel().size();
			totalDataLen = totalAudioLen + 44;

			//AppLog.logString("File size: " + totalDataLen);

			WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
					longSampleRate, channels, byteRate);

			while(in.read(data) != -1){
				out.write(data);
			}

			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private void WriteWaveFileHeader(
			FileOutputStream out, long totalAudioLen,
			long totalDataLen, long longSampleRate, int channels,
			long byteRate) throws IOException {

		byte[] header = new byte[44];

		header[0] = 'R';  // RIFF/WAVE header
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f';  // 'fmt ' chunk
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = 1;  // format = 1
		header[21] = 0;
		header[22] = (byte) channels;
		header[23] = 0;
		header[24] = (byte) (longSampleRate & 0xff);
		header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = (byte) (2 * 16 / 8);  // block align
		header[33] = 0;
		header[34] = RECORDER_BPP;  // bits per sample
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLen & 0xff);
		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

		out.write(header, 0, 44);
	}

		
	private Runnable updateTimerMethod = new Runnable() {

		public void run() { 

			timeInMillies = SystemClock.uptimeMillis() - startTime; 
			finalTime = timeSwap + timeInMillies;

			int seconds = (int) (finalTime / 1000); 
			int minutes = seconds / 60; 
			seconds = seconds % 60; 
			int milliseconds = (int) (finalTime % 1000);

			//timer.setText("" + minutes + ":" + String.format("%02d", seconds));
			text="<font color=#FFFFFF>"+String.format("%02d", minutes)+ ":"+"</font><font color=#ff7d00>"+String.format("%02d", seconds)+"</font>";
			timer.setText(Html.fromHtml(text));
			myHandler.postDelayed(this, 0); 
			
		}

	};
	
	private class RecordAudio extends AsyncTask<Void, double[], Void>{
		@Override
        protected void onPreExecute() {
            super.onPreExecute();

            recorder.startRecording();
        }
		@Override
		protected Void doInBackground(Void... params) {
			try {
		/*	int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
					RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
		AudioRecord	audioRecord = new AudioRecord(
					MediaRecorder.AudioSource.DEFAULT, RECORDER_SAMPLERATE,
					RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, bufferSize);
*/
			//	Log.i("Thread name.....", "OnRecordPositionUpdateListener on thread: " + Thread.currentThread().getName());
			short[] buffer = new short[blockSize];
			double[] toTransform = new double[blockSize];
			/*try{
				recorder.startRecording();
				//mVisualizerView.link(audioRecord);
			}
			catch(IllegalStateException e){
				Log.e("Recording failed", e.toString());
				//Toast.makeText(getMainActivity().getApplicationContext(), "Recording Failed", 300).show();
			}*/
			// recorder.startRecording();
			while (started) {
			//	int bufferReadResult = audioRecord.read(buffer, 0, blockSize);
			read = recorder.read(buffer, 0, blockSize);
				for (int i = 0; i < blockSize && i < read; i++) {
					toTransform[i] = (double) buffer[i] / 32768.0; // signed 16 bit
					//Log.e("toTransform",toTransform[i]+"");
				}
				
				transformer.ft(toTransform);
				publishProgress(toTransform);
				break;
			}
			// recorder.stop();
			/*try{
				recorder.stop();
				//VisualizerView.mVisualizer.setEnabled(false);
			}
			catch(IllegalStateException e){
				Log.e("Stop failed", e.toString());
				//Toast.makeText(getMainActivity().getApplicationContext(), "Stop Failed", 300).show();
			}*/
			}
			catch (Throwable t) {
				Log.e("AudioRecord", "Recording Failed");
				Toast.makeText(VoiceRecord.this, "Recording Failed", 300).show();
			}

			return null;

		}
		 
		protected void onProgressUpdate(double[]... toTransform) {
			//Log.e("RecordingProgress", "Displaying in progress");
			//Toast.makeText(getMainActivity().getApplicationContext(), "Recording on progree...", 1000).show();
			/*
			for (int i = 0; i < toTransform[0].length ; i+=60) {
				int x = 0;
				int downy = (int) (100 - (toTransform[0][i] * 10));
				int upy = 200;

				Log.e("downy",downy+"");
				canvas.drawLine(50,50,downy,50, paint);
				
			}
			imageView.invalidate();*/
			 for (int i = 0; i < toTransform[0].length; i+=60) {
	                int x = 0;
	                int downy = (int) (100 - (toTransform[0][i] * 10));
	                int upy = 100;

	                canvas.drawLine(50, 50, downy, 100, paint);
	            }

	            imageView.invalidate();
		}

	}
	
	

}



	

