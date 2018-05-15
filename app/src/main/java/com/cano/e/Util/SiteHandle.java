package com.cano.e.Util;

import android.content.ContentValues;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;

import com.cano.e.Config;
import com.cano.e.Event.SiteEvent;
import com.cano.e.R;
import com.cano.e.UI.QrShow;
import com.cano.e.UI.SiteConfirm;
import com.cano.e.UI.SiteEdit;
import com.cano.e.UI.SiteMenu;
import com.cano.e.Util.SiteData;
import com.cano.e.ViewCan;

import java.util.Map;

/**
 * Created by Cano on 2018/5/6.
 */

public class SiteHandle {

	View view;

	public SiteHandle(View view) {
		this.view = view;
	}

	SiteEdit siteEdit;
	SiteMenu siteMenu;

	public void add() {
		if (siteEdit == null) siteEdit = new SiteEdit(view);
		siteEdit.show(ftpSite -> {
			String PORT = ftpSite.get("port").toString();
			String Name = ftpSite.get("name").toString();
			String Hostname = ftpSite.get("site").toString();
			if (Hostname.equals("")) {
				Config.instance().getTextTip().show("必须输入地址", Color.RED);
				return;
			}
			try {
				int port = Integer.valueOf(PORT).intValue();
				ftpSite.put("port", port);
			} catch (Exception e) {
				Config.instance().getTextTip().show("端口号输入无效", Color.RED);
				return;
			}
			if (Name.equals("")) ftpSite.put("name", Hostname);
			Config.instance().getSiteEvent().addSite(ftpSite);
		});
	}

	public void add(ContentValues site) {
		if (siteEdit == null) siteEdit = new SiteEdit(view);
		siteEdit.show(site, ftpSite -> {
			String PORT = ftpSite.get("port").toString();
			String Name = ftpSite.get("name").toString();
			String Hostname = ftpSite.get("site").toString();
			if (Hostname.equals("")) {
				Config.instance().getTextTip().show("必须输入地址", Color.RED);
				return;
			}
			try {
				int port = Integer.valueOf(PORT).intValue();
				ftpSite.put("port", port);
			} catch (Exception e) {
				Config.instance().getTextTip().show("端口号输入无效", Color.RED);
				return;
			}
			if (Name.equals("")) ftpSite.put("name", Hostname);
			Config.instance().getSiteEvent().addSite(ftpSite);
		});
	}

	Map<String, Object> site;

	public void showMenu(Map site) {
		this.site = site;
		if (siteMenu == null) siteMenu = new SiteMenu(view);
		siteMenu.show(() -> {
			edit();
		}, () -> {
			delete();
		}, () -> {
			showQRcode();
		});
	}

	private void edit() {
		if (siteEdit == null) siteEdit = new SiteEdit(view);
		siteEdit.show(site, ftpSite -> {
			ContentValues newFtpSite = new ContentValues();
			String newName;
			int newPort;
			String PORT = ftpSite.get("port").toString();
			String Name = ftpSite.get("name").toString();
			String Hostname = ftpSite.get("site").toString();
			if (Hostname.equals("")) {
				Config.instance().getTextTip().show("必须输入地址", Color.RED);
				return;
			}
			try {
				newPort = Integer.valueOf(PORT).intValue();
			} catch (Exception e) {
				Config.instance().getTextTip().show("端口号输入无效", Color.RED);
				return;
			}
			if (Name.equals("")) newName = Hostname;
			else newName = Name;
			newFtpSite.put("name", newName);
			newFtpSite.put("site", Hostname);
			newFtpSite.put("port", newPort);
			newFtpSite.put("coding", ftpSite.get("coding").toString());
			newFtpSite.put("user", ftpSite.get("user").toString());
			newFtpSite.put("password", ftpSite.get("password").toString());
			Config.instance().getSiteEvent().modifySite(newFtpSite);
		});
	}

	private void delete() {
		SiteConfirm siteConfirm = Config.instance().getSiteConfirm(view);
		siteConfirm.show("删除FTP站点", () -> {
			Config.instance().getSiteEvent().deleteSite(site.get("name").toString());
		});
	}


	private void showQRcode() {
		String siteUrl = "ftp://"
				+ site.get("user").toString() + ":"
				+ site.get("password").toString() + "@"
				+ site.get("site").toString() + ":"
				+ site.get("port");

		view = LayoutInflater.from(Config.instance().getActivity()).inflate(R.layout.ftpqrcode, null);
		ViewCan viewCan = Config.instance().getViewCan();
		viewCan.addPage(view, "二维码");
		QrShow qrShow = new QrShow(view);
		qrShow.show(site.get("name").toString(), siteUrl);
	}

}
