package com.clarusapps.voicerecorder;

import java.io.File;
import java.util.ArrayList;
import java.util.zip.Inflater;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class CustomRecordAdaptor extends BaseAdapter {

	public LayoutInflater inflater;
	static ArrayList<Integer> checks=new ArrayList<Integer>();
	static ArrayList<String> recorddetail=new ArrayList<String>();
	ArrayList<RecordDetail> arecord;
	static int editcbcnt;
	int selectedPosition;
		public CustomRecordAdaptor(Context context,ArrayList<RecordDetail> lisrar) {
		// TODO Auto-generated constructor stub
		this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		arecord=lisrar;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arecord.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return arecord.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		convertView=inflater.inflate(R.layout.recordlistrow,null,false);
		
		CheckBox ch=(CheckBox)convertView.findViewById(R.id.recordlistrowcheckbox);
		TextView tvrecord=(TextView)convertView.findViewById(R.id.recordlistrow_tv_record);
		TextView tvrecordsize=(TextView)convertView.findViewById(R.id.recordlistrow_tv_recordingsize);
		TextView tvrecordtime=(TextView)convertView.findViewById(R.id.recordlistrow_tv_time);
		TextView tvrecordduration=(TextView)convertView.findViewById(R.id.recordlistrow_tv_recordingtime);
		
		tvrecord.setText(arecord.get(position).getrecordname());
		tvrecordsize.setText(arecord.get(position).getrecordsize());
		tvrecordtime.setText(arecord.get(position).getrecordtime());
		tvrecordduration.setText(arecord.get(position).getrecordduration());
		editcbcnt=0;
		
		if (position==selectedPosition)	
			convertView.setBackgroundColor(Color.parseColor("#ff7d00"));
        else 
        	convertView.setBackgroundColor(Color.parseColor("#393939"));

		
		ch.setOnClickListener(new OnClickListener() {
			 
			  @Override
			  public void onClick(View v) {
		                //is chkIos checked?
				if (((CheckBox) v).isChecked()) {
					  checks.add(1);
					  recorddetail.add(arecord.get(position).getrecordname());
					  editcbcnt++;
                }
                else{
                 checks.add(0);
                }	
				}
			  
			  
			});
		 
		return convertView;
	}
	

	 public void setSelected(int position) {
	        selectedPosition = position;
	    }
	 

}
