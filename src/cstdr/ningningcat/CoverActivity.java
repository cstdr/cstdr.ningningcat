package cstdr.ningningcat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.umeng.analytics.MobclickAgent;

/**
 * 封面页
 * @author cstdingran@gmail.com
 */
public class CoverActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cover);
        LinearLayout cover=(LinearLayout)findViewById(R.id.ll_cover);
        cover.setBackgroundResource(R.drawable.cover);
        Animation animCover=AnimationUtils.loadAnimation(this, android.R.anim.fade_in); // 简单的渐进动画效果，显示更平滑
        cover.setAnimation(animCover);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent=new Intent(CoverActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.cover_put_in, R.anim.cover_put_out); // TODO
                finish();
            }
        }, 2000);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
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
