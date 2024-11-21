package wendu.dsbridge.helper;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.king.zxing.CaptureActivity;
import com.king.zxing.Intents;

import java.util.HashMap;

import wendu.dsbridge.PermissionsCallback;
import wendu.dsbridge.QRCodeResultCallback;
import wendu.dsbridge.ResultCallback;
import wendu.dsbridge.activity.BaseActivity;
import wendu.dsbridge.tool.PermissionsManager;

/**
 * 前端处理二维码
 *
 * @author: admin
 * @date: 2023/4/19
 */
public class QRCodeDsbridge {
    public static  final  int QRCODE_RESULT_CODE = 10034;


    private Activity context;
    private QRCodeResultCallback resultCallback;
    private  PermissionsManager permissionsManager;
    private  Handler handler;

    public QRCodeDsbridge(Activity context) {
        this.context = context;
        permissionsManager = new PermissionsManager(context);
        handler = new Handler(Looper.getMainLooper());
    }

    public QRCodeDsbridge(Activity context,QRCodeResultCallback resultCallback) {
        this.context = context;
        permissionsManager = new PermissionsManager(context);
        handler = new Handler(Looper.getMainLooper());
        this.resultCallback = resultCallback;
    }

    public void setResultCallback(QRCodeResultCallback resultCallback) {
        this.resultCallback = resultCallback;
    }

    /**
     * 打开二维码扫描
     * todo:还没有写权限
     *
     * @param
     */
    public void openQrcCode() {
        handler.post(() -> {
            permissionsManager.camera(new PermissionsCallback() {
                @Override
                public void granted() {
                    Intent intent = new Intent(context, CaptureActivity.class);
                    context.startActivityForResult(intent, QRCODE_RESULT_CODE);
                }
            });

        });

    }

    /**
     * 打开二维码扫描
     *
     * @param obj
     */
    @JavascriptInterface
    public void openQrcCode(Object obj) {
        openQrcCode();
    }


    /**
     * 获取二维码的数据
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void QRCode(int requestCode, int resultCode, @Nullable Intent data) {
        /**
         * 处理二维码
         */
        if (resultCode == RESULT_OK) {
            if (requestCode == QRCODE_RESULT_CODE) {
                try {
                    if (null != data) {
                        String result = data.getStringExtra(Intents.Scan.RESULT);

                        if (null != resultCallback) {
                            resultCallback.onQRCodeResult(true, result,"");
                        }

                    } else {
                        if (null != resultCallback) {
                            resultCallback.onQRCodeResult(false, "","获取二维码信息失败");
                        }
                    }
                } catch (Exception e) {
                    if (null != resultCallback) {
                        resultCallback.onQRCodeResult(false, "","获取二维码信息失败");
                    }
                } catch (Throwable e) {
                    if (null != resultCallback) {
                        resultCallback.onQRCodeResult(false, "","获取二维码失败，数据过大");
                    }
                }
            }
        }
    }


}
