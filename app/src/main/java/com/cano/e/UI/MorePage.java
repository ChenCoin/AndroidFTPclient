package com.cano.e.UI;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;

import com.cano.e.Config;
import com.cano.e.R;
import com.cano.e.ViewCan;

/**
 * Created by Cano on 2018/5/6.
 */

public class MorePage {
	View view;

	Switch folderFirst, showAllFile;
	Spinner sortMode, showMode, themeBtn;

	public void bindView() {
		view = Config.instance().getViewCan().getMorePage();

		folderFirst = (Switch) view.findViewById(R.id.folderFirst);
		folderFirst.setChecked(Config.instance().isFolderFirst());
		folderFirst.setOnCheckedChangeListener((view, isChecked) -> {
			Config.instance().setFolderFirst(isChecked);
		});

		showAllFile = (Switch) view.findViewById(R.id.showAllFile);
		showAllFile.setChecked(Config.instance().isShowAllFile());
		showAllFile.setOnCheckedChangeListener((view, isChecked) -> {
			Config.instance().setShowAllFile(isChecked);
		});

		sortMode = (Spinner) view.findViewById(R.id.sortMode);
		sortMode.setSelection(Config.instance().getSortPattern());
		sortMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
									   int position, long id) {
				Config.instance().setSortPattern(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		showMode = (Spinner) view.findViewById(R.id.showMode);
		showMode.setSelection(Config.instance().getShowPattern());
		showMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
									   int position, long id) {
				Config.instance().setShowPattern(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		themeBtn = (Spinner) view.findViewById(R.id.themeBtn);
		themeBtn.setSelection(Config.instance().getTheme());
		themeBtn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
									   int position, long id) {
				Config config = Config.instance();
				if (position != config.getTheme()) {
					config.setTheme(position);
					config.getActivity().recreate();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		Button localFile = (Button) view.findViewById(R.id.showLocalFile);
		localFile.setOnClickListener(view -> {
			new LocalFilePage().show();
		});

	}

}
