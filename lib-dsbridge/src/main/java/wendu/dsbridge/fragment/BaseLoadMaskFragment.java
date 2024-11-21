package wendu.dsbridge.fragment;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import androidx.annotation.LayoutRes;

import wendu.dsbridge.WebViewLoadinStatusListener;
import wendu.dsbridge.base.BaseFm;

/**
 * @author: admin
 * @date: 2023/4/12
 */
public abstract class BaseLoadMaskFragment extends BaseFm implements WebViewLoadinStatusListener {

    /**
     * 设置 fragment 的核心容器
     *
     * @return
     */
    @LayoutRes

    protected abstract int initFragmentContentView();

    protected abstract void onCreateView(View viewRoot, Bundle savedInstanceState);

    protected boolean disable = false;

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    /**
     * 正在加载
     *
     * @param msg
     */
    @Override
    public abstract void onLoading(WebView webView, String msg);

    /**
     * 加载出错
     *
     * @param msg
     */
    @Override
    public abstract void onErrors(WebView webView, String msg);

    /**
     * 加载完成
     */
    @Override
    public abstract void onLoadSuccess(WebView webView);

    @Override
    public abstract void onProgress(WebView view, int newProgress);
}
