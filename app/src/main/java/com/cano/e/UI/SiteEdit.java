package com.cano.e.UI;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cano.e.Config;
import com.cano.e.R;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Cano on 2018/5/6.
 */

public class SiteEdit {
	View view;
	ViewStub viewStub;

	public SiteEdit(View page) {
		viewStub = (ViewStub) page.findViewById(R.id.siteEdit);
		view = viewStub.inflate();
		bindView();
	}

	TextView tilte;
	EditText name, hostname, port, user, passwd;
	Button cancel, confirm;
	Spinner coding;
	LinearLayout editPage;

	String codingString;

	private void bindView() {
		editPage = (LinearLayout)view.findViewById(R.id.editPage);
		tilte = (TextView) view.findViewById(R.id.ftpSiteEditTitle);
		name = (EditText) view.findViewById(R.id.name);
		hostname = (EditText) view.findViewById(R.id.hostname);
		port = (EditText) view.findViewById(R.id.port);
		user = (EditText) view.findViewById(R.id.user);
		passwd = (EditText) view.findViewById(R.id.passwd);
		coding = (Spinner) view.findViewById(R.id.coding);
		coding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				codingString = (Config.instance().getActivity().getResources().getStringArray(R.array.coding))[position];
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		cancel = (Button) view.findViewById(R.id.ftpSiteEditCancel);
		confirm = (Button) view.findViewById(R.id.ftpSiteEditConfirm);
		cancel.setOnClickListener(view -> {
			hidePage();
		});
	}

	// 添加站点
	public void show(ftpSiteResult result) {
		tilte.setText(R.string.addSite);
		name.setText("");
		hostname.setText("");
		port.setText("");
		user.setText("");
		passwd.setText("");
		confirm.setOnClickListener(view -> {
			ContentValues ftpSite = new ContentValues();
			ftpSite.put("name", name.getText().toString());
			ftpSite.put("site", hostname.getText().toString());
			ftpSite.put("port", port.getText().toString());
			ftpSite.put("coding", codingString);
			ftpSite.put("user", user.getText().toString());
			ftpSite.put("password", passwd.getText().toString());
			hidePage();
			result.send(ftpSite);
		});
		showPage();
	}

	// 二维码的添加站点
	public void show(ContentValues site, ftpSiteResult result) {
		tilte.setText(R.string.addSite);
		name.setText(site.get("name").toString());
		hostname.setText(site.get("site").toString());
		port.setText(site.get("port").toString());
		coding.setSelection(0);
		user.setText(site.get("user").toString());
		passwd.setText(site.get("password").toString());
		confirm.setOnClickListener(view -> {
			ContentValues ftpSite = new ContentValues();
			ftpSite.put("name", name.getText().toString());
			ftpSite.put("site", hostname.getText().toString());
			ftpSite.put("port", port.getText().toString());
			ftpSite.put("coding", codingString);
			ftpSite.put("user", user.getText().toString());
			ftpSite.put("password", passwd.getText().toString());
			hidePage();
			result.send(ftpSite);
		});
		showPage();
	}

	// 编辑站点
	public void show(Map site, ftpSiteResult result) {
		tilte.setText(R.string.editSite);
		name.setText(site.get("name").toString());
		hostname.setText(site.get("site").toString());
		port.setText(site.get("port").toString());
		int item = 0;
		String[] codings = Config.instance().getActivity().getResources().getStringArray(R.array.coding);
		codingString = site.get("coding").toString();
		for(String c : codings){
			if(c.equals(codingString)){
				break;
			}
			item++;
		}
		coding.setSelection(item);
		user.setText(site.get("user").toString());
		passwd.setText(site.get("password").toString());
		confirm.setOnClickListener(view -> {
			ContentValues ftpSite = new ContentValues();
			ftpSite.put("name", name.getText().toString());
			ftpSite.put("site", hostname.getText().toString());
			ftpSite.put("port", port.getText().toString());
			ftpSite.put("coding", codingString);
			ftpSite.put("user", user.getText().toString());
			ftpSite.put("password", passwd.getText().toString());
			hidePage();
			result.send(ftpSite);
		});
		showPage();
	}

	private void showPage() {
		viewStub.setVisibility(View.VISIBLE);
		AnimatorSet animatorSet = new AnimatorSet();//组合动画
		ObjectAnimator alpha = ObjectAnimator.ofFloat(editPage, "alpha", 0f, 1f);
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(editPage, "scaleX", 0f, 1f);
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(editPage, "scaleY", 0f, 1f);
		animatorSet.setDuration(500);
		animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
		animatorSet.play(alpha).with(scaleX).with(scaleY);
		animatorSet.start();
	}

	private void hidePage() {
		AnimatorSet animatorSet = new AnimatorSet();//组合动画
		ObjectAnimator alpha = ObjectAnimator.ofFloat(editPage, "alpha", 1f, 0f);
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(editPage, "scaleX", 1f, 0f);
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(editPage, "scaleY", 1f, 0f);
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

	public interface ftpSiteResult {
		void send(ContentValues values);
	}

}
