package com.cano.e.UI;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cano.e.Config;
import com.cano.e.MainActivity;
import com.cano.e.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Cano on 2018/5/6.
 */

public class SitePage {

	View view;
	ViewStub viewStub;

	public SitePage(View page) {
		viewStub = (ViewStub) page.findViewById(R.id.siteHome);
		view = viewStub.inflate();
		recyclerView = (RecyclerView) view.findViewById(R.id.siteList);
		siteNum = (TextView) view.findViewById(R.id.siteNum);
	}

	RecyclerView recyclerView;
	Adapter adapter;
	TextView siteNum;
	List data;
	Button scanBtn;

	public void initView(List data) {
		this.data = data;
		GridLayoutManager layoutManage = new GridLayoutManager(Config.instance().getActivity(), 5);
		recyclerView.setLayoutManager(layoutManage);
		adapter = new Adapter(data);
		recyclerView.setAdapter(adapter);
		siteNum.setText("共" + (data.size() - 1) + "个FTP站点");
		scanBtn = (Button) view.findViewById(R.id.scanBtn);
		scanBtn.setOnClickListener(view -> {
			MainActivity activity = Config.instance().getActivity();
			if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
				// Do not have the permission of camera, request it.
				ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, activity.REQ_CODE_PERMISSION);
			} else {
				// Have gotten the permission
				activity.startCaptureActivityForResult();
			}
		});
	}

	public void notifyUI() {
		adapter.notifyDataSetChanged();
		siteNum.setText("共" + (data.size() - 1) + "个FTP站点");
	}

	class Adapter extends RecyclerView.Adapter<ViewHolder> {
		List<Map<String, Object>> data;

		public Adapter(List<Map<String, Object>> data) {
			this.data = data;
		}

		@NonNull
		@Override
		public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.griditem, parent, false);
			ViewHolder viewHolder = new ViewHolder(v);
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
		}

		@Override
		public int getItemCount() {
			return data == null ? 0 : data.size();
		}
	}

	class ViewHolder extends RecyclerView.ViewHolder {

		TextView textView;
		ImageView imageView;

		public ViewHolder(View itemView) {
			super(itemView);
			textView = (TextView) itemView.findViewById(R.id.textView);
			imageView = (ImageView) itemView.findViewById(R.id.imageView);
		}
	}
}
