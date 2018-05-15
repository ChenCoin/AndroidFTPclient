package com.cano.e.Util;

import android.os.Environment;
import android.view.View;

import com.cano.e.Config;
import com.cano.e.Event.FtpEvent;
import com.cano.e.Model.FileIcon;
import com.cano.e.Model.FtpUtil;
import com.cano.e.R;

import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Cano on 2018/5/7.
 */

public class FtpData {

	FtpUtil ftpUtil;
	FtpEvent ftpEvent;
	FtpHandle ftpHandle;

	public FtpData(FtpUtil ftpUtil, FtpEvent ftpEvent, FtpHandle ftpHandle) {
		this.ftpUtil = ftpUtil;
		this.ftpEvent = ftpEvent;
		this.ftpHandle = ftpHandle;
	}

	public List<Map<String, Object>> ftpFileList() {

		List<Map<String, Object>> data = new ArrayList<>();
		try {
			//remotePath = ftpUtil.getWorkingDir();
			FTPFile[] originalList = ftpUtil.getList();
			List<FTPFile> arrayList = new ArrayList<FTPFile>(Arrays.asList(originalList));

			// 显示隐藏文件夹与否
			boolean isShowAllFile = Config.instance().isShowAllFile();
			if (!isShowAllFile) {
				Iterator<FTPFile> iter = arrayList.iterator();
				while (iter.hasNext()) {
					String item = iter.next().getName();
					if (item.substring(0, 1).equals(".")) {
						iter.remove();
					}
				}
			}

			// 排序模式
			int sortPattern = Config.instance().getSortPattern();
			switch (sortPattern) {
				case Config.sortDate:
					Collections.sort(arrayList, (file1, file2) -> {
						return file1.getTimestamp().compareTo(file2.getTimestamp());
					});
					break;
				case Config.sortType:
					Collections.sort(arrayList, (file1, file2) -> {
						String suffix1 = file1.getName().substring(file1.getName().lastIndexOf(".") + 1).toUpperCase();
						String suffix2 = file2.getName().substring(file2.getName().lastIndexOf(".") + 1).toUpperCase();
						int result = suffix1.compareTo(suffix2);
						if (result == 0) {
							result = file1.getName().compareTo(file2.getName());
						}
						return result;
					});
					break;
				case Config.sortSize:
					Collections.sort(arrayList, (file1, file2) -> {
						Long size1 = file1.getSize();
						Long size2 = file2.getSize();
						return size1.compareTo(size2);
					});
					break;
				case Config.sortName:
					Collections.sort(arrayList, (file1, file2) -> {
						return file1.getName().compareTo(file2.getName());
					});
			}

			// 文件夹优先排列
			boolean isFolderFirst = Config.instance().isFolderFirst();
			if (isFolderFirst) {
				Collections.sort(arrayList, (file1, file2) -> {
					if (file1.isDirectory() && !file2.isDirectory()) {
						return -1;
					} else if (!file1.isDirectory() && file2.isDirectory()) {
						return 1;
					} else {
						return 0;
					}
				});
			}

			FTPFile[] list = new FTPFile[arrayList.size()];
			arrayList.toArray(list);

			for (int i = 0; i < list.length; i++) {
				FTPFile file = list[i];
				if (file.getName().equals(".")) continue;
				Map<String, Object> map = new HashMap<>();
				map.put("text", file.getName());

				map.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(file.getTimestamp().getTime()));
				map.put("size", convertFileSize(file.getSize()));

				if (file.isDirectory()) {
					// 经典主题时，更换图标
					if (Config.instance().getTheme() == 0)
						map.put("image", R.mipmap.class_folder);
					else
						map.put("image", R.mipmap.ic_folder_black_48dp);
					map.put("click", (View.OnClickListener) view -> {
						ftpDirClicked(file.getName());
					});
					map.put("longclick", (View.OnLongClickListener) view -> {
						ftpEvent.dirLongClicked(file);
						return true;
					});
				} else {
					map.put("image", FileIcon.getIcon(file.getName()));
					map.put("click", (View.OnClickListener) view -> {
						ftpEvent.fileClicked(file);
					});
					map.put("longclick", (View.OnLongClickListener) view -> {
						ftpEvent.fileLongClicked(file);
						view.startDrag(null, new View.DragShadowBuilder(view), null, 0);
						return true;
					});
				}
				data.add(map);
			}

			String path = ftpUtil.getPath();
			if (!path.equals("/")) {
				if (!data.get(0).get("text").equals("..")) {
					int last = path.lastIndexOf("/");
					if (last == 0) last = 1;
					String parentPath = path.substring(0, last);
					Map<String, Object> map = new HashMap<>();
					map.put("text", "..");

					map.put("date", "");
					map.put("size", "");

					// 经典主题时，更换图标
					if (Config.instance().getTheme() == 0)
						map.put("image", R.mipmap.class_folder);
					else
						map.put("image", R.mipmap.ic_folder_black_48dp);

					map.put("click", (View.OnClickListener) view -> {
						ftpDirClicked(parentPath);
					});
					map.put("longclick", (View.OnLongClickListener) view -> {
						return true;
					});
					data.add(0, map);
				}
			}

			String url = ftpUtil.getFtpUrl();
			Config.instance().getActivity().runOnUiThread(() -> {
				ftpHandle.setFtpUrl(url);
			});
		} catch (Exception e) {
			return null;
		}
		return data;
	}

	private void ftpDirClicked(String file) {
		new Thread(() -> {
			ftpUtil.changePath(file);
			Config.instance().getActivity().runOnUiThread(() -> {
				ftpHandle.bindView();
			});
		}).start();
	}

	// local file system

	File currentFile = Environment.getExternalStorageDirectory();

	private void LocalDirClicked(File file) {
		currentFile = file;
		ftpHandle.setLocalFileList(localFileList(file));
	}

	public void updateLocalFile() {
		ftpHandle.setLocalFileList(localFileList(currentFile));
	}

	private List<Map<String, Object>> localFileList(File dir) {
		int length = Environment.getExternalStorageDirectory().getAbsolutePath().length();
		String url = dir.getAbsolutePath();
		if (url.length() > length) {
			String fileUrl = url.substring(length + 1).replace("/", " > ");
			ftpHandle.setLocalFileUrl(fileUrl);
		} else {
			ftpHandle.setLocalFileUrl("");
		}

		List<Map<String, Object>> fileList = new ArrayList<>();
		if (!isHome()) {
			Map<String, Object> back = new HashMap<>();
			back.put("text", "..");

			back.put("date", "");
			back.put("size", "");

			// 经典主题时，更换图标
			if (Config.instance().getTheme() == 0)
				back.put("image", R.mipmap.class_folder);
			else
				back.put("image", R.mipmap.ic_folder_black_48dp);

			back.put("click", (View.OnClickListener) view -> {
				backDir();
				String path = dir.getAbsolutePath();
				path = path.substring(0, path.lastIndexOf("/"));
				LocalDirClicked(new File(path));
			});
			back.put("longclick", (View.OnLongClickListener) view -> {
				return true;
			});
			fileList.add(back);
		}

		List<File> arrayList = new ArrayList<File>(Arrays.asList(dir.listFiles()));

		// 显示隐藏文件夹与否
		boolean isShowAllFile = Config.instance().isShowAllFile();
		if (!isShowAllFile) {
			Iterator<File> iter = arrayList.iterator();
			while (iter.hasNext()) {
				String item = iter.next().getName();
				if (item.substring(0, 1).equals(".")) {
					iter.remove();
				}
			}
		}

		// 排序模式
		int sortPattern = Config.instance().getSortPattern();
		switch (sortPattern) {
			case Config.sortDate:
				Collections.sort(arrayList, (file1, file2) -> {
					Long time1 = file1.lastModified();
					Long time2 = file2.lastModified();
					return time1.compareTo(time2);
				});
				break;
			case Config.sortType:
				Collections.sort(arrayList, (file1, file2) -> {
					String suffix1 = file1.getName().substring(file1.getName().lastIndexOf(".") + 1).toUpperCase();
					String suffix2 = file2.getName().substring(file2.getName().lastIndexOf(".") + 1).toUpperCase();
					int result = suffix1.compareTo(suffix2);
					if (result == 0) {
						result = file1.getName().compareTo(file2.getName());
					}
					return result;
				});
				break;
			case Config.sortSize:
				Collections.sort(arrayList, (file1, file2) -> {
					Long size1 = file1.length();
					Long size2 = file2.length();
					return size1.compareTo(size2);
				});
				break;
			case Config.sortName:
			default:
				Collections.sort(arrayList, (file1, file2) -> {
					return file1.getName().compareTo(file2.getName());
				});
		}

		// 文件夹优先排列
		boolean isFolderFirst = Config.instance().isFolderFirst();
		if (isFolderFirst) {
			Collections.sort(arrayList, (file1, file2) -> {
				if (file1.isDirectory() && !file2.isDirectory()) {
					return -1;
				} else if (!file1.isDirectory() && file2.isDirectory()) {
					return 1;
				} else {
					return 0;
				}
			});
		}

		File[] files = new File[arrayList.size()];
		arrayList.toArray(files);

		for (File file : files) {
			if (file.isDirectory()) {
				Map<String, Object> map = new HashMap<>();
				map.put("text", file.getName());

				map.put("date", new SimpleDateFormat("yyyy-MM-dd  HH:mm").format(file.lastModified()));
				map.put("size", convertFileSize(file.length()));

				// 经典主题时，更换图标
				if (Config.instance().getTheme() == 0)
					map.put("image", R.mipmap.class_folder);
				else
					map.put("image", R.mipmap.ic_folder_black_48dp);

				map.put("click", (View.OnClickListener) view -> {
					intoDir(file.getName());
					LocalDirClicked(file);
				});
				map.put("longclick", (View.OnLongClickListener) view -> {
					return true;
				});
				fileList.add(map);
			} else {
				Map<String, Object> map = new HashMap<>();
				map.put("text", file.getName());

				map.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(file.lastModified()));
				map.put("size", convertFileSize(file.length()));

				map.put("image", FileIcon.getIcon(file.getName()));
				map.put("click", (View.OnClickListener) view -> {
					ftpHandle.fileClicked(file);
				});
				map.put("longclick", (View.OnLongClickListener) view -> {
					ftpHandle.fileLongClicked(file);
					view.startDrag(null, new View.DragShadowBuilder(view), null, 0);
					return true;
				});
				fileList.add(map);
			}
		}
		return fileList;
	}

	int home = 0;

	private void intoDir(String name) {
		home++;
	}

	private void backDir() {
		home--;
	}

	private boolean isHome() {
		return home == 0;
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
