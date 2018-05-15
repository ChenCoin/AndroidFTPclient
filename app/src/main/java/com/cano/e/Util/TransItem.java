package com.cano.e.Util;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cano.e.Config;
import com.cano.e.Model.FtpUtil;

import java.io.File;
import java.util.Map;

/**
 * Created by Cano on 2018/5/8.
 */

public class TransItem {

	public boolean isStarted = false;

	public FtpUtil ftpUtil;
	public File localFile;
	public String remoteFile;

	public String name;
	public String text;
	public int icon;

	public Runnable control = () -> {

	};

	public TextView textView;
	public ProgressBar progressBar;

	public enum Action {
		download, multDownload, upload
	}

	public Action action;

	public void start() {
		isStarted = true;
		switch (action) {
			case download:
				download();
				break;
			case upload:
				upload();
				break;
			case multDownload:
				multDowmload();
		}
	}

	private void upload() {
		new Thread(() -> {
			boolean success = ftpUtil.upload(localFile, value -> {
				Config.instance().getActivity().runOnUiThread(() -> {
					textView.setText("上传至" + ftpUtil.getFtpInfo().get("site") + " - " + value + "%");
					progressBar.setProgress(value);
				});
				return true;
			});
			Config.instance().getActivity().runOnUiThread(() -> {
				if (success) {
					Config.instance().getTextTip().show("上传成功");
					progressBar.setVisibility(View.GONE);
					textView.setText("上传至" + ftpUtil.getFtpInfo().get("site") + "，已完成");
				} else {
					Config.instance().getTextTip().show("上传失败");
					progressBar.setVisibility(View.GONE);
					textView.setText("上传至" + ftpUtil.getFtpInfo().get("site") + "失败");
				}
			});
		}).start();
	}

	private void download() {
		new Thread(() -> {
			long start = System.currentTimeMillis();
			ftpUtil.download(remoteFile, localFile.getAbsolutePath() + "/" + remoteFile, value -> {
				Config.instance().getActivity().runOnUiThread(() -> {
					if (value == 100) {
						long during = System.currentTimeMillis() - start;
						int second = (int) during / 1000 % 60;
						int minute = (int) during / 1000 / 60;
						String downloadTime = minute == 0 ? "" + second + "秒" : "" + minute + "分" + second + "秒";
						Config.instance().getTextTip().show("下载成功");
						progressBar.setVisibility(View.GONE);
						textView.setText("下载自" + ftpUtil.getFtpInfo().get("site") + "，以完成，用时" + downloadTime);
					} else if (value < 0 || value > 100) {
						progressBar.setVisibility(View.GONE);
						Config.instance().getTextTip().show("下载失败");
					} else {
						textView.setText("下载自" + ftpUtil.getFtpInfo().get("site") + " - " + value + "%");
						progressBar.setProgress(value);
					}
				});
				return true;
			});
		}).start();
	}

	private void multDowmload() {
		new Thread(() -> {
			long start = System.currentTimeMillis();
			ftpUtil.multDownload(remoteFile, localFile.getAbsolutePath() + "/" + remoteFile, value -> {
				Config.instance().getActivity().runOnUiThread(() -> {
					if (value == 100) {
						long during = System.currentTimeMillis() - start;
						int second = (int) during / 1000 % 60;
						int minute = (int) during / 1000 / 60;
						String downloadTime = minute == 0 ? "" + second + "秒" : "" + minute + "分" + second + "秒";
						Config.instance().getTextTip().show("下载成功");
						progressBar.setVisibility(View.GONE);
						textView.setText("下载自" + ftpUtil.getFtpInfo().get("site") + "，以完成，用时" + downloadTime);
					} else if (value < 0 || value > 100) {
						progressBar.setVisibility(View.GONE);
						Config.instance().getTextTip().show("下载失败");
					} else {
						textView.setText("下载自" + ftpUtil.getFtpInfo().get("site") + " - " + value + "%");
						progressBar.setProgress(value);
					}
				});
				return true;
			});
		}).start();
	}

}
