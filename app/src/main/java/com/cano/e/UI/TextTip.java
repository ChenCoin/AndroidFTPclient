package com.cano.e.UI;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.cano.e.Config;
import com.cano.e.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Cano on 2018/5/6.
 */

public class TextTip {
	TextView textTip;

	public TextTip() {
		Activity activity = Config.instance().getActivity();
		textTip = (TextView) activity.findViewById(R.id.textTip);
		textTip.setVisibility(View.VISIBLE);
	}

	public void show(String content) {
		textTip.setTextColor(Color.BLACK);
		textTip.setText(content);
		hideText();
	}

	public void show(String context, int color) {
		textTip.setTextColor(color);
		textTip.setText(context);
		hideText();
	}

	Timer timer;

	private void hideText() {
		if (timer != null)
			timer.cancel();
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				Config.instance().getActivity().runOnUiThread(() -> {
					ObjectAnimator.ofFloat(textTip, "alpha", 1f, 0f)
							.setDuration(500)
							.start();
				});
				timer.cancel();
			}
		}, 3000);

		AnimatorSet animatorSet = new AnimatorSet();//组合动画
		ObjectAnimator slide = ObjectAnimator.ofFloat(textTip, "translationY", 100, 0);
		ObjectAnimator alpha = ObjectAnimator.ofFloat(textTip, "alpha", 0f, 1f);
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(textTip, "scaleX", 0f, 1f);
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(textTip, "scaleY", 0f, 1f);
		animatorSet.setDuration(500);
		animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
		animatorSet.play(slide).with(alpha).with(scaleX).with(scaleY);
		animatorSet.start();

	}
}

