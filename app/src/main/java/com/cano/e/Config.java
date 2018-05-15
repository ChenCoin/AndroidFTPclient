package com.cano.e;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;

import com.cano.e.Event.MoreEvent;
import com.cano.e.Event.SiteEvent;
import com.cano.e.Model.SiteDB;
import com.cano.e.UI.SiteConfirm;
import com.cano.e.UI.TextTip;

/**
 * Created by Cano on 2018/5/5.
 */

public class Config {

	private static Config config;

	public Config(MainActivity activity) {
		this.activity = activity;
		config = this;
	}

	public static Config instance() {
		return config;
	}

	public MainActivity getActivity() {
		return activity;
	}

	private MainActivity activity;

	public ViewCan getViewCan() {
		return viewCan;
	}

	public void setViewCan(ViewCan viewCan) {
		this.viewCan = viewCan;
	}

	private ViewCan viewCan;

	public SiteEvent getSiteEvent() {
		return siteEvent;
	}

	public void setSiteEvent(SiteEvent siteEvent) {
		this.siteEvent = siteEvent;
	}

	private SiteEvent siteEvent;

	public TextTip getTextTip() {
		if (textTip == null) textTip = new TextTip();
		return textTip;
	}

	private TextTip textTip;

	public SiteConfirm getSiteConfirm(View view) {
		if (siteConfirm == null) siteConfirm = new SiteConfirm(view);
		return siteConfirm;
	}

	private SiteConfirm siteConfirm;

	public SiteDB getSiteDB() {
		return siteDB;
	}

	public void setSiteDB(SiteDB siteDB) {
		this.siteDB = siteDB;
	}

	private SiteDB siteDB;

	public MoreEvent getMoreEvent() {
		return moreEvent;
	}

	public void setMoreEvent(MoreEvent moreEvent) {
		this.moreEvent = moreEvent;
	}

	private MoreEvent moreEvent;

	public final int[] myTheme = {R.style.AppTheme, R.style.blue, R.style.GreenTheme, R.style.GrayTheme};

	public int getTheme() {
		SharedPreferences sp = activity.getSharedPreferences("store", activity.MODE_PRIVATE);
		return sp.getInt("myTheme", 0);
	}

	public void setTheme(int theme) {
		SharedPreferences sp = activity.getSharedPreferences("store", activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt("myTheme", theme);
		editor.commit();
	}

	public static final int sortName = 0;
	public static final int sortDate = 1;
	public static final int sortSize = 2;
	public static final int sortType = 3;

	public int getSortPattern() {
		SharedPreferences sp = activity.getSharedPreferences("store", activity.MODE_PRIVATE);
		return sp.getInt("sortPattern", 0);
	}

	public void setSortPattern(int sortPattern) {
		SharedPreferences sp = activity.getSharedPreferences("store", activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt("sortPattern", sortPattern);
		editor.commit();
	}

	final int showSimple = 0;
	final int showDetail = 1;
	final int showIcon = 2;
	final int showBigIcon = 3;

	public int getShowPattern() {
		SharedPreferences sp = activity.getSharedPreferences("store", activity.MODE_PRIVATE);
		return sp.getInt("showPattern", 0);
	}

	public void setShowPattern(int showPattern) {
		SharedPreferences sp = activity.getSharedPreferences("store", activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt("showPattern", showPattern);
		editor.commit();
	}

	public boolean isFolderFirst() {
		SharedPreferences sp = activity.getSharedPreferences("store", activity.MODE_PRIVATE);
		return sp.getBoolean("folderFirst", true);
	}

	public void setFolderFirst(boolean folderFirst) {
		SharedPreferences sp = activity.getSharedPreferences("store", activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean("folderFirst", folderFirst);
		editor.commit();
	}

	public boolean isShowAllFile() {
		SharedPreferences sp = activity.getSharedPreferences("store", activity.MODE_PRIVATE);
		return sp.getBoolean("showAllFile", true);
	}

	public void setShowAllFile(boolean showAllFile) {
		SharedPreferences sp = activity.getSharedPreferences("store", activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean("showAllFile", showAllFile);
		editor.commit();
	}

}
