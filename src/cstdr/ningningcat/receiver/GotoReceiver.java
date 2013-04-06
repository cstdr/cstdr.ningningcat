package cstdr.ningningcat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cstdr.ningningcat.WebActivity;

/**
 * 页面跳转Receiver
 * 
 * @author cstdingran@gmail.com
 */
public class GotoReceiver extends BroadcastReceiver {

	public static final String ACTION_GOTO = "cstdr.ningningcat.ACTION_GOTO";

	@Override
	public void onReceive(Context context, Intent intent) {
		intent.setClass(context, WebActivity.class);
		context.startActivity(intent);
	}

}
