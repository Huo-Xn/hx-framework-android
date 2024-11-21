package wendu.dsbridge.dwebviewx5;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;

import java.util.HashMap;

public class X5WebViewManager {
    private final String SUCCEEDED = "x5Succeeded";

    private static X5WebViewManager instance;
    private final HashMap mX5Config;

    private SharedPreferences mX5SharedPreferences;

    private X5DownListener mX5DownListener;
    private X5InitListener mX5InitListener;

    public static X5WebViewManager getInstance() {
        if (instance == null) {
            synchronized (X5WebViewManager.class) {
                if (instance == null) {
                    instance = new X5WebViewManager();
                }
            }
        }
        return instance;
    }

    private X5WebViewManager() {
        mX5Config = new HashMap();
        mX5Config.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        mX5Config.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        // 在调用TBS初始化、创建WebView之前进行如下配置
        QbSdk.initTbsSettings(mX5Config);
        //非wifi情况下，主动下载x5内核
        QbSdk.setDownloadWithoutWifi(true);
        QbSdk.disableAutoCreateX5Webview();
    }


    public void init(Context context, QbSdk.PreInitCallback cb, TbsListener tl) {
        createRecord(context);
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.setTbsListener(tl);
        //x5内核初始化接口
        QbSdk.initX5Environment(context, cb);


    }


    public void init(final Context context) {
        createRecord(context);
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.setTbsListener(new TbsListener() {
            /**
             * 下载完成
             *
             * @param code
             */
            @Override
            public void onDownloadFinish(int code) {
                if (code == ErrorCode.NONEEDTODOWN_ERROR) {
                    //无需下载
                    Log.w("查看", "X5内核" + "无需下载");
                } else if (code == ErrorCode.DOWNLOAD_SUCCESS) {
                    //下载成功
                    changeSucceeded(true);
                    //只有在下载安装过后才会调用此方法
                    if (null != mX5DownListener) {
                        mX5DownListener.onDownloadSuccess(code, "download success");
                    }
                } else {
                    //下载失败了
                    changeSucceeded(false);
                    reset(context);
                    init(context);
                    if (null != mX5DownListener) {
                        mX5DownListener.onDownloadFail(code, "fail");
                    }
                }

            }


            /**
             * 安装完成
             *
             * @param code
             */
            @Override
            public void onInstallFinish(int code) {
                if (code == ErrorCode.DOWNLOAD_INSTALL_SUCCESS) {
                    //下载安装成功
                    Log.w("查看", "X5内核" + "安装成功");
                    if (null != mX5DownListener) {
                        mX5DownListener.onInstallSuccess(code,"安装成功");
                    }
                } else if (code == ErrorCode.INSTALL_SUCCESS_AND_RELEASE_LOCK) {
                    //下载安装成功并删除安装包
                    Log.w("查看", "X5内核" + "成功并删除安装包");
                } else {
                    //安装fail
                    changeSucceeded(false);
                    reset(context);
                    init(context);
                    Log.e("查看", "X5内核" + "安装fail");
                    if (null != mX5DownListener) {
                        mX5DownListener.onInstallFail(code,"安装失败");
                    }
                }

            }


            /**
             * 下载进度
             *
             * @param progress
             */
            @Override
            public void onDownloadProgress(int progress) {
                if (null != mX5DownListener) {
                    mX5DownListener.onDownloadProgress(progress);
                }
            }
        });
        //x5内核初始化接口
        QbSdk.initX5Environment(context, new QbSdk.PreInitCallback() {
            /**
             * 加载状态
             *
             * @param arg0 true 加载成功
             *             fales 加载失败
             */
            @Override
            public void onViewInitFinished(boolean arg0) {

                if (arg0) {
                    Log.d("查看", "X5内核" + "加载成功");
                    if (null != mX5InitListener) {
                        mX5InitListener.onViewInitSuccess();
                    }
                } else {
                    Log.e("查看", "X5内核" + "加载失败");
                    if (!isSucceeded()) {
                        //首次或者是下载失败的状态，要重新下载
                        reset(context);
                        init(context);
                    } else {
                        if (null != mX5InitListener) {
                            mX5InitListener.onViewInitFail();
                        }
                    }
                }

            }


            /**
             * 成功后会调用该方法
             */
            @Override
            public void onCoreInitFinished() {
//                if (null != mX5InitListener) {
//                    mX5InitListener.onCoreInitFinished();
//                }
            }
        });
    }


    /**
     * 清除有关x5的缓存
     *
     * @param context
     */
    public void reset(Context context) {
        QbSdk.reset(context);
    }


    /**
     * 存储关于X5初始化相关的记录
     *
     * @param context
     */
    private void createRecord(Context context) {
        mX5SharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
    }

    /**
     * 修改是否下载成功过的记录状态
     *
     * @param b
     */
    public void changeSucceeded(boolean b) {
        mX5SharedPreferences.edit().putBoolean(SUCCEEDED, b).commit();
    }


    /**
     * 获取是否下载成功过的记录状态
     * 首次必定处于失败状态
     */
    public boolean isSucceeded() {
        return mX5SharedPreferences.getBoolean(SUCCEEDED, false);
    }

    /**
     * 添加回调接口
     *
     * @param x5DownListener
     */
    public void setX5DownListener(X5DownListener x5DownListener) {
        this.mX5DownListener = x5DownListener;
    }

    /**
     * 添加初始化回调接口
     *
     * @param X5InitListener
     */
    public void setX5InitListener(X5InitListener X5InitListener) {
        this.mX5InitListener = X5InitListener;
    }




}
