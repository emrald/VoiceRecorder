package com.clarusapps.voicerecorder;

import java.io.File;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class Setting extends Activity{

	ListView lv;
	AlertDialog alert;
	Button btnback;
	static String recordnameprefix="Record";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		
		lv=(ListView)findViewById(R.id.setting_list);
		btnback=(Button)findViewById(R.id.setting_btn_back);
		
	    String[] values = new String[] { "File Name Prefix","More App"}; 
                 
   
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, android.R.id.text1, values);

	    lv.setAdapter(adapter); 

	    
	    btnback.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
					finish();
			}
		});
	    
	    lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
					String item=(String) lv.getAdapter().getItem(arg2);
					
					if(item.equalsIgnoreCase("File Name Prefix"))
					{
						AlertDialog.Builder builder =new AlertDialog.Builder(Setting.this);
						builder.setMessage("Filename Prefix ");
						final EditText etrecordname =new EditText(Setting.this);
						builder.setView(etrecordname);

						builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub

								recordnameprefix=etrecordname.getText().toString();
						
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
					if(item.equalsIgnoreCase("More App"))
					{
						Intent i=new Intent(Setting.this,MoreActivity.class);
						i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(i);
						
					}
			}
	    	
		});
		
		
	}
	
}
