package wendu.dsbridge.helper;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import wendu.dsbridge.PriorityBackPressedListener;
import wendu.dsbridge.OpenFileResultCallback;
import wendu.dsbridge.activity.PhotoActivity;
import wendu.dsbridge.tool.OpenFiles;
import wendu.dsbridge.tool.PermissionsManager;
import wendu.dsbridge.view.OpenFileWindow;

/**
 * 处理打开远程 URL
 *
 * @author: admin
 * @date: 2023/4/20
 */
public class OpenUrlFileDsbridge implements PriorityBackPressedListener {

    private Activity context;
    private PermissionsManager permissionsManager;
    private OpenFileResultCallback resultCallback;
    private Handler handler;
    /**
     * 图片响应数据标识
     */
    private int photoResult;
    private OpenFileWindow openFileWindow;

    public OpenUrlFileDsbridge(Activity context) {
        this.context = context;
        permissionsManager = new PermissionsManager(context);
        handler = new Handler(Looper.getMainLooper());
        openFileWindow = new OpenFileWindow(context);
    }

    public OpenUrlFileDsbridge(Activity context, OpenFileResultCallback resultCallback) {
        this.context = context;
        permissionsManager = new PermissionsManager(context);
        handler = new Handler(Looper.getMainLooper());
        this.resultCallback = resultCallback;
        openFileWindow = new OpenFileWindow(context);
    }

    public void setResultCallback(OpenFileResultCallback resultCallback) {
        this.resultCallback = resultCallback;
    }

    /**
     * 预览PDF
     *
     * @param obj
     */
    @JavascriptInterface
    public void openPDF(Object obj) {
        if (null != obj) {
            String url = obj.toString();
            String type = OpenFiles.urlToFileType(url);
            if (TextUtils.isEmpty(type)) {
                handler.post(() -> {
                    Toast.makeText(context, "无效的url", Toast.LENGTH_SHORT).show();
                });
                return;
            }
            //base64的数据类型
            if (url.startsWith("data:image")) {
                handler.post(() -> {
                    Intent intent = new Intent(context, PhotoActivity.class);
                    intent.putExtra("url", url);
                    context.startActivity(intent);
                    context.overridePendingTransition(0, 0);
                });
                return;
            }
            //进入 图片浏览
            if (TextUtils.equals(type, "jpg") || TextUtils.equals(type, "png") || TextUtils.equals(type, "jpeg") || TextUtils.equals(type, "gif")) {
                handler.post(() -> {
                    Intent intent = new Intent(context, PhotoActivity.class);
                    intent.putExtra("url", url);
                    context.startActivity(intent);
                    context.overridePendingTransition(0, 0);
                });

            } else if (TextUtils.equals("pdf", type) || TextUtils.equals("pptx", type) || TextUtils.equals("ppt", type) || TextUtils.equals("docx", type) || TextUtils.equals("doc", type) || TextUtils.equals("xlsx", type) || TextUtils.equals("xls", type)) {
                permissionsManager
                        .readAndWrite(() -> {
                            handler.post(() -> {
                                openFileWindow.show();
                                openFileWindow.openFile(url);
                            });

                        });

            } else {
                handler.post(() -> {
                    Toast.makeText(context, "无效的url", Toast.LENGTH_SHORT).show();
                });
                return;
            }
        }
    }


    @Override
    public boolean onPriorityBackPressed() {
        if (openFileWindow.isShowing()) {
            openFileWindow.dismiss();
            return true;
        }
        return false;
    }

}
