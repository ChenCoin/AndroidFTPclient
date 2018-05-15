package com.cano.e.Event;

import android.view.View;

import com.cano.e.Config;
import com.cano.e.Model.FileIcon;
import com.cano.e.Model.FtpUtil;
import com.cano.e.UI.TransList;
import com.cano.e.Util.TransItem;

import java.io.File;

/**
 * Created by Cano on 2018/5/7.
 */

public class TransEvent {

	View view;
	TransList transList;

	public TransEvent() {
		view = Config.instance().getViewCan().getMorePage();
		transList = new TransList(view, this);
	}

	public void download(FtpUtil ftpUtil, String remoteFile, File localFile, boolean multDownload) {
		TransItem item = new TransItem();

		item.ftpUtil = ftpUtil;
		item.remoteFile = remoteFile;
		item.localFile = localFile;

		item.action = multDownload ? TransItem.Action.multDownload : TransItem.Action.download;
		item.name = remoteFile;
		item.text = "下载自" + ftpUtil.getFtpInfo().get("site");
		item.icon = FileIcon.getIcon(remoteFile);

		transList.addTranslate(item);
	}

	public void upload(FtpUtil ftpUtil, File file) {
		TransItem item = new TransItem();

		item.action = TransItem.Action.upload;
		item.name = file.getName();
		item.text = "上传至" + ftpUtil.getFtpInfo().get("site");
		item.icon = FileIcon.getIcon(file.getName());

		item.ftpUtil = ftpUtil;
		item.localFile = file;

		transList.addTranslate(item);
	}


}
