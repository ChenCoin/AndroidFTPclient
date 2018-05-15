package com.cano.e.UI;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.cano.e.Config;
import com.cano.e.Model.FileIcon;
import com.cano.e.R;
import com.cano.e.ViewCan;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Cano on 2018/5/14.
 */

public class LocalFilePage {

	View view;
	TextView localFileUrl;
	ImageButton localFileMenu;
	RecyclerView localFileView;

	public void show() {
		view = LayoutInflater.from(Config.instance().getActivity()).inflate(R.layout.pagefile, null);
		ViewCan viewCan = Config.instance().getViewCan();
		viewCan.addPage(view, "本地文件");

		localFileUrl = (TextView) view.findViewById(R.id.localFileUrl);
		localFileMenu = (ImageButton) view.findViewById(R.id.localFileMenu);
		localFileMenu.setOnClickListener(this::menuClicked);
		localFileView = (RecyclerView) view.findViewById(R.id.localFileView);

		initView();
	}

	private void initView() {
		bindView(fileData(currentFile));
	}

	// 文件被点击或长按，菜单被点击
	private void dirLongClicked(File file, View view) {
		PopupMenu popupMenu = new PopupMenu(Config.instance().getActivity(), view);
		android.view.Menu menu_more = popupMenu.getMenu();

		String[] menuList = {"复制", "删除", "重命名", "移动", "属性"};
		Runnable[] menuEvent = {this::copy, this::delete, this::rename, this::move, this::attr};

		for (int i = 0; i < menuList.length; i++) {
			menu_more.add(android.view.Menu.NONE, android.view.Menu.FIRST + i, i, menuList[i]);
		}

		popupMenu.setOnMenuItemClickListener(item -> {
			int i = item.getItemId();
			menuEvent[i - 1].run();
			return true;
		});

		popupMenu.show();
	}

	File fileClicked;

	private void fileClicked(File file, View view) {
		fileClicked = file;

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//设置intent的Action属性
		intent.setAction(Intent.ACTION_VIEW);
		//获取文件file的MIME类型
		String type = FileIcon.getMIMEType(file);
		//设置intent的data和Type属性。
		intent.setDataAndType(Uri.fromFile(file), type);
		//跳转
		try {
			Config.instance().getActivity().startActivity(intent);
		} catch (Exception e) {
			Config.instance().getTextTip().show("找不到打开此文件的应用");
		}
	}

	private void fileLongClicked(File file, View view) {
		fileClicked = file;

		PopupMenu popupMenu = new PopupMenu(Config.instance().getActivity(), view);
		android.view.Menu menu_more = popupMenu.getMenu();

		String[] menuList = {"复制", "删除", "重命名", "移动", "属性"};
		Runnable[] menuEvent = {this::copy, this::delete, this::rename, this::move, this::attr};

		for (int i = 0; i < menuList.length; i++) {
			menu_more.add(android.view.Menu.NONE, android.view.Menu.FIRST + i, i, menuList[i]);
		}

		popupMenu.setOnMenuItemClickListener(item -> {
			int i = item.getItemId();
			menuEvent[i - 1].run();
			return true;
		});

		popupMenu.show();
	}

	private void menuClicked(View view) {
		PopupMenu popupMenu = new PopupMenu(Config.instance().getActivity(), view);
		android.view.Menu menu_more = popupMenu.getMenu();

		String[] menuList = {"新建文件夹", "刷新"};
		Runnable[] menuEvent = {this::createFile, this::initView};

		for (int i = 0; i < menuList.length; i++) {
			menu_more.add(android.view.Menu.NONE, android.view.Menu.FIRST + i, i, menuList[i]);
		}

		popupMenu.setOnMenuItemClickListener(item -> {
			int i = item.getItemId();
			menuEvent[i - 1].run();
			return true;
		});

		popupMenu.show();
	}

	// 文件操作
	private void delete() {
		fileClicked.delete();
	}

	private void rename() {
	}

	private void move() {
	}

	private void copy() {
	}

	private void attr() {
	}

	private void createFile() {
	}

