package wendu.dsbridge.dwebview;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.Keep;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wendu.dsbridge.CompletionHandler;
import wendu.dsbridge.OnReturnValue;
import wendu.dsbridge.PriorityBackPressedListener;
import wendu.dsbridge.WebViewLoadinStatusListener;
import wendu.dsbridge.tool.ImageCompressUtils;


/**
 * Created by du on 16/12/29.
 */

public class DWebView extends WebView implements LifecycleObserver  , PriorityBackPressedListener {
    private static final String BRIDGE_NAME = "_dsbridge";
    private static final String LOG_TAG = "dsBridge";
    private static boolean isDebug = false;
    private Map<String, Object> javaScriptNamespaceInterfaces = new HashMap<String, Object>();
    private String APP_CACHE_DIRNAME;
    private int callID = 0;
    
    public final static int FILECHOOSER_RESULTCODE = 101;// 表单的结果回调</span>

    private volatile boolean alertBoxBlock = true;
    private JavascriptCloseWindowListener javascriptCloseWindowListener = null;
    private ArrayList<CallInfo> callInfoList;
    private InnerJavascriptInterface innerJavascriptInterface = new InnerJavascriptInterface();
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private DWebViewChromeClient dWebViewChromeClient;
    private DWebViewClient dWebViewClient;

    private Uri imageUri;
    /**
     * 是否启用返回
     */
    private boolean backEnable = true;


    public DWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DWebView(Context context) {
        super(context);
        init();
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void init() {
        dWebViewChromeClient = new DWebViewChromeClient();
        dWebViewClient = new DWebViewClient();
        dWebViewChromeClient.setFileChooserListener(new FileChooserListener() {
            @Override
            public void openFileChooser(ValueCallback<Uri> valueCallback, String s, String s1) {
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                takePhoto();
                return true;
            }
        });
        APP_CACHE_DIRNAME = getContext().getFilesDir().getAbsolutePath() + "/webcache";
        WebSettings settings = getSettings();
        settings.setDomStorageEnabled(true);
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setAllowContentAccess(true); // 是否可访问Content Provider的资源，默认值 true
        settings.setAllowFileAccess(true);    //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        settings.setAppCacheEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setAppCachePath(APP_CACHE_DIRNAME);
        settings.setUseWideViewPort(true);
        super.setWebChromeClient(dWebViewChromeClient);
        setWebViewClient(dWebViewClient);
        addInternalJavascriptObject();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            super.addJavascriptInterface(innerJavascriptInterface, BRIDGE_NAME);
        } else {
            // add dsbridge tag in lower android version
            settings.setUserAgentString(settings.getUserAgentString() + " _dsbridge");
        }
    }

    public void addWebViewLoadinStatusListener(int code,WebViewLoadinStatusListener webViewLoadinStatusListener) {
        dWebViewChromeClient.addWebViewLoadinStatusListener(code,webViewLoadinStatusListener);
    }

    public void setBackEnable(boolean backEnable) {
        this.backEnable = backEnable;
    }


    private class InnerJavascriptInterface {

        private void PrintDebugInfo(String error) {
            Log.d(LOG_TAG, error);
            if (isDebug) {
                evaluateJavascript(String.format("alert('%s')", "DEBUG ERR MSG:\\n" + error.replaceAll("\\'", "\\\\'")));
            }
        }

