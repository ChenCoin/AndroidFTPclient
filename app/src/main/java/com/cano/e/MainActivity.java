package com.cano.e;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.acker.simplezxing.activity.CaptureActivity;
import com.cano.e.Event.MoreEvent;
import com.cano.e.Event.SiteEvent;

public class MainActivity extends AppCompatActivity {

	SiteEvent siteEvent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

		Config config = new Config(this);
		setTheme(config.myTheme[config.getTheme()]);

		setContentView(R.layout.activity_main);


		ViewCan viewCan = new ViewCan();
		siteEvent = new SiteEvent();
		MoreEvent moreEvent = new MoreEvent();

		config.setViewCan(viewCan);
		config.setSiteEvent(siteEvent);
		config.setMoreEvent(moreEvent);

		viewCan.bindView();
		siteEvent.initView();
		moreEvent.initView();

	}

	@Override
	public void onBackPressed() {
		moveTaskToBack(false);
	}

	/*
	 * QrCode result
	 */
	public void startCaptureActivityForResult() {
		Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
		Bundle bundle = new Bundle();
		bundle.putBoolean(CaptureActivity.KEY_NEED_BEEP, CaptureActivity.VALUE_BEEP);
		bundle.putBoolean(CaptureActivity.KEY_NEED_VIBRATION, CaptureActivity.VALUE_VIBRATION);
		bundle.putBoolean(CaptureActivity.KEY_NEED_EXPOSURE, CaptureActivity.VALUE_NO_EXPOSURE);
		bundle.putByte(CaptureActivity.KEY_FLASHLIGHT_MODE, CaptureActivity.VALUE_FLASHLIGHT_OFF);
		bundle.putByte(CaptureActivity.KEY_ORIENTATION_MODE, CaptureActivity.VALUE_ORIENTATION_AUTO);
		bundle.putBoolean(CaptureActivity.KEY_SCAN_AREA_FULL_SCREEN, CaptureActivity.VALUE_SCAN_AREA_FULL_SCREEN);
		bundle.putBoolean(CaptureActivity.KEY_NEED_SCAN_HINT_TEXT, CaptureActivity.VALUE_SCAN_HINT_TEXT);
		intent.putExtra(CaptureActivity.EXTRA_SETTING_BUNDLE, bundle);
		startActivityForResult(intent, CaptureActivity.REQ_CODE);
	}

	public static final int REQ_CODE_PERMISSION = 0x1111;

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case REQ_CODE_PERMISSION: {
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// User agree the permission
					startCaptureActivityForResult();
				} else {
					// User disagree the permission
					Toast.makeText(this, "You must agree the camera permission request before you use the code scan function", Toast.LENGTH_LONG).show();
				}
			}
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case CaptureActivity.REQ_CODE:
				switch (resultCode) {
					case RESULT_OK:
						siteEvent.scanAddSite(data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));
						break;
					case RESULT_CANCELED:
						if (data != null) {
							siteEvent.scanAddSite(data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));
						}
						break;
				}
				break;
		}
	}

}
