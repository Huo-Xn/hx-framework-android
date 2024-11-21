package com.hx.empty.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.hx.empty.R;

public class SplashActivity extends AppCompatActivity {
    /**
     * 默认启动页过渡时间
     */
    private static final int DEFAULT_SPLASH_DURATION_MILLIS = 500;

    protected LinearLayout mWelcomeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initSplashView(getSplashImgResId());
        onCreateActivity();
    }

    private void initView() {
        mWelcomeLayout = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mWelcomeLayout.setLayoutParams(params);
        mWelcomeLayout.setOrientation(LinearLayout.VERTICAL);
        setContentView(mWelcomeLayout);
    }

    /**
     * 初始化启动界面
     *
     * @param splashImgResId 背景资源图片资源ID
     */
    protected void initSplashView(int splashImgResId) {
        if (splashImgResId != 0) {
            Bitmap bm = BitmapFactory.decodeResource(this.getResources(), splashImgResId);
            BitmapDrawable bd = new BitmapDrawable(this.getResources(), bm);
            mWelcomeLayout.setBackground(bd);
        }
    }

    /**
     * 初始化启动界面背景图片
     *
     * @return 背景图片资源ID
     */
    protected int getSplashImgResId() {
        return 0;
    }

    /**
     * activity启动后的初始化
     */
    protected  void onCreateActivity(){
        initSplashView(R.drawable.config_bg_splash);
        startSplash(false);
    }

    /**
     * 启动页结束后的动作
     */
    protected void onSplashFinished(){
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    };

    /**
     * @return 启动页持续的时间
     */
    protected long getSplashDurationMillis() {
        return DEFAULT_SPLASH_DURATION_MILLIS;
    }

    /**
     * 开启过渡
     *
     * @param enableAlphaAnim 是否启用渐近动画
     */
    protected void startSplash(boolean enableAlphaAnim) {
        if (enableAlphaAnim) {
            startSplashAnim(new AlphaAnimation(0.2F, 1.0F));
        } else {
            startSplashAnim(new AlphaAnimation(1.0F, 1.0F));
        }
    }

    /**
     * 开启引导过渡动画
     *
     * @param anim
     */
    private void startSplashAnim(Animation anim) {
        anim.setDuration(getSplashDurationMillis());
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                onSplashFinished();
            }
        });
        mWelcomeLayout.startAnimation(anim);
    }

    @Override
    protected void onDestroy() {
        recycleBackground(mWelcomeLayout);
        super.onDestroy();
    }

    /**
     * 释放图片资源
     *
     * @param view 控件
     */
    private static void recycleBackground(View view) {
        Drawable d = view.getBackground();
        //别忘了把背景设为null，避免onDraw刷新背景时候出现used a recycled bitmap错误
        view.setBackgroundResource(0);
        if (d != null && d instanceof BitmapDrawable) {
            Bitmap bmp = ((BitmapDrawable) d).getBitmap();
            if (bmp != null && !bmp.isRecycled()) {
                bmp.recycle();
            }
        }
        if (d != null) {
            d.setCallback(null);
        }
    }
    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return onDisableBackKeyDown(keyCode) && super.onKeyDown(keyCode, event);
    }

    /**
     * 禁用物理返回键
     * <p>
     * 使用方法：
     * <p>需重写 onKeyDown</p>
     *
     * @param keyCode
     * @return 是否拦截事件
     * <p>
     * @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
     * return KeyboardUtils.onDisableBackKeyDown(keyCode) && super.onKeyDown(keyCode, event);
     * }
     * </p>
     */
    public static boolean onDisableBackKeyDown(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_HOME:
                return false;
            default:
                break;
        }
        return true;
    }
}
