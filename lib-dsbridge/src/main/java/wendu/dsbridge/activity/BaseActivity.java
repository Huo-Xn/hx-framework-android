package wendu.dsbridge.activity;

import static wendu.dsbridge.AndroidToH5Action.FROMANDROIDPHOTO;
import static wendu.dsbridge.AndroidToH5Action.FROMANDROIDQRCCODE;
import static wendu.dsbridge.AndroidToH5Action.FROMCHOOSEFILE;
import static wendu.dsbridge.tool.StatusBar.getNavBarHeight;
import static wendu.dsbridge.tool.StatusBar.isNavigationBarShown;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewTreeObserver;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.amap.api.location.AMapLocationClient;
import com.luck.picture.lib.config.PictureConfig;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import wendu.dsbridge.FileResultCallback;
import wendu.dsbridge.FromH5MessageCallBack;
import wendu.dsbridge.PermissionsCallback;
import wendu.dsbridge.PhotoResultCallback;
import wendu.dsbridge.PriorityBackPressedListener;
import wendu.dsbridge.QRCodeResultCallback;
import wendu.dsbridge.R;
import wendu.dsbridge.WebViewLoadinStatusListener;
import wendu.dsbridge.base.BaseAc;
import wendu.dsbridge.dwebview.DWebView;
import wendu.dsbridge.fragment.BaseLoadMaskFragment;
import wendu.dsbridge.fragment.BaseUpdateMaskFragment;
import wendu.dsbridge.fragment.DefaultErrorMaskFragment;
import wendu.dsbridge.fragment.DefaultUpdateFragment;
import wendu.dsbridge.fragment.DefaultUsualMaskFragment;
import wendu.dsbridge.fragment.WebViewFragment;
import wendu.dsbridge.helper.BackPriority;
import wendu.dsbridge.helper.FileDsbridge;
import wendu.dsbridge.helper.FragmentViewModel;
import wendu.dsbridge.helper.OpenUrlFileDsbridge;
import wendu.dsbridge.helper.PhotoDsbridge;
import wendu.dsbridge.helper.QRCodeDsbridge;
import wendu.dsbridge.net.OkHttpUtil;
import wendu.dsbridge.tool.GsonUtil;
import wendu.dsbridge.tool.LocationManager;
import wendu.dsbridge.tool.PermissionsManager;

/**
 * @author: admin
 * @date: 2022/7/26
 * base actvity
 */
public abstract class BaseActivity extends BaseAc implements WebViewLoadinStatusListener, KeyboardVisibilityEventListener, QRCodeResultCallback, PhotoResultCallback, FileResultCallback {

    /**
     * 定位管理
     */
    protected LocationManager locationManager;

    private MaskManager maskManager;
    private WebViewFragment webViewFragment;

    /**
     * 设置访问的主要 地址
     *
     * @param settings 可以在设置载入URL之前 进行一些设置。比如你要设置字体的大小   settings.setTextZoom(100);
     * @return 返回网页网址
     */
    protected abstract String url(WebSettings settings);