	// 设置界面上的文件路径
	private void setUrl(String url) {
		localFileUrl.setText(url);
	}

	Adapter adapter;

	private void bindView(List<Map<String, Object>> data) {
		int showPattern = Config.instance().getShowPattern();
		RecyclerView.LayoutManager layoutManage;
		switch (showPattern) {
			case 0:
				layoutManage = new GridLayoutManager(Config.instance().getActivity(), 5);
				break;
			case 1:
				layoutManage = new GridLayoutManager(Config.instance().getActivity(), 3);
				break;
			case 2:
			case 3:
				layoutManage = new LinearLayoutManager(Config.instance().getActivity());
				break;
			default:
				layoutManage = new GridLayoutManager(Config.instance().getActivity(), 5);
		}

		localFileView.setLayoutManager(layoutManage);
		adapter = new Adapter(data);
		localFileView.setAdapter(adapter);

	}

	// adapter and holder

	class Adapter extends RecyclerView.Adapter<ViewHolder> {
		List<Map<String, Object>> data;

		public Adapter(List<Map<String, Object>> data) {
			this.data = data;
		}

		@NonNull
		@Override
		public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			View view;
			switch (Config.instance().getShowPattern()) {
				case 0:
				case 1:
					view = LayoutInflater.from(parent.getContext()).inflate(R.layout.griditem, parent, false);
					break;
				case 2:
				case 3:
					view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem, parent, false);
					break;
				default:
					view = LayoutInflater.from(parent.getContext()).inflate(R.layout.griditem, parent, false);
			}
			ViewHolder viewHolder = new ViewHolder(view);
			return viewHolder;
		}

		@Override
		public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
			holder.textView.setText(data.get(position).get("text").toString());
			holder.imageView.setImageResource((int) data.get(position).get("image"));

			data.get(position).put("view", holder.imageView);

			int theme = Config.instance().getTheme();
			if (theme != 0) {
				int themeColor;
				switch (theme) {
					case 1:
						themeColor = R.color.blue;
						break;
					case 2:
						themeColor = R.color.green;
						break;
					case 3:
					default:
						themeColor = R.color.black;
						break;
				}
				holder.imageView.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(Config.instance().getActivity(), themeColor)));
			}

			holder.itemView.setOnClickListener((View.OnClickListener) data.get(position).get("click"));
			holder.itemView.setOnLongClickListener((View.OnLongClickListener) data.get(position).get("longclick"));
			if (Config.instance().getShowPattern() == 3) {
				holder.fileDate.setText(data.get(position).get("date").toString());
				holder.fileSize.setText(data.get(position).get("size").toString());
			}
		}

		@Override
		public int getItemCount() {
			return data == null ? 0 : data.size();
		}
	}

	class ViewHolder extends RecyclerView.ViewHolder {

		TextView textView, fileDate, fileSize;
		ImageView imageView;

		public ViewHolder(View itemView) {
			super(itemView);
			textView = (TextView) itemView.findViewById(R.id.textView);
			imageView = (ImageView) itemView.findViewById(R.id.imageView);
			fileDate = (TextView) itemView.findViewById(R.id.filedate);
			fileSize = (TextView) itemView.findViewById(R.id.filesize);
		}
	}


	// file data

	File currentFile = Environment.getExternalStorageDirectory();

	private void LocalDirClicked(File file) {
		currentFile = file;
		bindView(fileData(file));
	}

	private List<Map<String, Object>> fileData(File dir) {
		int length = Environment.getExternalStorageDirectory().getAbsolutePath().length();
		String url = dir.getAbsolutePath();
		if (url.length() > length) {
			String fileUrl = "本地文件 > " + url.substring(length + 1).replace("/", " > ");
			setUrl(fileUrl);
		} else {
			setUrl("本地文件 > ");
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
					dirLongClicked(file, (View) map.get("view"));
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
					fileClicked(file, (View) map.get("view"));
				});
				map.put("longclick", (View.OnLongClickListener) view -> {
					fileLongClicked(file, (View) map.get("view"));
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
