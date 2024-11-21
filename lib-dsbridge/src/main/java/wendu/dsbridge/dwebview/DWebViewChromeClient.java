package wendu.dsbridge.dwebview;

import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import java.util.HashMap;
import java.util.Map;

import wendu.dsbridge.WebViewLoadinStatusListener;
import wendu.dsbridge.net.NetworkUtil;


/**
 * @description:
 * @author: ash
 * @date : 2022/2/25 11:57
 * @email : ash_945@126.com
 */
public class DWebViewChromeClient extends WebChromeClient {


    private volatile boolean alertBoxBlock = true;

    private boolean allowLoad = true;

    private FileChooserListener fileChooserListener;

    private HashMap<Integer, WebViewLoadinStatusListener> webViewLoadinStatusListeners;

    private ValueCallback<Uri[]> uploadMessageAboveL;
    private ValueCallback<Uri> uploadMessage;

    public DWebViewChromeClient() {
        webViewLoadinStatusListeners = new HashMap<>();
    }

    public void setFileChooserListener(FileChooserListener fileChooserListener) {
        this.fileChooserListener = fileChooserListener;
    }

    public void addWebViewLoadinStatusListener(int code, WebViewLoadinStatusListener webViewLoadinStatusListener) {
        webViewLoadinStatusListeners.put(code, webViewLoadinStatusListener);
    }

    public ValueCallback<Uri[]> getUploadMessageAboveL() {
        return uploadMessageAboveL;
    }

    public ValueCallback<Uri> getUploadMessage() {
        return uploadMessage;
    }

    public void setAlertBoxBlock(boolean alertBoxBlock) {
        this.alertBoxBlock = alertBoxBlock;
    }

