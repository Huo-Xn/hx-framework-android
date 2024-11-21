package wendu.dsbridge;

import android.webkit.WebView;

/**
 * @author: admin
 * @date: 2023/4/12
 */
public interface WebViewLoadinStatusListener {

    /**
     * 正在加载
     * @param msg
     */
    void onLoading(WebView view, String msg);

    /**
     * 加载出错了
     * @param msg
     */
    void onErrors(WebView view,String msg);

    /**
     * 加载完成了
     */
    void onLoadSuccess(WebView view);

    /**
     *  进度
     * @param view
     * @param newProgress
     */
    void onProgress(WebView view, int newProgress);
}
