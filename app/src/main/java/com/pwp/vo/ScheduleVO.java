package com.pwp.vo;

import java.io.Serializable;

/**
 * 日程的VO类
 * @author jack_peng
 *
 */
public class ScheduleVO implements Serializable{

	private int scheduleID;
	private int remindID;
	private String scheduleContent;
	private String scheduleTtile;
	private String scheduleLoaction;
	private String scheduleDate;
	private String time;
	private long alartime;
	private long alartimeend;
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public ScheduleVO(){}
	
	public ScheduleVO(int scheduleID,String scheduleTtile,String scheduleLoaction,int remindID,String scheduleContent,String scheduleDate,String time,Long alartime,Long alartimeend){
		this.scheduleID = scheduleID;
		this.remindID = remindID;
		this.scheduleContent = scheduleContent;
		this.scheduleTtile = scheduleTtile;
		this.scheduleLoaction = scheduleLoaction;
		this.scheduleDate = scheduleDate;
		this.time=time;
		this.alartime=alartime;
		this.alartimeend = alartimeend;
	}
	
	public long getAlartimeend() {
		return alartimeend;
	}

	public void setAlartimeend(long alartimeend) {
		this.alartimeend = alartimeend;
	}

	public long getAlartime() {
		return alartime;
	}

	public void setAlartime(long alartime) {
		this.alartime = alartime;
	}

	public int getScheduleID() {
		return scheduleID;
	}
	public void setScheduleID(int scheduleID) {
		this.scheduleID = scheduleID;
	}
	
	public String getScheduleTtile() {
		return scheduleTtile;
	}

	public void setScheduleTtile(String scheduleTtile) {
		this.scheduleTtile = scheduleTtile;
	}

	public String getScheduleLoaction() {
		return scheduleLoaction;
	}

	public void setScheduleLoaction(String scheduleLoaction) {
		this.scheduleLoaction = scheduleLoaction;
	}

	public int getRemindID() {
		return remindID;
	}
	public void setRemindID(int remindID) {
		this.remindID = remindID;
	}
	public String getScheduleContent() {
		return scheduleContent;
	}
	public void setScheduleContent(String scheduleContent) {
		this.scheduleContent = scheduleContent;
	}
	public String getScheduleDate() {
		return scheduleDate;
	}

	public void setScheduleDate(String scheduleDate) {
		this.scheduleDate = scheduleDate;
	}
	
}
