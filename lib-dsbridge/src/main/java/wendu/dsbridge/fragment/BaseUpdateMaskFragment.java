package wendu.dsbridge.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.boge.update.DownloadWrapper;
import com.boge.update.common.RadiusEnum;
import com.google.gson.Gson;

import java.io.File;

import okhttp3.Call;
import wendu.dsbridge.RequestCallBack;
import wendu.dsbridge.base.BaseFm;
import wendu.dsbridge.helper.UpdateResponse;
import wendu.dsbridge.net.OkHttpUtil;

/**
 * 基础更新 遮罩层
 *
 * @author: admin
 * @date: 2023/4/12
 */
public abstract class BaseUpdateMaskFragment extends BaseFm {

    private File file;


    private static final int REQUEST_CODE_INSTALL_PERMISSION = 0x4576;
    /**
     * 检查更新调用的url地址
     */
    private String updateUrl = "";

    /**
     * 设置 fragment 的核心容器
     *
     * @return
     */
    @LayoutRes
    protected abstract int initFragmentContentView();

    /**
     * @param viewRoot
     * @param savedInstanceState
     */
    protected abstract void onCreateView(View viewRoot, Bundle savedInstanceState);


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    /**
     * 设置更新的文字配置
     */
    protected void setText(String versionName, String description, String url) {
        show();
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    /**
     * 检查是否存在更新
     */
    public void executeUpdate(String updateUrl) {
        this.updateUrl = updateUrl;
        if (!TextUtils.isEmpty(updateUrl)) {
            OkHttpUtil.getInstance().get(updateUrl).execute(new RequestCallBack() {
                @Override
                public void failed(Call call, Exception e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            hideFragment(BaseUpdateMaskFragment.this);
                        }
                    });
                    Log.e("Update:", "更新失败，请检查" + e.getMessage());
                }

                @Override
                public void success(int code, Call call, String body) {
                    try {
                        PackageInfo packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                        UpdateResponse updateBean = new Gson().fromJson(body, UpdateResponse.class);
                        //从远程地址处获取 code
                        int versionCode = Integer.parseInt(updateBean.getData().getVersionCode());
                        //如果服务器上的版本号，大于本地的版本号，说明是更新
                        if (versionCode > packageInfo.versionCode) {
                            //更新的版本名称
                            String versionName = updateBean.getData().getVersionName();
                            //更新的版本描述
                            String description = updateBean.getData().getDescription();
                            //更新的新版本 APK 下载地址
                            String url = updateBean.getData().getUrl();

                            if (!TextUtils.isEmpty(url)) {
                                handler.post(() -> {
                                    //按照顺序 将服务器返回的信息 添加进
                                    setText(versionName, description, url);
                                    new DownloadWrapper(getActivity(), url, false, RadiusEnum.UPDATE_RADIUS_30).start();
                                });
                            }
                        }

                    } catch (PackageManager.NameNotFoundException e) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                hideFragment(BaseUpdateMaskFragment.this);
                            }
                        });
                        e.printStackTrace();
                    } catch (Exception e) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                hideFragment(BaseUpdateMaskFragment.this);
                            }
                        });
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    // 检查是否拥有安装未知来源应用的权限
    public boolean hasInstallPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return context.getPackageManager().canRequestPackageInstalls();
        }
        return true;
    }

    // 请求安装未知来源应用的权限
    public void requestInstallPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Uri packageUri = Uri.parse("package:" + activity.getPackageName());
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageUri);
            activity.startActivityForResult(intent, REQUEST_CODE_INSTALL_PERMISSION);
        }
    }


    // 安装 APK
    public void installApk(Context context, String apkFilePath) {
        File apkFile = new File(apkFilePath);
        if (apkFile.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".FileProvider", apkFile);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    // 安装 APK
    public void installApk(Context context, File apkFile) {
        if (apkFile.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".FileProvider", apkFile);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_INSTALL_PERMISSION) {
            if (resultCode == RESULT_OK) {
                // 用户同意安装未知来源应用，继续安装 APK
                installApk(getActivity(), file);
            } else {
                // 用户拒绝安装未知来源应用，提示用户手动授权并导航到设置页面
                showToast("请授予安装应用权限");
                requestInstallPermission(getActivity());
            }
        }
    }



}
