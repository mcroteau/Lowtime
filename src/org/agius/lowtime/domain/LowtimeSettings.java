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
	private int snoozeDuration;
	private boolean lowtimeLaunched;
	private int alarmId;
	private boolean alarmActive;
	private SharedPreferences settings;
	SharedPreferences.Editor editor; 
	
	
	public LowtimeSettings(SharedPreferences settings){
		this.settings = settings;
		this.editor = settings.edit();
		this.alarmId = INACTIVE_ID;
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
	
	public int getSnoozeDuration() {
		return snoozeDuration;
	}

	public void setSnoozeDuration(int snoozeDuration) {
		this.snoozeDuration = snoozeDuration;
	}
	
	public boolean isLowtimeLaunched() {
		return lowtimeLaunched;
	}

	public void setLowtimeLaunched(boolean lowtimeLaunched) {
		this.lowtimeLaunched = lowtimeLaunched;
	}

	
	public int getAlarmId() {
		return alarmId;
	}

	public void setAlarmId(int alarmId) {
		this.alarmId = alarmId;
	}
	

	public boolean isAlarmActive(){
		return this.alarmActive;
	}
	
	public void setAlarmActive(boolean alarmActive) {
		this.alarmActive = alarmActive;
	}


	public SharedPreferences getSettings() {
		return settings;
	}

	public void setSettings(SharedPreferences settings) {
		this.settings = settings;
	}
	
	
	public boolean settingsSet(){
		return (settings != null && minutes != 0 && range != 0 && snoozeDuration != 0);
	}

	public void reinitialize(SharedPreferences settings){
		this.settings = settings;
		this.editor = settings.edit();
		resetValues();
	}
	
	public void commit(){
		setEditorValues();
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
	        setSnoozeDuration(settings.getInt(LOWTIME_SNOOZE, 0));
	        setAlarmId(settings.getInt(LOWTIME_ALARM_ID, 0));
	        setAlarmActive(settings.getBoolean(LOWTIME_ALARM_ACTIVE, false));
        }
	}
	
	public void setEditorValues(){
		editor.putString(LOWTIME_WAKETONE, getWaketone());
		editor.putString(LOWTIME_WAKETONE_URI, getWaketoneUri());
		editor.putInt(LOWTIME_RANGE, getRange());
		editor.putInt(LOWTIME_HOUR, getHour());
		editor.putInt(LOWTIME_MINUTES, getMinutes());
		editor.putBoolean(LOWTIME_ACTIVE, isActive());		
		editor.putInt(LOWTIME_SNOOZE, getSnoozeDuration());
		editor.putInt(LOWTIME_ALARM_ID, getAlarmId());
		editor.putBoolean(LOWTIME_ALARM_ACTIVE, isAlarmActive());
	}
	
}
