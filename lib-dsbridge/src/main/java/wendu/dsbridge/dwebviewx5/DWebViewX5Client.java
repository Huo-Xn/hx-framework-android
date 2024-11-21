package wendu.dsbridge.dwebviewx5;

import android.widget.Toast;

import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * @description:
 * @author: ash
 * @date : 2022/2/24 10:56
 * @email : ash_945@126.com
 */
public  class DWebViewX5Client extends WebViewClient {

    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String s) {
        webView.loadUrl(s);
        return true;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest webResourceRequest) {
        webView.loadUrl(webResourceRequest.getUrl().toString());
        return true;
    }


}