    /**
     * 当前置工作准备结束之后调用的函数，也是主要写主要逻辑处理的地方
     *
     * @param savedInstanceState 缓存数据（不常用）
     * @param permissionsManager 权限申请
     * @param request            网络请求
     */
    protected abstract void initData(Bundle savedInstanceState, final PermissionsManager permissionsManager, final OkHttpUtil request);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_webbase);
        getContentView().getViewTreeObserver().addOnGlobalLayoutListener(listener);
        initAdvance();

        webViewFragment = new WebViewFragment();

        addFragmentToContent(webViewFragment);
        showFragment(webViewFragment);
        KeyboardVisibilityEvent.setEventListener(this, this, this);

        new ViewModelProvider(this).get(FragmentViewModel.class).getFragmentModel().observe(this, baseFm -> {
            baseFm.addJavascriptObject(this, "mian");
            baseFm.getPhotoDsbridge().setResultCallback(this);
            baseFm.getFileDsbridge().setResultCallback(this);
            baseFm.getQRCodeDsbridge().setResultCallback(this);
            baseFm.getMyWebview().addWebViewLoadinStatusListener(0, this);
            maskManager = new MaskManager();

            WebSettings settings = baseFm.getMyWebview().getSettings();
            String url = url(settings);
            getMask().showUsualMask();
            if (TextUtils.isEmpty(url)) {
                finish();
            } else {
                baseFm.getMyWebview().loadUrl(url);
            }

            initData(savedInstanceState, permissionsManager, OkHttpUtil.getInstance());

        });

    }


    /**
     * 预留一些对于要提前声明的方法
     * 比如要替换加载的图片资源，声明一些SDK中的功能等
     */
    protected void initAdvance() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制竖屏

        //高德地图需要提前声明
        AMapLocationClient.updatePrivacyShow(this, true, true);
        AMapLocationClient.updatePrivacyAgree(this, true);

        permissionsManager.locationPermissions(new PermissionsCallback() {
            @Override
            public void granted() {
                locationManager = LocationManager.getInstance();
                locationManager.init(BaseActivity.this);
                getLifecycle().addObserver(locationManager);
            }
        });

    }

    public QRCodeDsbridge getQRCodeDsbridge() {
        return webViewFragment.getQRCodeDsbridge();
    }

    public PhotoDsbridge getPhotoDsbridge() {
        return webViewFragment.getPhotoDsbridge();
    }

    public FileDsbridge getFileDsbridge() {
        return webViewFragment.getFileDsbridge();
    }

    public OpenUrlFileDsbridge getOpenUrlFileDsbridge() {
        return webViewFragment.getOpenUrlFileDsbridge();
    }

    /**
     * 返回事件的处理优先级， 1-100之间的数字，数字越大，越优先处理
     *
     * @param listener
     */
    public void addOnBackPressedListener(BackPriority backPriority, PriorityBackPressedListener listener) {
        webViewFragment.addOnBackPressedListener(backPriority,listener);
    }

    /**
     * 删除该优先级的处理回调
     */
    public void removeOnBackPressedListener(BackPriority backPriority) {
        webViewFragment.removeOnBackPressedListener(backPriority);
    }

    /**
     * web 加载中
     *
     * @param webView
     * @param msg
     */
    @Override
    public void onLoading(WebView webView, String msg) {

    }

    /**
     * webview 加载失败
     *
     * @param webView
     * @param msg
     */
    @Override
    public void onErrors(WebView webView, String msg) {

    }

    /**
     * webview加载完毕
     *
     * @param webView
     */
    @Override
    public void onLoadSuccess(WebView webView) {

    }

    /**
     * webview加载时的加载进度
     *
     * @param view
     * @param newProgress
     */
    @Override
    public void onProgress(WebView view, int newProgress) {

    }

    /**
     * 添加一个 更新的地址
     *
     * @param updateUrl
     */
    public void update(String updateUrl) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null != maskManager.mUpdateFragment) {
                    maskManager.mUpdateFragment.executeUpdate(updateUrl);
                }
            }
        },2000);

    }

    /**
     * 获取webview
     *
     * @return
     */
    public DWebView getMyWebview() {
        return webViewFragment.getMyWebview();
    }


    /**
     * 获取 遮罩管理者
     *
     * @return
     */
    public MaskManager getMask() {
        return maskManager;
    }

    /**
     * 二维码扫码结果
     *
     * @param isSuc 是否成功
     * @param data  返回的数据
     * @param err   错误的信息
     */
    @Override
    public void onQRCodeResult(boolean isSuc, String data, String err) {
        if (isSuc) {
            sendMessageToH5(FROMANDROIDQRCCODE, GsonUtil.mapToJson(new String[]{"data", data}));
        } else {
            showToast(err);
        }
    }

    /**
     * 图片返回结果
     *
     * @param isSuc 是否成功
     * @param code  响应码
     * @param data  返回的数据
     * @param err   错误的信息
     */
    @Override
    public void onPhotoResult(boolean isSuc, int code, String data, String err) {
        if (isSuc) {
            onChossPhoto(code, data);
        } else {
            showToast(err);
        }
    }

    @Override
    public void onFileResult(boolean isSuc, int code, String data, String err) {
        if (isSuc) {
            onChossFile(code, data);
        } else {
            showToast(err);
        }
    }

    @Override
    protected void onDestroy() {
        getContentView().getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        super.onDestroy();
    }

    /**
     * 文件选择
     *
     * @param fileResultCode
     * @param json
     */
    public void onChossFile(int fileResultCode, String json) {
        sendMessageToH5(FROMCHOOSEFILE, json);
    }

    /**
     * 图片选择
     *
     * @param fileResultCode
     * @param json
     */
    public void onChossPhoto(int fileResultCode, String json) {
        if (fileResultCode == PictureConfig.CHOOSE_REQUEST) {
            if (null != json) {
                sendMessageToH5(FROMANDROIDPHOTO, json);
            } else {
                sendMessageToH5(FROMANDROIDPHOTO, "");
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        webViewFragment.getQRCodeDsbridge().QRCode(requestCode, resultCode, data);
        webViewFragment.getFileDsbridge().customFileResult(this, requestCode, resultCode, data);
        webViewFragment.getPhotoDsbridge().customPhotoResult(requestCode, resultCode, data);
    }


    protected void sendMessageToH5(String keyboardDown, String s) {
        webViewFragment.sendMessageToH5(keyboardDown, s);
    }


    public void fromH5Message(String methodNmae, FromH5MessageCallBack fromH5MessageCallBack) {
        webViewFragment.fromH5Message(methodNmae, fromH5MessageCallBack);
    }


    /**
     * 监听 键盘是否消失
     *
     * @param isOpen
     */
    @Override
    public void onVisibilityChanged(boolean isOpen) {
        if (!isOpen) {
            sendMessageToH5("keyboardDown", "0");
        } else {
            sendMessageToH5("keyboardUp", "1");
        }
    }

    /**
     * 返回键 监听
     */
    @Override
    public void onBackPressed() {
       /* if (webViewFragment.onBackPressed()) {
        } else {
            doubleClickFinish();
        }*/
        String js = "appgoback()";
        List<String> strList = new ArrayList<>(Arrays.asList("/login", "/index"));
        getMyWebview().evaluateJavascript(js, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                String url = getMyWebview().getUrl();
                String endUrl = url.substring(url.lastIndexOf("/"));
                //  判断在首页返回，退出界面
                if (strList.contains(endUrl)) {
                    doubleClickFinish();
                    return;
                }
                webViewFragment.onBackPressed();
            }
        });
    }


    /**
     * 遮罩层管理者
     */
    public class MaskManager {


        /**
         * 通常的遮罩层
         */
        private BaseLoadMaskFragment mUsualMaskFragment;

        /**
         * 更新的遮罩层
         */
        private BaseUpdateMaskFragment mUpdateFragment;

        /**
         * 发送错误的遮罩层
         */
        private BaseLoadMaskFragment mErrorMaskFragment;

        /**
         * 自定义遮罩层
         */
        private BaseLoadMaskFragment mCustomMaskFragment;

        /**
         * 是否禁用遮罩
         *
         * @param disable
         */
        public void disableUsualMask(boolean disable) {
            mUsualMaskFragment.setDisable(disable);
            mErrorMaskFragment.setDisable(disable);
        }


        private MaskManager() {
            mUsualMaskFragment = new DefaultUsualMaskFragment();
            mErrorMaskFragment = new DefaultErrorMaskFragment();
            mUpdateFragment = new DefaultUpdateFragment();
            setUsualMask(mUsualMaskFragment);
            setErrorMask(mErrorMaskFragment);
            setUpdateMask(mUpdateFragment);
        }


        /**
         * 添加遮罩层的 fragment
         *
         * @param fragment 需要继承自 MaskFragment 的 fragment
         */
        public void setUsualMask(BaseLoadMaskFragment fragment) {
            if (null != fragment) {
                if (null != mUsualMaskFragment) {
                    removeFragment(mUsualMaskFragment);
                }
                mUsualMaskFragment = fragment;
                addFragmentToContent(fragment);
                hideFragment(fragment);
                webViewFragment.getMyWebview().addWebViewLoadinStatusListener(61, fragment);
            }
        }

        /**
         * 添加遮罩层的 fragment
         *
         * @param fragment 需要继承自 MaskFragment 的 fragment
         */
        public void setErrorMask(BaseLoadMaskFragment fragment) {
            if (null != fragment) {
                if (null != mErrorMaskFragment) {
                    removeFragment(mErrorMaskFragment);
                }
                mErrorMaskFragment = fragment;
                addFragmentToContent(fragment);
                hideFragment(fragment);
                webViewFragment.getMyWebview().addWebViewLoadinStatusListener(62, fragment);
            }
        }

        /**
         * 添加遮罩层的 fragment
         *
         * @param fragment 需要继承自 MaskFragment 的 fragment
         */
        public void setUpdateMask(BaseUpdateMaskFragment fragment) {
            if (null != fragment) {
                if (null != mUpdateFragment) {
                    removeFragment(mUpdateFragment);
                }
                mUpdateFragment = fragment;
                addFragmentToContent(fragment);
                hideFragment(fragment);
            }
        }


        /**
         * 添加遮罩层的 fragment
         *
         * @param fragment 需要继承自 MaskFragment 的 fragment
         */
        public void addCustomMask(BaseLoadMaskFragment fragment) {
            if (null != fragment) {
                if (null != mCustomMaskFragment) {
                    removeFragment(mCustomMaskFragment);
                }
                mCustomMaskFragment = fragment;
                addFragmentToContent(fragment);
                hideFragment(fragment);
                webViewFragment.getMyWebview().addWebViewLoadinStatusListener(64, fragment);
            }

        }

        /**
         * 显示常规遮罩
         */
        public void showUsualMask() {
            handler.post(() -> {
                if (null != mUsualMaskFragment)
                    showFragment(mUsualMaskFragment);
            });
        }


        /**
         * 隐藏常规遮罩
         */
        public void dismissUsualMask() {
            handler.post(() -> {
                if (null != mUsualMaskFragment)
                    hideFragment(mUsualMaskFragment);
            });
        }

        /**
         * 显示错误遮罩
         */
        public void showErrorMask() {
            handler.post(() -> {
                if (null != mErrorMaskFragment)
                    showFragment(mErrorMaskFragment);
            });
        }


        /**
         * 隐藏错误遮罩
         */
        public void dismissErrorMask() {
            handler.post(() -> {
                if (null != mErrorMaskFragment)
                    hideFragment(mErrorMaskFragment);
            });
        }


        /**
         * 显示更新遮罩
         */
        public MaskManager showUpdateMask() {
            handler.post(() -> {
                if (null != mUpdateFragment)
                    showFragment(mUpdateFragment);
            });
            return getMask();
        }


        /**
         * 隐藏更新遮罩
         */
        public void dismissUpdateMask() {
            handler.post(() -> {
                if (null != mUpdateFragment)
                    hideFragment(mUpdateFragment);
            });
        }


        /**
         * 显示自定义遮罩
         */
        public void showCustomMask() {
            handler.post(() -> {
                if (null != mCustomMaskFragment) {
                    showFragment(mCustomMaskFragment);
                }
            });
        }


        /**
         * 隐藏自定义遮罩
         */
        public void dismissCustomMask() {
            handler.post(() -> {
                if (null != mCustomMaskFragment)
                    hideFragment(mCustomMaskFragment);
            });
        }

    }

    private int usableHeightPrevious;

    private ViewTreeObserver.OnGlobalLayoutListener listener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            //设置一个Listener监听View树变化，界面变化之后，获取"可用高度"，最后重新设置高度
            possiblyResizeChildOfContent();
        }
    };

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int navBarHeight = 0;
            if(isNavigationBarShown(this)){
                navBarHeight = getNavBarHeight(this);
            }
            int usableHeightSansKeyboard = getContentView().getRootView().getHeight()-navBarHeight;
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard/4)) {
                // keyboard probably just became visible
                int statusBarHeight = getStatusBarHeight();
                getContentView().getLayoutParams().height = usableHeightSansKeyboard - heightDifference+statusBarHeight;
            } else {
                // keyboard probably just became hidden
                getContentView().getLayoutParams().height = usableHeightSansKeyboard;
            }
            getContentView().requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        getContentView().getWindowVisibleDisplayFrame(r);
        //rect.top其实是状态栏的高度，如果是全屏主题，直接 return rect.bottom就可以了
        return (r.bottom - r.top);// 全屏模式下： return r.bottom
    }


}
