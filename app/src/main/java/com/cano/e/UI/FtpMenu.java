package com.cano.e.UI;

import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.LinearLayout;

import com.cano.e.R;

/**
 * Created by Cano on 2018/5/7.
 */

public class FtpMenu {

	View view;
	ViewStub viewStub;

	public FtpMenu(View page) {
		viewStub = (ViewStub) page.findViewById(R.id.ftpMenu);
		view = viewStub.inflate();

		bindView();
	}

	LinearLayout linearLayout;
	Button menuattr, menurename, menudelete, menudownload, menuupload, menucancel;

	private void bindView() {
		linearLayout = (LinearLayout) view.findViewById(R.id.menuPage);

		menuattr = (Button) view.findViewById(R.id.menuattr);
		menurename = (Button) view.findViewById(R.id.menurename);
		menudelete = (Button) view.findViewById(R.id.menudelete);
		menudownload = (Button) view.findViewById(R.id.menudownload);
		menuupload = (Button) view.findViewById(R.id.menuupload);

		menucancel = (Button) view.findViewById(R.id.menucancel);
		menucancel.setOnClickListener(view -> {
			hidePage();
		});
	}

	/*
	 * menu of ftp dir item , when long clicked
	 */
	public void showDir(Runnable attr, Runnable rename, Runnable delete) {
		menuattr.setVisibility(View.VISIBLE);
		menurename.setVisibility(View.VISIBLE);
		menudelete.setVisibility(View.VISIBLE);
		menudownload.setVisibility(View.GONE);
		menuupload.setVisibility(View.GONE);
		menuattr.setOnClickListener(view -> {
			hidePage();
			attr.run();
		});
		menudelete.setOnClickListener(view -> {
			hidePage();
			delete.run();
		});
		menurename.setOnClickListener(view -> {
			hidePage();
			rename.run();
		});
		showPage();
	}

	/*
	 * menu of ftp file item , when file clicked
	 */
	public void showFtpFile(Runnable attr, Runnable rename, Runnable delete, Runnable download) {
		menuattr.setVisibility(View.VISIBLE);
		menurename.setVisibility(View.VISIBLE);
		menudelete.setVisibility(View.VISIBLE);
		menudownload.setVisibility(View.VISIBLE);
		menuupload.setVisibility(View.GONE);
		menuattr.setOnClickListener(view -> {
			hidePage();
			attr.run();
		});
		menudelete.setOnClickListener(view -> {
			hidePage();
			delete.run();
		});
		menurename.setOnClickListener(view -> {
			hidePage();
			rename.run();
		});
		menudownload.setOnClickListener(view -> {
			hidePage();
			download.run();
		});
		showPage();
	}

	/*
	 * menu of ftp file item , when file clicked
	 */
	public void showLoaclFile(Runnable upload) {
		menuattr.setVisibility(View.GONE);
		menurename.setVisibility(View.GONE);
		menudelete.setVisibility(View.GONE);
		menudownload.setVisibility(View.GONE);
		menuupload.setVisibility(View.VISIBLE);
		menuupload.setOnClickListener(view -> {
			hidePage();
			upload.run();
		});
		showPage();
	}

	private void showPage() {
		viewStub.setVisibility(View.VISIBLE);
	}

	private void hidePage() {
		viewStub.setVisibility(View.INVISIBLE);
	}
}
