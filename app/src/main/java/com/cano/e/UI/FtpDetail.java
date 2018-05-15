package com.cano.e.UI;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.text.Html;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cano.e.Config;
import com.cano.e.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Cano on 2018/5/14.
 */

public class FtpDetail {

	View view;
	ViewStub viewStub;

	public FtpDetail(View page) {
		viewStub = (ViewStub) page.findViewById(R.id.ftpDetail);
		view = viewStub.inflate();
		bindView();
	}

	TextView detailContent;
	Button detailHide;
	LinearLayout detailPage;

	private void bindView() {
		detailContent = (TextView) view.findViewById(R.id.detailContent);
		detailHide = (Button) view.findViewById(R.id.detailHide);
		detailPage = (LinearLayout) view.findViewById(R.id.detailPage);
		detailHide.setOnClickListener(view -> {
			hidePage();
		});
	}

	public void show(String content) {
		detailContent.setText(content);
		viewStub.setVisibility(View.VISIBLE);
		AnimatorSet animatorSet = new AnimatorSet();//组合动画
		ObjectAnimator alpha = ObjectAnimator.ofFloat(detailPage, "alpha", 0f, 1f);
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(detailPage, "scaleX", 0f, 1f);
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(detailPage, "scaleY", 0f, 1f);
		animatorSet.setDuration(500);
		animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
		animatorSet.play(alpha).with(scaleX).with(scaleY);
		animatorSet.start();
	}

	private void hidePage() {
		AnimatorSet animatorSet = new AnimatorSet();//组合动画
		ObjectAnimator alpha = ObjectAnimator.ofFloat(detailPage, "alpha", 1f, 0f);
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(detailPage, "scaleX", 1f, 0f);
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(detailPage, "scaleY", 1f, 0f);
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
}
