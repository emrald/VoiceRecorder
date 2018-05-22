package com.clarusapps.voicerecorder;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;


import android.R.color;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class RecordList extends Activity {

	ListView lv;
	ArrayList<RecordDetail> lisrar;
	static CustomRecordAdaptor adaptor;
	static int currentselected;
	File file;
	String[] fileArray;
	private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
	RecordDetail recordobj;
	ArrayList<String> arrrecord=new ArrayList<String>();
	static ArrayList<RecordDetail> recordarr = new ArrayList<RecordDetail>();
	public static final String TIME_FORMAT = "h:mm a d MMMMMMMMM";
	String timeduration;
	static String recordpath;
	static String filename;
	Context context;
	Button btnedit,btndelete,btnshare,btnlist,btnsetting;
	AlertDialog alert;
	String filepath = Environment.getExternalStorageDirectory().getPath();
	File recording;
	boolean FLAG_DUPNAME=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recordlist);

		lv = (ListView) findViewById(R.id.recordlist_list);
		btndelete=(Button)findViewById(R.id.recordlist_delete);
		btnedit=(Button)findViewById(R.id.recordlist_edit);
		btnlist=(Button)findViewById(R.id.recordlist_btn_listview);
		btnsetting=(Button)findViewById(R.id.recordlist_btn_settings);
		btnshare=(Button)findViewById(R.id.recordlist_share);
		context=getApplicationContext();
		// lisrar=VoiceRecord.recordarr;

		Log.e("list", lisrar + "");

		File file = new File(filepath,AUDIO_RECORDER_FOLDER);

		if(file.exists()){
			setrecorddetail();
		}

		btnlist.setOnClickListener(new OnClickListener() {

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
				Intent i=new Intent(RecordList.this,Setting.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}
		});

		btnshare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(CustomRecordAdaptor.editcbcnt==0)
				{
					Toast.makeText(RecordList.this,"Select one record",Toast.LENGTH_SHORT).show();
					//lv.setAdapter(new CustomRecordAdaptor(getApplicationContext(),recordarr));
				}
				else if(CustomRecordAdaptor.editcbcnt>1)
				{
					Toast.makeText(RecordList.this,"Select only one record",Toast.LENGTH_SHORT).show();
					CustomRecordAdaptor.checks.clear();
					CustomRecordAdaptor.recorddetail.clear();
					//lv.setAdapter(new CustomRecordAdaptor(getApplicationContext(),recordarr));
				}
				else
				{

					for(int i=0;i<CustomRecordAdaptor.checks.size();i++){

						if(CustomRecordAdaptor.checks.get(i)==1){

							recording = new File(recordpath + CustomRecordAdaptor.recorddetail.get(i));
						}
					}
					Intent intent=new Intent(android.content.Intent.ACTION_SEND);
					intent.setType("audio/*");
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
					intent.putExtra(Intent.EXTRA_STREAM,Uri.parse("file:///"+recording));
					startActivity(Intent.createChooser(intent, "Share Sound File"));
					// Add data to the intent, the receiving app will decide what to do with it.
					//intent.putExtra(Intent.EXTRA_TEXT,recording);

				}
			}
		});
		btndelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(CustomRecordAdaptor.editcbcnt==0)
				{
					Toast.makeText(RecordList.this,"Select one record",Toast.LENGTH_SHORT).show();
					//lv.setAdapter(new CustomRecordAdaptor(getApplicationContext(),recordarr));
				}
				else
				{
					AlertDialog.Builder builder =new AlertDialog.Builder(RecordList.this);
					builder.setMessage("Are you sure you want to delete record?");

					builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						for(int i=0;i<CustomRecordAdaptor.checks.size();i++){

							if(CustomRecordAdaptor.checks.get(i)==1){
								//remove items from the list here for example from ArryList 
								File file = new File(recordpath + CustomRecordAdaptor.recorddetail.get(i));
								boolean deleted = file.delete();
								Log.e("deleted",CustomRecordAdaptor.recorddetail.get(i));
								//similarly remove other items from the list from that particular postion
								setrecorddetail();
								//lv.setAdapter(new CustomRecordAdaptor(getApplicationContext(),recordarr));
							}
						}
						
						//lv.setAdapter(new CustomRecordAdaptor(getApplicationContext(),recordarr));
						adaptor.notifyDataSetChanged();
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
			}
		});


		btnedit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(CustomRecordAdaptor.editcbcnt==0)
				{
					Toast.makeText(RecordList.this,"Select one record",Toast.LENGTH_SHORT).show();
					//lv.setAdapter(new CustomRecordAdaptor(getApplicationContext(),recordarr));
				}
				else if(CustomRecordAdaptor.editcbcnt>1)
				{
					Toast.makeText(RecordList.this,"Select only one record",Toast.LENGTH_SHORT).show();
					CustomRecordAdaptor.checks.clear();
					CustomRecordAdaptor.recorddetail.clear();
					//lv.setAdapter(new CustomRecordAdaptor(getApplicationContext(),recordarr));
					adaptor.notifyDataSetChanged();
				}
				else
				{
					FLAG_DUPNAME=false;
					AlertDialog.Builder builder =new AlertDialog.Builder(RecordList.this);
					builder.setMessage("Edit Recoding Name");
					final EditText etrecordname =new EditText(context);
					builder.setView(etrecordname);

					builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

							String Recordingname=etrecordname.getText().toString();
							for(int rec=0;rec<recordarr.size();rec++)
							{
								if(recordarr.get(rec).recordname.equalsIgnoreCase(Recordingname))
								{
									Toast.makeText(RecordList.this,"Record Name already exist",Toast.LENGTH_SHORT).show();
									Log.e(Recordingname,"duplicate");
									FLAG_DUPNAME=true;
								}
							}
							if(FLAG_DUPNAME==false)
							{
								for(int i=0;i<CustomRecordAdaptor.checks.size();i++){

								if(CustomRecordAdaptor.checks.get(i)==1){

									File from = new File(recordpath + CustomRecordAdaptor.recorddetail.get(i));
									Log.e("from",recordpath + CustomRecordAdaptor.recorddetail.get(i));
									Log.e("to", recordpath +Recordingname);
									File to=new File(recordpath +Recordingname);
									from.renameTo(to);
									setrecorddetail();
								}
								
							}
							}
							//lv.setAdapter(new CustomRecordAdaptor(getApplicationContext(),recordarr));
							CustomRecordAdaptor.checks.clear();
							CustomRecordAdaptor.recorddetail.clear();
							adaptor.notifyDataSetChanged();
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
			}
		});
		adaptor=new CustomRecordAdaptor(getApplicationContext(),recordarr);
		lv.setAdapter(adaptor);

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				currentselected=arg2;
				
				//lv.getChildAt(arg2).setBackgroundColor(Color.parseColor("#ff7d00"));
				 ((CustomRecordAdaptor)adaptor).setSelected(arg2);
				 adaptor.notifyDataSetChanged();
				//arg0.setBackgroundColor(Color.parseColor("#ff7d00"));
				Intent i = new Intent(RecordList.this, MusicPlayerActivity.class);
				filename=((RecordDetail)lv.getAdapter().getItem(arg2)).getrecordname();
				String recordtime=((RecordDetail)lv.getAdapter().getItem(arg2)).getrecordtime();
				String filesize=((RecordDetail)lv.getAdapter().getItem(arg2)).getrecordsize();
				i.putExtra("filename",filename);
				i.putExtra("recordtime",recordtime);
				i.putExtra("filesize",filesize);
				startActivity(i);
			}

		});

	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub

		if(file.exists()){
			setrecorddetail();
		}
		adaptor=new CustomRecordAdaptor(getApplicationContext(),recordarr);
		((CustomRecordAdaptor)adaptor).setSelected(currentselected);
		lv.setAdapter(adaptor);
		
		super.onResume();
	}
	
	public void setrecorddetail()
	{
		recordarr.clear();

		file = new File(filepath, AUDIO_RECORDER_FOLDER);

		File[] filesarray = file.listFiles();
		fileArray = new String[filesarray.length];


		SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
		for (int i = 0; i < filesarray.length; i++) {

			File f=filesarray[i];

			fileArray[i] = filesarray[i].getName();

			String filesize = String.valueOf(f.length() / 1024 + "KB");

			recordobj = new RecordDetail();

			Date lastModified = new Date(f.lastModified());

			String Timein12hourFormat = timeFormat.format(lastModified);

			MediaPlayer mp = new MediaPlayer();
			FileInputStream fs = null;
			FileDescriptor fd = null;
			try {
				fs = new FileInputStream(f);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				fd = fs.getFD();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				mp.setDataSource(fd);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				mp.prepare();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // might be optional
			int length = mp.getDuration();

			recordobj.setrecordsize(filesize);
			recordobj.setrecordtime(Timein12hourFormat);
			recordobj.setrecordname(f.getName());
			recordobj.setrecordduration(String.format("%d:%d", 
					TimeUnit.MILLISECONDS.toMinutes(length),
					TimeUnit.MILLISECONDS.toSeconds(length) - 
					TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(length))));
			recordarr.add(recordobj);
			recordpath = file.getAbsolutePath() + "/";
		}

	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		finish();
		super.onDestroy();
	}
}
