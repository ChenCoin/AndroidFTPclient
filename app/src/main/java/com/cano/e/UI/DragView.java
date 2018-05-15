package com.cano.e.UI;

import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.cano.e.R;
import com.cano.e.Util.FtpHandle;

/**
 * Created by Cano on 2018/4/25.
 */

public class DragView {

	View view;
	FtpHandle ftpHandle;

	public DragView(View view, FtpHandle ftpHandle) {
		this.view = view;
		this.ftpHandle = ftpHandle;
	}

	RecyclerView ftpFilePage, localFilePage;

	public void bindView() {
		ftpFilePage = (RecyclerView) view.findViewById(R.id.ftpview);
		localFilePage = (RecyclerView) view.findViewById(R.id.localfileview);

		localFilePage.setOnDragListener((view, event) -> {
			if (ftpHandle.isFtpFileDrag()) {
				final int action = event.getAction();
				switch (action) {
					case DragEvent.ACTION_DRAG_STARTED:
						return true;
					case DragEvent.ACTION_DRAG_ENTERED:
						//被拖放View进入目标View
						ftpHandle.showTip("即将下载");
						return true;
					case DragEvent.ACTION_DRAG_LOCATION:
						return true;
					case DragEvent.ACTION_DRAG_EXITED:
						//被拖放View离开目标View
						ftpHandle.showTip("取消下载");
						return true;
					case DragEvent.ACTION_DROP:
						//释放拖放阴影，并获取移动数据
						ftpHandle.showTip("开始下载");
						ftpHandle.download();
						return true;
					case DragEvent.ACTION_DRAG_ENDED:
						//拖放事件完成
						return true;
				}
			}
			return false;
		});

		ftpFilePage.setOnDragListener((view, event) -> {
			if (!ftpHandle.isFtpFileDrag()) {
				final int action = event.getAction();
				switch (action) {
					case DragEvent.ACTION_DRAG_STARTED:
						return true;
					case DragEvent.ACTION_DRAG_ENTERED:
						//被拖放View进入目标View
						ftpHandle.showTip("即将上传");
						return true;
					case DragEvent.ACTION_DRAG_LOCATION:
						return true;
					case DragEvent.ACTION_DRAG_EXITED:
						//被拖放View离开目标View
						ftpHandle.showTip("取消上传");
						return true;
					case DragEvent.ACTION_DROP:
						//释放拖放阴影，并获取移动数据
						ftpHandle.showTip("开始上传");
						ftpHandle.upload();
						return true;
					case DragEvent.ACTION_DRAG_ENDED:
						//拖放事件完成
						return true;
				}
			}
			return false;
		});
	}

}
