package cstdr.ningningcat.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

import com.umeng.analytics.MobclickAgent;

import cstdr.ningningcat.R;
import cstdr.ningningcat.ui.widget.layout.CoverLayout;
import cstdr.ningningcat.util.UIUtil;

/**
 * 封面页（暂时没有用到）
 * 
 * @author cstdingran@gmail.com
 */
public class CoverActivity extends Activity {

	// private static final String TAG = "CoverActivity";

	private CoverLayout coverLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		UIUtil.initUI(CoverActivity.this);
		coverLayout = new CoverLayout(this);
		setContentView(coverLayout);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(CoverActivity.this,
						WebActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.cover_put_in,
						R.anim.cover_put_out); // TODO
				finish();
			}
		}, 2000);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
}
