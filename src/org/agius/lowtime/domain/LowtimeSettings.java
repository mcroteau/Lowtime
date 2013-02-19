package org.agius.lowtime.domain;

import static org.agius.lowtime.LowtimeConstants.*;


import android.content.SharedPreferences;

public class LowtimeSettings {

	private int hour;
	private int minutes;
	private int range;
	private boolean active;
	private String waketone;
	private String waketoneUri;
	private SharedPreferences settings;
	SharedPreferences.Editor editor; 
	
	
	public LowtimeSettings(SharedPreferences settings){
		this.settings = settings;
		this.editor = settings.edit();
		resetValues();
	}
	
	
	public int getMinutes() {
		return minutes;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getWaketone() {
		return waketone;
	}

	public void setWaketone(String waketone) {
		this.waketone = waketone;
	}

	public String getWaketoneUri() {
		return waketoneUri;
	}

	public void setWaketoneUri(String waketoneUri) {
		this.waketoneUri = waketoneUri;
	}
	
	public SharedPreferences getSettings() {
		return settings;
	}

	public void setSettings(SharedPreferences settings) {
		this.settings = settings;
	}
	
	
	public boolean settingsSet(){
		return (settings != null && minutes != 0);
	}

	public void reinitialize(SharedPreferences settings){
		this.settings = settings;
		this.editor = settings.edit();
		resetValues();
	}
	
	public void commit(){
		editor.commit();
	}
	
	public void resetValues(){
		if(settings != null){
	        setWaketone(settings.getString(LOWTIME_WAKETONE, ""));
	        setWaketoneUri(settings.getString(LOWTIME_WAKETONE_URI, ""));
	        setRange(settings.getInt(LOWTIME_RANGE, 0));
	        setHour(settings.getInt(LOWTIME_HOUR, 0));
	        setMinutes(settings.getInt(LOWTIME_MINUTES, 0));
	        setActive(settings.getBoolean(LOWTIME_ACTIVE, false));
        }
	}
	
}
