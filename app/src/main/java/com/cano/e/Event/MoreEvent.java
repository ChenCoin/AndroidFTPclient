package com.cano.e.Event;

import com.cano.e.Config;
import com.cano.e.UI.MorePage;

/**
 * Created by Cano on 2018/5/6.
 */

public class MoreEvent {

	public void initView() {
		MorePage morePage = new MorePage();
		morePage.bindView();
	}

	TransEvent transEvent;

	public TransEvent getTransEvent() {
		if (transEvent == null) transEvent = new TransEvent();
		return transEvent;
	}

}
