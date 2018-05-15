package com.cano.e.UI;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cano.e.Config;
import com.cano.e.Event.TransEvent;
import com.cano.e.R;
import com.cano.e.Util.TransItem;

import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Cano on 2018/5/7.
 */

public class TransList {
	View view;
	Context context;
	TransEvent transEvent;

	RecyclerView translist;
	Adapter adapter;

	List<TransItem> data;

	public TransList(View view, TransEvent transEvent) {
		this.context = Config.instance().getActivity();
		this.transEvent = transEvent;
		this.view = view;

		translist = (RecyclerView) view.findViewById(R.id.transRecycler);
		adapter = new Adapter();
		initList();
	}

	private void initList() {
		data = new ArrayList<>();
		LinearLayoutManager layoutManager = new LinearLayoutManager(context);
		//设置布局管理器
		translist.setLayoutManager(layoutManager);
		//设置为垂直布局，这也是默认的
		layoutManager.setOrientation(OrientationHelper.VERTICAL);
		//设置Adapter
		translist.setAdapter(adapter);
		//设置增加或删除条目的动画
		translist.setItemAnimator(new DefaultItemAnimator());
	}

	public void addTranslate(TransItem item) {
		data.add(item);
		adapter.notifyDataSetChanged();
	}

	public interface Callback {
		void run(Map map);
	}

	public void setProgressbar(ProgressBar transprogress, int i) {
		transprogress.setProgress(i);
	}

	public void removeMap(Map<String, Object> map) {
		data.remove(map);
		adapter.notifyDataSetChanged();
	}

	class Adapter extends RecyclerView.Adapter<ViewHolder> {

		@NonNull
		@Override
		public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.transitem, parent, false);
			ViewHolder viewHolder = new ViewHolder(v);
			return viewHolder;
		}

		@Override
		public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
			TransItem item = data.get(position);
			if(!item.isStarted) {
				holder.name.setText(item.name);
				holder.text.setText(item.text);
				holder.transIcon.setImageResource(item.icon);
				holder.transBtn.setOnClickListener(view -> {
					item.control.run();
				});

				item.textView = holder.text;
				item.progressBar = holder.transProgress;

				item.start();
			}
		}

		@Override
		public int getItemCount() {
			return data == null ? 0 : data.size();
		}
	}

	class ViewHolder extends RecyclerView.ViewHolder {

		ImageView transIcon;
		TextView name, text;
		ProgressBar transProgress;
		ImageButton transBtn;

		public ViewHolder(View itemView) {
			super(itemView);
			transIcon = (ImageView) itemView.findViewById(R.id.transIcon);
			name = (TextView) itemView.findViewById(R.id.transText);
			text = (TextView) itemView.findViewById(R.id.transText2);
			transProgress = (ProgressBar) itemView.findViewById(R.id.transprogress);
			transBtn = (ImageButton) itemView.findViewById(R.id.transBtn);
		}
	}
}
