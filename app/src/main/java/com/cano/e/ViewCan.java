package com.cano.e;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cano on 2018/5/6.
 */

public class ViewCan {
	List<View> datas = new ArrayList<>();
	List<String> titles = new ArrayList<>();

	Activity activity;

	PagerAdapter myViewPagerAdapter = new PagerAdapter() {
		@Override
		public int getCount() {//返回页卡数量
			return datas.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) { //判断View是否来自Object
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) { //初始化一个页面
			container.addView(datas.get(position));
			return datas.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {//销毁一个页卡
			container.removeView((View) object);
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
	};

	ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			if (position == 0)
				viewPager.setCurrentItem(position + 1);
			else if (position == datas.size() - 1)
				viewPager.setCurrentItem(position - 1);

			if (position != 0 && position != datas.size() - 1) {
				for (int i = 0; i < datas.size() - 2; i++) {
					circlebar.getChildAt(i).setEnabled(false);
				}
				circlebar.getChildAt(position - 1).setEnabled(true);

				pageTitle.setText(titles.get(position));
				if (position == 1 || position == 2) {
					pageClose.setVisibility(View.INVISIBLE);
				} else {
					pageClose.setVisibility(View.VISIBLE);
				}
			}
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}
	};

	ViewPager viewPager;
	LinearLayout circlebar;
	TextView pageTitle;
	ImageButton pageClose;

	public ViewCan() {
		activity = Config.instance().getActivity();
		viewPager = (ViewPager) activity.findViewById(R.id.viewpager);
		pageTitle = (TextView) activity.findViewById(R.id.pageTitle);
		pageClose = (ImageButton) activity.findViewById(R.id.pageClose);
		circlebar = (LinearLayout) activity.findViewById(R.id.circlebar);
	}

	View morePage;

	public void bindView() {
		datas.add(LayoutInflater.from(activity).inflate(R.layout.space, null));
		morePage = LayoutInflater.from(activity).inflate(R.layout.pagemore, null);
		datas.add(morePage);
		datas.add(LayoutInflater.from(activity).inflate(R.layout.space, null));

		titles.add("");
		titles.add((String) activity.getResources().getText(R.string.moreTitle));
		titles.add("");

		for (int i = 0; i < datas.size() - 2; i++) {
			addBarCircle(i);
		}

		viewPager.setAdapter(myViewPagerAdapter);//设置适配器
		viewPager.addOnPageChangeListener(pageChangeListener);
		viewPager.setCurrentItem(2);

		pageClose.setOnClickListener(view -> {
			removePage();
		});
	}

	public View getMorePage() {
		return morePage;
	}

	public void addPage(View view, String title) {
		int item = datas.size() - 1;
		datas.add(item, view);
		titles.add(item, title);
		addBarCircle(1);
		myViewPagerAdapter.notifyDataSetChanged();
		viewPager.setCurrentItem(item);
	}

	private void removePage() {
		int i = viewPager.getCurrentItem();
		View view = datas.get(i);

		AnimatorSet animatorSet = new AnimatorSet();//组合动画
		ObjectAnimator slide = ObjectAnimator.ofFloat(view, "translationY", 0, -1000);
		ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
		animatorSet.setDuration(500);
		animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
		animatorSet.play(slide).with(alpha);
		animatorSet.start();

		new Handler().postDelayed(() -> {
			datas.remove(i);
			titles.remove(i);
			circlebar.removeViewAt(1);
			myViewPagerAdapter.notifyDataSetChanged();
			viewPager.setCurrentItem(i - 1);
		}, 500);
	}

	private void addBarCircle(int i) {
		View view = new View(activity);
		view.setBackgroundResource(R.drawable.circlebar);
		view.setEnabled(false);
		//设置宽高
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(15, 15);
		//设置间隔
		if (i != 0) {
			layoutParams.leftMargin = 24;
		}
		//添加到LinearLayout
		circlebar.addView(view, layoutParams);
	}
}
