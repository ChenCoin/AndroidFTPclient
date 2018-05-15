package com.cano.e.UI;

import android.animation.ObjectAnimator;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.cano.e.Config;
import com.cano.e.R;
import com.cano.e.Util.FtpHandle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Cano on 2018/5/7.
 */

public class FtpPage {
	View view;
	ViewStub viewStub;
	FtpHandle ftpHandle;

	public FtpPage(View page, FtpHandle ftpHandle) {
		this.ftpHandle = ftpHandle;
		viewStub = (ViewStub) page.findViewById(R.id.ftpHome);
		view = viewStub.inflate();

		DragView dragView = new DragView(view, ftpHandle);
		dragView.bindView();

		initView();
	}

	TextView ftpurl, localfileurl, ftpTip;
	RecyclerView ftpview, localfileview;
	ImageButton localFileSwitch;
	LinearLayout localFileTitle;
	ImageButton ftpMenuBtn;

	private void initView() {
		ftpurl = (TextView) view.findViewById(R.id.ftpurl);
		localfileurl = (TextView) view.findViewById(R.id.localfileurl);
		ftpview = (RecyclerView) view.findViewById(R.id.ftpview);
		localfileview = (RecyclerView) view.findViewById(R.id.localfileview);
		ftpTip = (TextView) view.findViewById(R.id.ftpTip);
		localFileSwitch = (ImageButton) view.findViewById(R.id.localFileSwitch);
		localFileSwitch.setOnClickListener(this::switchLocalFile);
		localFileTitle = (LinearLayout) view.findViewById(R.id.localFileTitle);
		ftpMenuBtn = (ImageButton) view.findViewById(R.id.ftpMenuBtn);
		ftpMenuBtn.setOnClickListener(this::showMenu);
	}

	private void showMenu(View view) {
		PopupMenu popupMenu = new PopupMenu(Config.instance().getActivity(), view);
		android.view.Menu menu_more = popupMenu.getMenu();

		String[] menuList = {"新建文件夹", "刷新"};
		Runnable[] menuEvent = {this::createFolder, this::notifyView};

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

	private void notifyView() {
		ftpHandle.bindView();
		ftpHandle.updateLocalFile();
	}

	private void createFolder() {
		ftpHandle.createFolderClicked();
	}

	boolean isLocalFileShowing = false;

	private void switchLocalFile(View view) {
		if (isLocalFileShowing) {
			localfileview.setVisibility(View.GONE);
			localFileSwitch.setImageResource(R.mipmap.up);
			isLocalFileShowing = false;
			localFileTitle.setBackgroundResource(R.drawable.titlebar2);
		} else {
			localfileview.setVisibility(View.VISIBLE);
			localFileSwitch.setImageResource(R.mipmap.down);
			isLocalFileShowing = true;
			localFileTitle.setBackgroundResource(R.drawable.titlebar);
		}
	}

	Timer timer;

	public void setTip(String content) {
		if (timer != null)
			timer.cancel();
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				Config.instance().getActivity().runOnUiThread(() -> {
					ftpTip.setVisibility(View.GONE);
				});
				timer.cancel();
			}
		}, 3000);
		ftpTip.setVisibility(View.VISIBLE);
		ftpTip.setText(content);
	}

	public void setFtpUrl(String path) {
		ftpurl.setText(path);
	}

	public void setFileUrl(String path) {
		localfileurl.setText("本地文件 > " + path);
	}

	Adapter ftpAdapter, fileAdapter;

	public void setFtpList(List<Map<String, Object>> data) {
		showPattern = Config.instance().getShowPattern();
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
		ftpview.setLayoutManager(layoutManage);
		ftpAdapter = new Adapter(data);
		ftpview.setAdapter(ftpAdapter);
	}

	public void setFileList(List<Map<String, Object>> data) {
		showPattern = Config.instance().getShowPattern();
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

		localfileview.setLayoutManager(layoutManage);
		fileAdapter = new Adapter(data);
		localfileview.setAdapter(fileAdapter);
	}

	int showPattern = 0;

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
			switch (showPattern) {
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
			if (showPattern == 3) {
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


}
