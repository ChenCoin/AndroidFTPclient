package com.cano.e.UI;

import android.content.Context;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cano.e.R;

/**
 * Created by Cano on 2018/5/7.
 */

public class FtpRename {
	View view;
	ViewStub ftprename;

	public FtpRename(View page) {
		ftprename = (ViewStub) page.findViewById(R.id.ftpRename);
		view = ftprename.inflate();
		ftprename.setVisibility(View.INVISIBLE);
		bindView();
	}

	TextView renameTitle;
	EditText editText;
	Button editcancel, editconfirm;

	private void bindView() {
		renameTitle = (TextView) view.findViewById(R.id.renameTitle);
		editText = (EditText) view.findViewById(R.id.renametext);
		editcancel = (Button) view.findViewById(R.id.renamecancel);
		editconfirm = (Button) view.findViewById(R.id.renameconfirm);
		editcancel.setOnClickListener(view -> {
			ftprename.setVisibility(View.INVISIBLE);
		});
	}

	public void show(String oldName, SetName set) {
		renameTitle.setText("重命名");
		editText.setText(oldName);
		editconfirm.setOnClickListener(view -> {
			String newName = editText.getText().toString();
			ftprename.setVisibility(View.INVISIBLE);
			set.run(newName);
		});
		ftprename.setVisibility(View.VISIBLE);
	}

	public void show(SetName set) {
		renameTitle.setText("新建文件夹");
		editText.setText("");
		editconfirm.setOnClickListener(view -> {
			String newName = editText.getText().toString();
			ftprename.setVisibility(View.INVISIBLE);
			set.run(newName);
		});
		ftprename.setVisibility(View.VISIBLE);
	}

	public interface SetName {
		void run(String name);
	}
}
