package com.clarusapps.voicerecorder;

public class RecordDetail {

	String recordname, recordsize, recordtime, recorddate, recordduration;
	public boolean FLAG_Selected=false,FLAG_UNSELECTED=true;
	public void setrecordname(String recordname) {
		this.recordname = recordname;
	}
	
	public void setrecordsize(String recordsize) {
		this.recordsize = recordsize;
	}

	public void setrecordtime(String recordtime) {
		this.recordtime = recordtime;
	}

	public void setrecorddate(String recorddate) {
		this.recorddate = recorddate;
	}

	public void setrecordduration(String recordduration) {
		this.recordduration = recordduration;
	}

	
	public String getrecordname()
	{
		return this.recordname;
	}
	
	public String getrecordsize()
	{
		return this.recordsize;
	}
	
	public String getrecordtime()
	{
		return this.recordtime;
	}
	
	public String getrecorddate()
	{
		return this.recorddate;
	}
	
	public String getrecordduration()
	{
		return this.recordduration;
	}
}
