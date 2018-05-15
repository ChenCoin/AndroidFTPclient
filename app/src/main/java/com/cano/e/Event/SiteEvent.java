package com.cano.e.Event;

import android.app.Activity;
import android.content.ContentValues;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.cano.e.Config;
import com.cano.e.Model.SiteDB;
import com.cano.e.R;
import com.cano.e.UI.SiteEdit;
import com.cano.e.UI.SiteMenu;
import com.cano.e.UI.SitePage;
import com.cano.e.Util.SiteData;
import com.cano.e.Util.SiteHandle;
import com.cano.e.ViewCan;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Cano on 2018/5/6.
 */

public class SiteEvent {

	Activity activity;
	View view;

	SiteData siteData;
	SitePage sitePage;
	SiteHandle siteHandle;
	SiteDB siteDB;

	public void initView() {
		activity = Config.instance().getActivity();
		view = LayoutInflater.from(activity).inflate(R.layout.pagesite, null);
		ViewCan viewCan = Config.instance().getViewCan();
		viewCan.addPage(view, (String) activity.getResources().getText(R.string.siteTitle));

		sitePage = new SitePage(view);
		siteData = new SiteData();
		siteDB = new SiteDB(Config.instance().getActivity());
		Config.instance().setSiteDB(siteDB);
		notifyUI();
	}

	public void notifyUI() {
		sitePage.initView(siteData.getSiteList());
	}

	public void clicked(Map<String, Object> site) {
		FtpEvent ftpEvent = new FtpEvent();
		ftpEvent.initView(site);
	}

	public void longClicked(Map<String, Object> site) {
		if (siteHandle == null) siteHandle = new SiteHandle(view);
		siteHandle.showMenu(site);
	}

	public void addSiteClicked() {
		if (siteHandle == null) siteHandle = new SiteHandle(view);
		siteHandle.add();
	}

	public void addSite(ContentValues ftpSite) {
		siteData.insert(ftpSite);
	}

	public void deleteSite(String name) {
		siteDB.delete(name);
		notifyUI();
	}

	public void modifySite(ContentValues ftpSite) {
		siteDB.modify(ftpSite);
		Config.instance().getTextTip().show("修改成功", Color.BLACK);
		notifyUI();
	}

	public void scanAddSite(String site) {
		if (site.substring(0, 6).equals("ftp://")) {
			String siteContent = site.substring(6);
			int a = siteContent.indexOf(":");
			int b = siteContent.indexOf("@");
			int c = siteContent.indexOf(":", b);
			String user = siteContent.substring(0, a);
			String password = siteContent.substring(a + 1, b);
			String hostname = siteContent.substring(b + 1, c);
			String port = siteContent.substring(c + 1);
			int PORT;
			try {
				PORT = Integer.valueOf(port).intValue();
			} catch (Exception e) {
				Config.instance().getTextTip().show("错误的二维码", Color.RED);
				return;
			}

			ContentValues ftpSite = new ContentValues();
			ftpSite.put("name", hostname);
			ftpSite.put("site", hostname);
			ftpSite.put("port", PORT);
			ftpSite.put("user", user);
			ftpSite.put("password", password);

			if (siteHandle == null) siteHandle = new SiteHandle(view);
			siteHandle.add(ftpSite);
		} else {
			Config.instance().getTextTip().show("错误的二维码", Color.RED);
		}
	}

}
