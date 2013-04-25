package jie.android.lac.app;

import android.content.Context;
import android.content.SharedPreferences;

public final class Configuration {

	public static final String PREFS_WIZARD_DONE			=	"wizard_done";
	
	public static final String PREFS_SLIDING_FADE_ENABLED	=	"sliding_fade_enable";
	public static final String PREFS_SLIDING_FADE_DEGREE	=	"sliding_fade_degree";
	
	public static final String PREFS_DATA_LOCATION			=	"data_location";//0:not init; 1: card; 2: local
//	private static final String PREFS_DATA_FOLDER			=	"data_folder";
	
	private SharedPreferences prefs = null;
	
	public Configuration(Context context) {
		prefs = context.getSharedPreferences("LAC", Context.MODE_PRIVATE);
	}
	
	public boolean getWizardDone() {
		return prefs.getBoolean(PREFS_WIZARD_DONE, false);
	}
	
	public void setWizardDone(boolean value) {
		prefs.edit().putBoolean(PREFS_WIZARD_DONE, value);
	}
	
	public boolean getSlidingFadeEnabled() {
		return prefs.getBoolean(PREFS_SLIDING_FADE_ENABLED, true);
	}
	
	public void setSlidingFadeEnabled(boolean value) {
		prefs.edit().putBoolean(PREFS_SLIDING_FADE_ENABLED, value);
	}
	
	public float getSlidingFadeDegree() {
		return prefs.getFloat(PREFS_SLIDING_FADE_DEGREE, 0.35f);
	}
	
	public void setSlidingFadeDegree(float value) {
		prefs.edit().putFloat(PREFS_SLIDING_FADE_DEGREE, value);
	}
	
	public int getDataLocation() {
		return prefs.getInt(PREFS_DATA_LOCATION, -1); 
	}
	
	public void setDataLocation(int value) {
		prefs.edit().putInt(PREFS_DATA_LOCATION, value);
	}
	
//	public final String getDataFolder() {
//		return prefs.getString(PREFS_DATA_FOLDER, "/mnt/sdcard/");
//	}
//	
//	public void setDataFolder(final String value) {
//		prefs.edit().putString(PREFS_DATA_FOLDER, value);
//	}
	
}
