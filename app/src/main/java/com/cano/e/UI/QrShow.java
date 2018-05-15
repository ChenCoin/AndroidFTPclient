package com.cano.e.UI;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cano.e.Config;
import com.cano.e.MainActivity;
import com.cano.e.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Hashtable;

/**
 * Created by Cano on 2018/4/25.
 */

public class QrShow {

	View view;

	public QrShow(View view ) {
		this.view = view;
		bindView();
	}

	TextView title;
	ImageView image;
	Button button;

	Bitmap bitmapQRCode;

	private void bindView() {
		title = (TextView) view.findViewById(R.id.ftpQrTitle);
		image = (ImageView) view.findViewById(R.id.ftpQrImage);
		button = (Button) view.findViewById(R.id.ftpQrClose);
		button.setOnClickListener(view -> {
			MainActivity activity = Config.instance().getActivity();
			MediaStore.Images.Media.insertImage(activity.getContentResolver(), bitmapQRCode, "QRcode", "QRcode of FTP site");
			Toast.makeText(activity, "保存成功", Toast.LENGTH_LONG).show();
		});
	}

	public void show(String qrtitle, String site) {
		title.setText(qrtitle);
		try {
			bitmapQRCode = createQRCode(site, 300);
			image.setImageBitmap(bitmapQRCode);
		} catch (Exception e) {
			title.setText("生成二维码失败");
		}
	}

	Bitmap createQRCode(String str, int widthAndHeight)
			throws WriterException {
		final int BLACK = 0xff000000;
		final int WHITE = 0xffffffff;
		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		BitMatrix matrix = new MultiFormatWriter().encode(str,
				BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight);
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (matrix.get(x, y)) {
					pixels[y * width + x] = BLACK;
				} else {
					pixels[y * width + x] = WHITE;
				}
			}
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

}