    @Override
    public boolean onJsAlert(WebView view, String url, final String message, final JsResult result) {
        if (!alertBoxBlock) {
            result.confirm();
        }
        Dialog alertDialog = new AlertDialog.Builder(view.getContext()).
                setMessage(message).
                setCancelable(false).
                setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (alertBoxBlock) {
                            result.confirm();
                        }
                    }
                })
                .create();
        alertDialog.show();
        return true;
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message,
                               final JsResult result) {
        if (!alertBoxBlock) {
            result.confirm();
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (alertBoxBlock) {
                    if (which == Dialog.BUTTON_POSITIVE) {
                        result.confirm();
                    } else {
                        result.cancel();
                    }
                }
            }
        };
        new AlertDialog.Builder(view.getContext())
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, listener)
                .setNegativeButton(android.R.string.cancel, listener).show();
        return true;

    }

    @Override
    public boolean onJsPrompt(WebView view, String url, final String message,
                              String defaultValue, final JsPromptResult result) {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
//            String prefix = "_dsbridge=";
//            if (message.startsWith(prefix)) {
//                innerJavascriptInterface.call(message.substring(prefix.length()), defaultValue);
//                result.confirm(onJsPromptSDK17(message, defaultValue, result,prefix));
//                return true;
//            }
        }

        if (!alertBoxBlock) {
            result.confirm();
        }

        final EditText editText = new EditText(view.getContext());
        editText.setText(defaultValue);
        if (defaultValue != null) {
            editText.setSelection(defaultValue.length());
        }
        float dpi = view.getContext().getResources().getDisplayMetrics().density;
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (alertBoxBlock) {
                    if (which == Dialog.BUTTON_POSITIVE) {
                        result.confirm(editText.getText().toString());
                    } else {
                        result.cancel();
                    }
                }
            }
        };
        new AlertDialog.Builder(view.getContext())
                .setTitle(message)
                .setView(editText)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, listener)
                .setNegativeButton(android.R.string.cancel, listener)
                .show();
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int t = (int) (dpi * 16);
        layoutParams.setMargins(t, 0, t, 0);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        editText.setLayoutParams(layoutParams);
        int padding = (int) (15 * dpi);
        editText.setPadding(padding - (int) (5 * dpi), padding, padding, padding);
        return true;

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        uploadMessageAboveL = filePathCallback;
        if (null != fileChooserListener) {
            String[] acceptTypes = fileChooserParams.getAcceptTypes();
            int mode = fileChooserParams.getMode();
            fileChooserListener.onShowFileChooser(webView, filePathCallback, fileChooserParams);

            if (null != acceptTypes) {
                if (acceptTypes.length > 0) {
                    if (TextUtils.equals(acceptTypes[0], "image/*")) {
                        if (mode == 1) {
                            //多选图片
                            fileChooserListener.multipleSelectionPictures();
                        } else {
                            //单选
                            fileChooserListener.selectionPicture();
                        }
                    } else {

                        if (mode == 1) {
                            //多选文件
                            fileChooserListener.multipleSelectionFiles();
                        } else {
                            //单选
                            fileChooserListener.selectionFile();
                        }

                    }
                } else {
                    if (mode == 1) {
                        //多选文件
                        fileChooserListener.multipleSelectionFiles();
                    } else {
                        //单选
                        fileChooserListener.selectionFile();
                    }
                }
            } else {
                if (mode == 1) {
                    //多选文件
                    fileChooserListener.multipleSelectionFiles();
                } else {
                    //单选
                    fileChooserListener.selectionFile();
                }
            }

            return true;
        } else {
            return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
        }
    }


    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        if (newProgress > 80) {
            if (!TextUtils.isEmpty(view.getTitle())) {
                if (view.getTitle().contains("404") || view.getTitle().contains("网页无法打开")) {
                    if (allowLoad){
                        for (Map.Entry<Integer, WebViewLoadinStatusListener> webViewLoadinStatusListener : webViewLoadinStatusListeners.entrySet()) {
                            if (null != webViewLoadinStatusListener.getValue()) {
                                webViewLoadinStatusListener.getValue().onErrors(view, "网页无法打开");
                            }
                        }
                    }
                } else {
                    if (NetworkUtil.isNetworkAvailable(view.getContext())) {
                        for (Map.Entry<Integer, WebViewLoadinStatusListener> webViewLoadinStatusListener : webViewLoadinStatusListeners.entrySet()) {
                            if (null != webViewLoadinStatusListener.getValue()) {
                                webViewLoadinStatusListener.getValue().onLoadSuccess(view);
                            }
                        }
                        if (allowLoad) {
                            allowLoad = false;
                        }
                    } else {
                        if (allowLoad) {
                            for (Map.Entry<Integer, WebViewLoadinStatusListener> webViewLoadinStatusListener : webViewLoadinStatusListeners.entrySet()) {
                                if (null != webViewLoadinStatusListener.getValue()) {
                                    webViewLoadinStatusListener.getValue().onErrors(view, "网页无法打开");
                                }
                            }
                        }
                    }
                }
            } else {
                if (allowLoad) {
                    for (Map.Entry<Integer, WebViewLoadinStatusListener> webViewLoadinStatusListener : webViewLoadinStatusListeners.entrySet()) {
                        if (null != webViewLoadinStatusListener.getValue()) {
                            webViewLoadinStatusListener.getValue().onLoading(view, "加载中");
                        }
                    }
                }
            }
        } else {
            if (allowLoad) {
                for (Map.Entry<Integer, WebViewLoadinStatusListener> webViewLoadinStatusListener : webViewLoadinStatusListeners.entrySet()) {
                    if (null != webViewLoadinStatusListener.getValue()) {
                        webViewLoadinStatusListener.getValue().onLoading(view, "加载中");
                    }
                }
            }
        }
        for (Map.Entry<Integer, WebViewLoadinStatusListener> webViewLoadinStatusListener : webViewLoadinStatusListeners.entrySet()) {
            if (null != webViewLoadinStatusListener.getValue()) {
                webViewLoadinStatusListener.getValue().onProgress(view, newProgress);
            }
        }


    }


}
