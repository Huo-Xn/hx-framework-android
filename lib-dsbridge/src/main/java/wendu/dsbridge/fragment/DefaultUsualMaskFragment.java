package wendu.dsbridge.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import wendu.dsbridge.R;
import wendu.dsbridge.WebViewLoadinStatusListener;

/**
 * 默认普通的遮罩层
 *
 * @author: admin
 * @date: 2023/4/12
 */
public class DefaultUsualMaskFragment extends BaseLoadMaskFragment implements WebViewLoadinStatusListener {

    /**
     * 遮罩所需要显示的 图片
     */
    private ImageView mMaskImg;

    /**
     * 遮罩层 提示文字
     */
    private TextView mMaskText;

    /**
     * H5正在加载时的图片
     */
    private int H5LoadingImg = R.mipmap.icon_diqiu;

    private Handler handler;

    @Override
    protected int initFragmentContentView() {
        return R.layout.layout_mask_default;
    }


    @Override
    protected void onCreateView(View viewRoot, Bundle savedInstanceState) {
        handler = new Handler(Looper.myLooper());
        mMaskImg = $(R.id.base_mywebview_mask_img);
        mMaskText = $(R.id.base_mywebview_mask_text);
        Glide.with(this).load(H5LoadingImg).into(mMaskImg);
    }

    @Override
    public void onLoading(WebView webView, String msg) {
        if (disable) {
            show();
            mMaskText.setText(msg);
        }
    }

    @Override
    public void onErrors(WebView webView, String msg) {
        if (disable){
            show();
            mMaskText.setText("正在加载");
        }
    }

    @Override
    public void onLoadSuccess(WebView webView) {
        hide();
    }

    @Override
    public void onProgress(WebView view, int newProgress) {

    }
}
