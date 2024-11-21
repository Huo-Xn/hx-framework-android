package com.hx.empty.activity;

import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;

import wendu.dsbridge.CompletionHandler;
import wendu.dsbridge.activity.BaseActivity;
import wendu.dsbridge.net.OkHttpUtil;
import wendu.dsbridge.tool.PermissionsManager;

public class MainActivity extends BaseActivity {

    @Override
    protected String url(WebSettings settings) {
        // andriod 11 及 以上系统沉浸式头 有灰色蒙版
        //添加Flag把状态栏设为可绘制模式
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //如果为全透明模式，取消设置Window半透明的Flag
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //设置状态栏为透明
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        View decor = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //设置window的状态栏不可见,且状态栏字体是白色
            if (isDarkMode(getBaseContext() )) {
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            } else {
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        }

        // TODO 填写你的项目地址
        return "http://192.168.172.67:1711/#/login";
    }

    @Override
    protected void initData(Bundle savedInstanceState, PermissionsManager permissionsManager, OkHttpUtil request) {
        // apk 更新
        String updateUrl = "";
        /*permissionsManager.readAndWrite(() -> {
            Log.w("permissions", "读写权限获取成功");
        });
        permissionsManager.camera(() -> {
            Log.w("permissions", "相机权限获取成功");
        });
        update(updateUrl);*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //这个是处理回调逻辑 需要回调onReceiveValue方法防止下次无法响应js方法
        getMyWebview().uploadMessageForAndroid5(data, resultCode);
    }

    protected boolean isDarkMode(Context context) {
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
        int currentNightMode = uiModeManager.getNightMode();
        return currentNightMode == UiModeManager.MODE_NIGHT_YES;
    }

    // 状态栏高度
    @JavascriptInterface
    public void getStatusBarHeight(Object data, CompletionHandler handler) {
        // 获取DisplayMetrics对象，该对象包含了屏幕的密度和尺寸信息
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float pixels = getStatusBarHeight();
        // 转换为CSS像素
        handler.complete(pixels / displayMetrics.density);
    }
}