        private String method(Object jsb, String error, String argStr, String methodName) {
            JSONObject ret = new JSONObject();
            try {
                ret.put("code", -1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (jsb == null) {
                PrintDebugInfo(error);
                return ret.toString();
            }
            Object arg = null;
            Method method = null;
            String callback = null;

            try {
                JSONObject args = new JSONObject(argStr);
                if (args.has("_dscbstub")) {
                    callback = args.getString("_dscbstub");
                }
                if (args.has("data")) {
                    arg = args.get("data");
                }
            } catch (JSONException e) {
                error = String.format("The argument of \"%s\" must be a JSON object string!", methodName);
                PrintDebugInfo(error);
                e.printStackTrace();
                return ret.toString();
            }


            Class<?> cls = jsb.getClass();
            boolean asyn = false;
            try {
                method = cls.getMethod(methodName,
                        new Class[]{Object.class, CompletionHandler.class});
                asyn = true;
            } catch (Exception e) {
                try {
                    method = cls.getMethod(methodName, new Class[]{Object.class});
                } catch (Exception ex) {

                }
            }

            if (method == null) {
                error = "Not find method \"" + methodName + "\" implementation! please check if the  signature or namespace of the method is right ";
                PrintDebugInfo(error);
                return ret.toString();
            }


            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                JavascriptInterface annotation = method.getAnnotation(JavascriptInterface.class);
                if (annotation == null) {
                    error = "Method " + methodName + " is not invoked, since  " +
                            "it is not declared with JavascriptInterface annotation! ";
                    PrintDebugInfo(error);
                    return ret.toString();
                }
            }

            Object retData;
            method.setAccessible(true);
            try {
                if (asyn) {
                    final String cb = callback;
                    method.invoke(jsb, arg, new CompletionHandler() {

                        @Override
                        public void complete(Object retValue) {
                            complete(retValue, true);
                        }

                        @Override
                        public void complete() {
                            complete(null, true);
                        }

                        @Override
                        public void setProgressData(Object value) {
                            complete(value, false);
                        }

                        private void complete(Object retValue, boolean complete) {
                            try {
                                JSONObject ret = new JSONObject();
                                ret.put("code", 0);
                                ret.put("data", retValue);
                                //retValue = URLEncoder.encode(ret.toString(), "UTF-8").replaceAll("\\+", "%20");
                                if (cb != null) {
                                    //String script = String.format("%s(JSON.parse(decodeURIComponent(\"%s\")).data);", cb, retValue);
                                    String script = String.format("%s(%s.data);", cb, ret.toString());
                                    if (complete) {
                                        script += "delete window." + cb;
                                    }
                                    //Log.d(LOG_TAG, "complete " + script);
                                    evaluateJavascript(script);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    retData = method.invoke(jsb, arg);
                    ret.put("code", 0);
                    ret.put("data", retData);
                    return ret.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
                error = String.format("Call failed：The parameter of \"%s\" in Java is invalid.", methodName);
                PrintDebugInfo(error);
                return ret.toString();
            }
            return ret.toString();
        }

        @Keep
        @JavascriptInterface
        public String call(String methodName, String argStr) {
            String error = "Js bridge  called, but can't find a corresponded " +
                    "JavascriptInterface object , please check your code!";

            String[] nameStr = parseNamespace(methodName.trim());
            methodName = nameStr[1];

            // Object jsb = javaScriptNamespaceInterfaces.get(nameStr[0]);

            String str = "";
            for (Map.Entry<String, Object> stringObjectEntry : javaScriptNamespaceInterfaces.entrySet()) {
                Object jsb = stringObjectEntry.getValue();
                String method = method(jsb, error, argStr, methodName);
                if (TextUtils.equals(method, "{\"code\":0}")) {
                    return method;
                } else {
                    str = method;
                }
            }
            return str;
        }

    }

    Map<Integer, OnReturnValue> handlerMap = new HashMap<>();

    public interface JavascriptCloseWindowListener {
        /**
         * @return If true, close the current activity, otherwise, do nothing.
         */
        boolean onClose();
    }


    @Deprecated
    public interface FileChooser {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        void openFileChooser(ValueCallback valueCallback, String acceptType);

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        void openFileChooser(ValueCallback<Uri> valueCallback,
                             String acceptType, String capture);
    }



    /**
     * Set debug mode. if in debug mode, some errors will be prompted by a dialog
     * and the exception caused by the native handlers will not be captured.
     *
     * @param enabled
     */
    public static void setWebContentsDebuggingEnabled(boolean enabled) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(enabled);
        }
        isDebug = enabled;
    }




    private String[] parseNamespace(String method) {
        int pos = method.lastIndexOf('.');
        String namespace = "";
        if (pos != -1) {
            namespace = method.substring(0, pos);
            method = method.substring(pos + 1);
        }
        return new String[]{namespace, method};
    }

    @Keep
    private void addInternalJavascriptObject() {
        addJavascriptObject(new Object() {

            @Keep
            @JavascriptInterface
            public boolean hasNativeMethod(Object args) throws JSONException {
                JSONObject jsonObject = (JSONObject) args;
                String methodName = jsonObject.getString("name").trim();
                String type = jsonObject.getString("type").trim();
                String[] nameStr = parseNamespace(methodName);
                Object jsb = javaScriptNamespaceInterfaces.get(nameStr[0]);
                if (jsb != null) {
                    Class<?> cls = jsb.getClass();
                    boolean asyn = false;
                    Method method = null;
                    try {
                        method = cls.getMethod(nameStr[1],
                                new Class[]{Object.class, CompletionHandler.class});
                        asyn = true;
                    } catch (Exception e) {
                        try {
                            method = cls.getMethod(nameStr[1], new Class[]{Object.class});
                        } catch (Exception ex) {

                        }
                    }
                    if (method != null) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            JavascriptInterface annotation = method.getAnnotation(JavascriptInterface.class);
                            if (annotation == null) {
                                return false;
                            }
                        }
                        if ("all".equals(type) || (asyn && "asyn".equals(type) || (!asyn && "syn".equals(type)))) {
                            return true;
                        }

                    }
                }
                return false;
            }

            @Keep
            @JavascriptInterface
            public String closePage(Object object) throws JSONException {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (javascriptCloseWindowListener == null
                                || javascriptCloseWindowListener.onClose()) {
                            Context context = getContext();
                            if (context instanceof Activity) {
                                ((Activity) context).onBackPressed();
                            }
                        }
                    }
                });
                return null;
            }

            @Keep
            @JavascriptInterface
            public void disableJavascriptDialogBlock(Object object) throws JSONException {
                JSONObject jsonObject = (JSONObject) object;
                alertBoxBlock = !jsonObject.getBoolean("disable");
                if (null != dWebViewChromeClient) {
                    dWebViewChromeClient.setAlertBoxBlock(alertBoxBlock);
                }

            }

            @Keep
            @JavascriptInterface
            public void dsinit(Object jsonObject) {
                DWebView.this.dispatchStartupQueue();
            }

            @Keep
            @JavascriptInterface
            public void returnValue(final Object obj) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = (JSONObject) obj;
                        Object data = null;
                        try {
                            int id = jsonObject.getInt("id");
                            boolean isCompleted = jsonObject.getBoolean("complete");
                            OnReturnValue handler = handlerMap.get(id);
                            if (jsonObject.has("data")) {
                                data = jsonObject.get("data");
                            }
                            if (handler != null) {
                                handler.onValue(data);
                                if (isCompleted) {
                                    handlerMap.remove(id);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

        }, "_dsb");
    }

    private void _evaluateJavascript(String script) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            DWebView.super.evaluateJavascript(script, null);
        } else {
            super.loadUrl("javascript:" + script);
        }
    }

