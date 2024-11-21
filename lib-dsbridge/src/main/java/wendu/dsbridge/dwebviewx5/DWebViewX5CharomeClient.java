package wendu.dsbridge.dwebviewx5;

import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.appcompat.app.AlertDialog;

import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

/**
 * @description:
 * @author: ash
 * @date : 2022/2/25 13:38
 * @email : ash_945@126.com
 */
public class DWebViewX5CharomeClient extends WebChromeClient {


    private volatile boolean alertBoxBlock = true;

    private X5FileChooserListener x5FileChooserListener;

    private ValueCallback<Uri[]> uploadMessageAboveL;
    private ValueCallback<Uri> uploadMessage;


    public void setAlertBoxBlock(boolean alertBoxBlock) {
        this.alertBoxBlock = alertBoxBlock;
    }

    public void setX5FileChooserListener(X5FileChooserListener x5FileChooserListener) {
        this.x5FileChooserListener = x5FileChooserListener;
    }

    public ValueCallback<Uri[]> getUploadMessageAboveL() {
        return uploadMessageAboveL;
    }

    public ValueCallback<Uri> getUploadMessage() {
        return uploadMessage;
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
//            innerJavascriptInterface.call(message.substring(prefix.length()), defaultValue)
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

    @Override
    public void openFileChooser(ValueCallback<Uri> valueCallback, String s, String s1) {
        uploadMessage = valueCallback;
        if (null != x5FileChooserListener) {
            x5FileChooserListener.openFileChooser(valueCallback, s, s1);
        } else {
            //TODO:无法得知 单选和多选的参数 待补充
            super.openFileChooser(valueCallback, s, s1);
        }
    }


    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, FileChooserParams fileChooserParams) {
        uploadMessageAboveL = valueCallback;
        if (null != x5FileChooserListener) {
            String[] acceptTypes = fileChooserParams.getAcceptTypes();
            int mode = fileChooserParams.getMode();

            x5FileChooserListener.onShowFileChooser(webView, valueCallback, fileChooserParams);

            if (null != acceptTypes) {
                if (acceptTypes.length > 0) {
                    if (TextUtils.equals(acceptTypes[0], "image/*")) {
                        if (mode == 1) {
                            //多选图片
                            x5FileChooserListener.multipleSelectionPictures();
                        } else {
                            //单选
                            x5FileChooserListener.selectionPicture();
                        }
                    } else {

                        if (mode == 1) {
                            //多选文件
                            x5FileChooserListener.multipleSelectionFiles();
                        } else {
                            //单选
                            x5FileChooserListener.selectionFile();
                        }

                    }
                } else {
                    if (mode == 1) {
                        //多选文件
                        x5FileChooserListener.multipleSelectionFiles();
                    } else {
                        //单选
                        x5FileChooserListener.selectionFile();
                    }
                }
            } else {
                if (mode == 1) {
                    //多选文件
                    x5FileChooserListener.multipleSelectionFiles();
                } else {
                    //单选
                    x5FileChooserListener.selectionFile();
                }
            }

            return true;
        } else {
            return super.onShowFileChooser(webView, valueCallback, fileChooserParams);
        }
    }


}
