package com.cano.e.Util;

import android.content.ContentValues;
import android.view.View;

import com.cano.e.Config;
import com.cano.e.Event.SiteEvent;
import com.cano.e.Model.SiteDB;
import com.cano.e.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Cano on 2018/5/6.
 */

public class SiteData {
	SiteDB siteDB;
	SiteEvent siteEvent = Config.instance().getSiteEvent();

	public List<Map<String, Object>> getSiteList() {
		List<Map<String, Object>> data = new ArrayList<>();

		siteDB = Config.instance().getSiteDB();
		List<Map<String, Object>> Sites = siteDB.getAll();

		for (int i = 0; i < Sites.size(); i++) {
			Map<String, Object> map = new HashMap<>();
			Map<String, Object> ftpInfo = Sites.get(i);
			String name = ftpInfo.get("name").toString();
			map.put("text", name);

			// 经典主题时，更换图标
			if (Config.instance().getTheme() == 0)
				map.put("image", R.mipmap.class_site);
			else
				map.put("image", R.mipmap.ic_folder_site_48dp);

			map.put("click", (View.OnClickListener) view -> {
				siteEvent.clicked(ftpInfo);
			});
			map.put("longclick", (View.OnLongClickListener) view -> {
				siteEvent.longClicked(ftpInfo);
				return true;
			});
			data.add(map);
		}

		Map<String, Object> map = new HashMap<>();
		map.put("text", "添加FTP");

		// 经典主题时，更换图标
		if (Config.instance().getTheme() == 0)
			map.put("image", R.mipmap.class_add);
		else
			map.put("image", R.mipmap.ic_folder_add_48dp);

		map.put("click", (View.OnClickListener) view -> {
			siteEvent.addSiteClicked();
		});
		map.put("longclick", (View.OnLongClickListener) view -> {
			return true;
		});
		data.add(map);
		return data;
	}

	public void insert(ContentValues ftpSite) {
		siteDB.insert(ftpSite);
		siteEvent.notifyUI();
	}

	public void delete() {

	}

	public void modify() {

	}

}
