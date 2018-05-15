package com.cano.e.UI;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;

import com.cano.e.Config;
import com.cano.e.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Cano on 2018/5/6.
 */

public class SiteMenu {
	View view;
	ViewStub viewStub;

	public SiteMenu(View page) {
		viewStub = (ViewStub) page.findViewById(R.id.siteMenu);
		view = viewStub.inflate();
		bindView();
	}

	Button menuAttr, menuDelete, menuQRcode, menuCancel;
	LinearLayout menuPage;

	private void bindView() {
		menuAttr = (Button) view.findViewById(R.id.menuAttr);
		menuDelete = (Button) view.findViewById(R.id.menuDelete);
		menuQRcode = (Button) view.findViewById(R.id.menuQRcode);
		menuCancel = (Button) view.findViewById(R.id.menuCancel);
		menuPage = (LinearLayout) view.findViewById(R.id.menuPage);
		menuCancel.setOnClickListener(view -> {
			hidePage();
		});
	}

	public void show(Runnable attr, Runnable delete, Runnable showqrcode) {
		menuAttr.setOnClickListener(view -> {
			hidePage();
			attr.run();
		});
		menuDelete.setOnClickListener(view -> {
			hidePage();
			delete.run();
		});
		menuQRcode.setOnClickListener(view -> {
			hidePage();
			showqrcode.run();
		});
		viewStub.setVisibility(View.VISIBLE);
		AnimatorSet animatorSet = new AnimatorSet();//组合动画
		ObjectAnimator alpha = ObjectAnimator.ofFloat(menuPage, "alpha", 0f, 1f);
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(menuPage, "scaleX", 0f, 1f);
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(menuPage, "scaleY", 0f, 1f);
		animatorSet.setDuration(500);
		animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
		animatorSet.play(alpha).with(scaleX).with(scaleY);
		animatorSet.start();
	}

	private void hidePage() {
		AnimatorSet animatorSet = new AnimatorSet();//组合动画
		ObjectAnimator alpha = ObjectAnimator.ofFloat(menuPage, "alpha", 1f, 0f);
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(menuPage, "scaleX", 1f, 0f);
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(menuPage, "scaleY", 1f, 0f);
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
