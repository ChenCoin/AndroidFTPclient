package com.cano.e.UI;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cano.e.Config;
import com.cano.e.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Cano on 2018/5/7.
 */

public class FtpConfirm {
	View view;
	ViewStub viewStub;

	public FtpConfirm(View page) {
		viewStub = (ViewStub) page.findViewById(R.id.ftpConfirm);
		view = viewStub.inflate();
		bindView();
	}

	LinearLayout confirmPage;
	CheckBox checkBox;
	Button confirmCancel, confirmOK;
	TextView confirmText;

	private void bindView() {
		confirmPage = (LinearLayout) view.findViewById(R.id.confirmPage);
		confirmCancel = (Button) view.findViewById(R.id.confirmCancel);
		confirmOK = (Button) view.findViewById(R.id.confirmOK);
		confirmText = (TextView) view.findViewById(R.id.confirmText);
		checkBox = (CheckBox) view.findViewById(R.id.checkBox);
		confirmCancel.setOnClickListener(view -> {
			hidePage();
		});
	}

	public void show(String content, TransMode event) {
		confirmText.setText(content);
		checkBox.setVisibility(View.VISIBLE);
		confirmOK.setOnClickListener(view -> {
			hidePage();
			event.run(checkBox.isChecked());
		});
		viewStub.setVisibility(View.VISIBLE);
		AnimatorSet animatorSet = new AnimatorSet();//组合动画
		ObjectAnimator alpha = ObjectAnimator.ofFloat(confirmPage, "alpha", 0f, 1f);
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(confirmPage, "scaleX", 0f, 1f);
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(confirmPage, "scaleY", 0f, 1f);
		animatorSet.setDuration(500);
		animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
		animatorSet.play(alpha).with(scaleX).with(scaleY);
		animatorSet.start();
	}

	public void show(String content, Runnable event) {
		confirmText.setText(content);
		checkBox.setVisibility(View.GONE);
		confirmOK.setOnClickListener(view -> {
			hidePage();
			event.run();
		});
		viewStub.setVisibility(View.VISIBLE);
		AnimatorSet animatorSet = new AnimatorSet();//组合动画
		ObjectAnimator alpha = ObjectAnimator.ofFloat(confirmPage, "alpha", 0f, 1f);
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(confirmPage, "scaleX", 0f, 1f);
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(confirmPage, "scaleY", 0f, 1f);
		animatorSet.setDuration(500);
		animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
		animatorSet.play(alpha).with(scaleX).with(scaleY);
		animatorSet.start();
	}

	private void hidePage() {
		AnimatorSet animatorSet = new AnimatorSet();//组合动画
		ObjectAnimator alpha = ObjectAnimator.ofFloat(confirmPage, "alpha", 1f, 0f);
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(confirmPage, "scaleX", 1f, 0f);
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(confirmPage, "scaleY", 1f, 0f);
		animatorSet.setDuration(300);
		animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
		animatorSet.play(alpha).with(scaleX).with(scaleY);
		animatorSet.start();

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				Config.instance().getActivity().runOnUiThread(() -> {
					viewStub.setVisibility(View.INVISIBLE);
				});
				timer.cancel();
			}
		}, 300);
	}

	public interface TransMode {
		public void run(boolean multDownload);
	}
}
