package com.cano.e.Event;

import android.view.LayoutInflater;
import android.view.View;

import com.cano.e.Config;
import com.cano.e.Model.FtpUtil;
import com.cano.e.R;
import com.cano.e.Util.FtpHandle;
import com.cano.e.ViewCan;

import org.apache.commons.net.ftp.FTPFile;

import java.util.Map;

/**
 * Created by Cano on 2018/5/6.
 */

public class FtpEvent {

	View view;
	FtpHandle ftpHandle;
	FtpUtil ftpUtil;

	public void initView(Map<String, Object> site) {
		view = LayoutInflater.from(Config.instance().getActivity()).inflate(R.layout.pageftp, null);
		ViewCan viewCan = Config.instance().getViewCan();
		viewCan.addPage(view, site.get("name").toString());

		ftpUtil = new FtpUtil();
		ftpUtil.bind(site);

		ftpHandle = new FtpHandle(view, ftpUtil, this);
		ftpHandle.bindView();
	}

	public void dirLongClicked(FTPFile ftpFile) {
		ftpHandle.dirLongClicked(ftpFile);
	}

	public void fileClicked(FTPFile ftpFile) {
		ftpHandle.fileClicked(ftpFile);
	}

	public void fileLongClicked(FTPFile ftpFile) {
		ftpHandle.fileLongClicked(ftpFile);
	}

}
