package com.cano.e.Util;

import android.graphics.Color;
import android.view.View;

import com.cano.e.Config;
import com.cano.e.Event.FtpEvent;
import com.cano.e.Event.TransEvent;
import com.cano.e.Model.FtpUtil;
import com.cano.e.UI.FtpConfirm;
import com.cano.e.UI.FtpDetail;
import com.cano.e.UI.FtpMenu;
import com.cano.e.UI.FtpPage;
import com.cano.e.UI.FtpRename;

import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Cano on 2018/5/7.
 */

public class FtpHandle {

	View view;
	FtpUtil ftpUtil;
	FtpPage ftpPage;
	FtpData ftpData;

	public FtpHandle(View view, FtpUtil ftpUtil, FtpEvent ftpEvent) {
		this.view = view;
		this.ftpUtil = ftpUtil;
		ftpPage = new FtpPage(view, this);
		ftpData = new FtpData(ftpUtil, ftpEvent, this);
		ftpData.updateLocalFile();
	}

	public void bindView() {
		new Thread(() -> {
			List ftpList = ftpData.ftpFileList();

			Config.instance().getActivity().runOnUiThread(() -> {
				if (ftpList == null)
					Config.instance().getTextTip().show("网络连接出错", Color.RED);
				else
					ftpPage.setFtpList(ftpList);
			});
		}).start();
	}

	public void setFtpUrl(String url) {
		ftpPage.setFtpUrl(url);
	}

	public void setLocalFileUrl(String url) {
		ftpPage.setFileUrl(url);
	}

	public void setLocalFileList(List files) {
		ftpPage.setFileList(files);
	}

	public void updateLocalFile() {
		ftpData.updateLocalFile();
	}

	private FTPFile remoteFile;
	private File localFile;

	// menu display
	FtpMenu ftpMenu;

	public void dirLongClicked(FTPFile ftpFile) {
		remoteFile = ftpFile;
		if (ftpMenu == null) ftpMenu = new FtpMenu(view);
		ftpMenu.showDir(() -> {
			attr();
		}, () -> {
			rename();
		}, () -> {
			delete();
		});
	}

	public void fileClicked(FTPFile ftpFile) {
		remoteFile = ftpFile;
		if (ftpMenu == null) ftpMenu = new FtpMenu(view);
		ftpMenu.showFtpFile(() -> {
			attr();
		}, () -> {
			rename();
		}, () -> {
			delete();
		}, () -> {
			download();
		});
	}

	public void fileClicked(File file) {
		localFile = file;
		if (ftpMenu == null) ftpMenu = new FtpMenu(view);
		ftpMenu.showLoaclFile(() -> {
			upload();
		});
	}

	public void fileLongClicked(FTPFile ftpFile) {
		remoteFile = ftpFile;
		ftpFileDrag = true;
	}

	public void fileLongClicked(File file) {
		localFile = file;
		ftpFileDrag = false;
	}

	private boolean ftpFileDrag = true;

	public boolean isFtpFileDrag() {
		return ftpFileDrag;
	}

	public void showTip(String content) {
		ftpPage.setTip(content);
	}

	public void download() {
		if (ftpConfirm == null) ftpConfirm = new FtpConfirm(view);
		ftpConfirm.show("下载文件", isMultDownload -> {
			TransEvent transEvent = Config.instance().getMoreEvent().getTransEvent();
			transEvent.download(ftpUtil, remoteFile.getName(), ftpData.currentFile, isMultDownload);
		});
	}

	public void upload() {
		Config.instance().getTextTip().show("已添加到传输列表");
		TransEvent transEvent = Config.instance().getMoreEvent().getTransEvent();
		transEvent.upload(ftpUtil, localFile);
	}

	FtpDetail ftpDetail;

	public void attr() {
		if (ftpDetail == null) ftpDetail = new FtpDetail(view);
		String content = remoteFile.getName() + "\n"
				+ ftpUtil.getPath() + "\n"
				+ convertFileSize(remoteFile.getSize()) + "\n"
				+ new SimpleDateFormat("yyyy-MM-dd  HH:mm").format(remoteFile.getTimestamp().getTime()) + "\n"
				+ remoteFile.getRawListing().substring(0,remoteFile.getRawListing().indexOf(" "));
		ftpDetail.show(content);
	}

	FtpRename ftpRename;

	public void rename() {
		if (ftpRename == null) ftpRename = new FtpRename(view);
		ftpRename.show(remoteFile.getName(), name -> {
			new Thread(() -> {
				boolean success = ftpUtil.rename(remoteFile.getName(), name);
				Config.instance().getActivity().runOnUiThread(() -> {
					if (success) {
						Config.instance().getTextTip().show("重命名成功");
						bindView();
					} else {
						Config.instance().getTextTip().show("重命名失败");
					}
				});
			}).start();
		});
	}

	public void createFolderClicked() {
		if (ftpRename == null) ftpRename = new FtpRename(view);
		ftpRename.show(folder -> {
			new Thread(() -> {
				boolean success = ftpUtil.createFolder(folder);
				Config.instance().getActivity().runOnUiThread(() -> {
					if (success) {
						Config.instance().getTextTip().show("新建文件夹成功");
						bindView();
					} else {
						Config.instance().getTextTip().show("新建文件夹失败");
					}
				});
			}).start();
		});
	}

	FtpConfirm ftpConfirm;

	public void delete() {
		if (ftpConfirm == null) ftpConfirm = new FtpConfirm(view);
		String content;
		if (remoteFile.isDirectory()) content = "删除整个文件夹";
		else content = "删除文件";
		ftpConfirm.show(content, () -> {
			new Thread(() -> {
				boolean success = ftpUtil.delete(remoteFile);
				Config.instance().getActivity().runOnUiThread(() -> {
					if (success) {
						Config.instance().getTextTip().show("删除成功");
						bindView();
					} else {
						Config.instance().getTextTip().show("删除失败");
					}
				});
			}).start();
		});
	}

	public String convertFileSize(long size) {
		// 如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
		double value = (double) size;
		if (value < 1024) {
			return String.valueOf(value) + "B";
		} else {
			value = new BigDecimal(value / 1024).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
		}
		// 如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
		// 因为还没有到达要使用另一个单位的时候
		// 接下去以此类推
		if (value < 1024) {
			return String.valueOf(value) + "K";
		} else {
			value = new BigDecimal(value / 1024).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
		}
		if (value < 1024) {
			return String.valueOf(value) + "M";
		} else {
			// 否则如果要以GB为单位的，先除于1024再作同样的处理
			value = new BigDecimal(value / 1024).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
			return String.valueOf(value) + "G";
		}
	}

}