    /**
     * This method can be called in any thread, and if it is not called in the main thread,
     * it will be automatically distributed to the main thread.
     *
     * @param script
     */
    public void evaluateJavascript(final String script) {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                _evaluateJavascript(script);
            }
        });
    }

    /**
     * This method can be called in any thread, and if it is not called in the main thread,
     * it will be automatically distributed to the main thread.
     *
     * @param url
     */
    @Override
    public void loadUrl(final String url) {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (url != null && url.startsWith("javascript:")) {
                    DWebView.super.loadUrl(url);
                } else {
                    callInfoList = new ArrayList<>();
                    DWebView.super.loadUrl(url);
                }
            }
        });
    }

    /**
     * This method can be called in any thread, and if it is not called in the main thread,
     * it will be automatically distributed to the main thread.
     *
     * @param url
     * @param additionalHttpHeaders
     */
    @Override
    public void loadUrl(final String url, final Map<String, String> additionalHttpHeaders) {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (url != null && url.startsWith("javascript:")) {
                    DWebView.super.loadUrl(url, additionalHttpHeaders);
                } else {
                    callInfoList = new ArrayList<>();
                    DWebView.super.loadUrl(url, additionalHttpHeaders);
                }
            }
        });
    }

    @Override
    public void reload() {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                callInfoList = new ArrayList<>();
                DWebView.super.reload();
            }
        });
    }

    /**
     * set a listener for javascript closing the current activity.
     */
    public void setJavascriptCloseWindowListener(JavascriptCloseWindowListener listener) {
        javascriptCloseWindowListener = listener;
    }


    private static class CallInfo {
        private String data;
        private int callbackId;
        private String method;

        CallInfo(String handlerName, int id, Object[] args) {
            if (args == null) args = new Object[0];
            data = new JSONArray(Arrays.asList(args)).toString();
            callbackId = id;
            method = handlerName;
        }

        @Override
        public String toString() {
            JSONObject jo = new JSONObject();
            try {
                jo.put("method", method);
                jo.put("callbackId", callbackId);
                jo.put("data", data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jo.toString();
        }
    }

    private synchronized void dispatchStartupQueue() {
        if (callInfoList != null) {
            for (CallInfo info : callInfoList) {
                dispatchJavascriptCall(info);
            }
            callInfoList = null;
        }
    }

    private void dispatchJavascriptCall(CallInfo info) {
        evaluateJavascript(String.format("window._handleMessageFromNative(%s)", info.toString()));
    }

    public synchronized <T> void callHandler(String method, Object[] args, final OnReturnValue<T> handler) {

        CallInfo callInfo = new CallInfo(method, ++callID, args);
        if (handler != null) {
            handlerMap.put(callInfo.callbackId, handler);
        }

        if (callInfoList != null) {
            callInfoList.add(callInfo);
        } else {
            dispatchJavascriptCall(callInfo);
        }

    }

    public void callHandler(String method, Object[] args) {
        callHandler(method, args, null);
    }

    public <T> void callHandler(String method, OnReturnValue<T> handler) {
        callHandler(method, null, handler);
    }


    /**
     * Test whether the handler exist in javascript
     *
     * @param handlerName
     * @param existCallback
     */
    public void hasJavascriptMethod(String handlerName, OnReturnValue<Boolean> existCallback) {
        callHandler("_hasJavascriptMethod", new Object[]{handlerName}, existCallback);
    }

    /**
     * Add a java object which implemented the javascript interfaces to dsBridge with namespace.
     * Remove the object using {@link #removeJavascriptObject(String) removeJavascriptObject(String)}
     *
     * @param object
     * @param namespace if empty, the object have no namespace.
     */
    public void addJavascriptObject(Object object, String namespace) {
        if (namespace == null) {
            namespace = "";
        }
        if (object != null) {
            javaScriptNamespaceInterfaces.put(namespace, object);
        }
    }

    /**
     * remove the javascript object with supplied namespace.
     *
     * @param namespace
     */
    public void removeJavascriptObject(String namespace) {
        if (namespace == null) {
            namespace = "";
        }
        javaScriptNamespaceInterfaces.remove(namespace);

    }

    public void disableJavascriptDialogBlock(boolean disable) {
        alertBoxBlock = !disable;
        if (null != dWebViewChromeClient) {
            dWebViewChromeClient.setAlertBoxBlock(alertBoxBlock);
        }
    }

    public void setDWebViewChromeClient(DWebViewChromeClient dWebViewChromeClient) {
        this.dWebViewChromeClient = dWebViewChromeClient;
        setWebChromeClient(dWebViewChromeClient);
    }

    public void setDWebViewClient(DWebViewClient dWebViewClient) {
        this.dWebViewClient = dWebViewClient;
        setWebViewClient(dWebViewClient);
    }

    public DWebViewChromeClient getDWebViewChromeClient() {
        return dWebViewChromeClient;
    }

    public DWebViewClient getDWebViewClient() {
        return dWebViewClient;
    }

    @Override
    public void clearCache(boolean includeDiskFiles) {
        super.clearCache(includeDiskFiles);
        CookieManager.getInstance().removeAllCookie();
        Context context = getContext();
        try {
            context.deleteDatabase("webview.db");
            context.deleteDatabase("webviewCache.db");
        } catch (Exception e) {
            e.printStackTrace();
        }

        File appCacheDir = new File(APP_CACHE_DIRNAME);
        File webviewCacheDir = new File(context.getCacheDir()
                .getAbsolutePath() + "/webviewCache");

        if (webviewCacheDir.exists()) {
            deleteFile(webviewCacheDir);
        }

        if (appCacheDir.exists()) {
            deleteFile(appCacheDir);
        }
    }

    public void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        } else {
            Log.e("Webview", "delete file no exists " + file.getAbsolutePath());
        }
    }

    private void runOnMainThread(Runnable runnable) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            runnable.run();
            return;
        }
        mainHandler.post(runnable);
    }


    /**
     * 向 前端 发出消息
     *
     * @param methodName
     * @param data
     */
    public void callHandler(String methodName, String data) {
        callHandler(methodName, new Object[]{data});
    }

    /**
     * 向 前端 发出消息
     *
     * @param methodName
     * @param data
     */
    public void callHandler(String methodName, String data, OnReturnValue<Object> onReturnValue) {
        callHandler(methodName, new Object[]{data}, onReturnValue);
    }

    public void hide() {
        setVisibility(GONE);
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    /**
     * 销毁
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        if (null != this) {
            //先从父容器中移除webview,然后再销毁webview
            ViewParent parent = getParent();
            if (null != parent) {
                ((ViewGroup) parent).removeView(this);
            }
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            stopLoading();
            getSettings().setJavaScriptEnabled(false);
            clearHistory();
            clearView();
            removeAllViews();
            try {
                destroy();
            } catch (Throwable ex) {

            }
        }
    }

    @Override
    public boolean onPriorityBackPressed() {
        if (canGoBack()) {
            if (backEnable) {
                //表示按返回键时的操作
                goBack();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    //h5系统调用系统手机、文件、相册系统 获取照片或者文件
    private void takePhoto() {
        File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "HXApp");
        if (!imageStorageDir.exists()) {
            imageStorageDir.mkdirs();
        }
        Activity activity =((Activity) getContext());
        File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        imageUri = Uri.fromFile(file);
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = activity.getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent i = new Intent(captureIntent);
            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            i.setPackage(packageName);
            i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraIntents.add(i);

        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        Intent chooserIntent = Intent.createChooser(intent, "图片选择");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
        activity.startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
    }
    /**
     * 5.0以上 上传图片成功后的回调
     */
    public void uploadMessageForAndroid5(Intent intent, int resultCode) {
        ValueCallback<Uri[]> uploadMessageAboveL = dWebViewChromeClient.getUploadMessageAboveL();
        if (null == uploadMessageAboveL) {
            return;
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            uploadMessageAboveL.onReceiveValue(null);
            return;
        }
        //h5系统调用系统手机、文件、相册系统 回调Uri
        Uri[] results = null;
        if (resultCode == RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        Uri compressImageUri = ImageCompressUtils.compressBmpFromBmp(uriToString(item.getUri()));
                        results[i] =compressImageUri ;
                    }
                }

                if (dataString != null){
                    Uri compressImageUri = ImageCompressUtils.compressBmpFromBmp(uriToString(Uri.parse(dataString)));
                    results = new Uri[]{compressImageUri};
                }

            }else{
                Uri compressImageUri = ImageCompressUtils.compressBmpFromBmp(imageUri.getPath());
                results = new Uri[]{compressImageUri};
            }
        }

        /**
         * 如果经过了上面的处理，result 不为空，说明用户确实取得了图片，那么将 result 返回即可
         *
         * 否则，返回空的图片（图片只有名字，并没有真正的图片）
         */
        if (results == null) {
            results = new Uri[]{imageUri};
        }
        uploadMessageAboveL.onReceiveValue(results);
        uploadMessageAboveL = null;
    }



    public String uriToString(Uri uri) {
        String path = null;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            if (DocumentsContract.isDocumentUri(getContext(), uri)) {
                // ExternalStorageProvider
                if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                    String docId = DocumentsContract.getDocumentId(uri);
                    String[] split = docId.split(":");
                    String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        path = Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                    // DownloadsProvider
                    String id = DocumentsContract.getDocumentId(uri);
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    path = getDataColumn(getContext(), contentUri, null, null);
                } else if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                    // MediaProvider
                    String docId = DocumentsContract.getDocumentId(uri);
                    String[] split = docId.split(":");
                    String type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    String selection = "_id=?";
                    String[] selectionArgs = new String[]{split[1]};
                    path = getDataColumn(getContext(), contentUri, selection, selectionArgs);
                }
            } else {
                path = getRealPathFromUri(getContext(), uri);
            }
        }
        return path;
    }

    private String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Files.FileColumns.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

}
