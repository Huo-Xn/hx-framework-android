package wendu.dsbridge.fragment;

import static com.hjq.permissions.XXPermissions.REQUEST_CODE;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.heiko.stripeprogressbar.StripeProgressBar;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.io.File;
import java.util.List;

import okhttp3.Call;
import wendu.dsbridge.DownloadListener;
import wendu.dsbridge.PermissionsCallback;
import wendu.dsbridge.R;
import wendu.dsbridge.RequestCallBack;
import wendu.dsbridge.helper.UpdateResponse;
import wendu.dsbridge.net.DownloadUtil;
import wendu.dsbridge.net.OkHttpUtil;

/**
 * 默认的 升级遮罩层
 *
 * @author: admin
 * @date: 2023/4/13
 */
public class DefaultUpdateFragment extends BaseUpdateMaskFragment implements DownloadListener {

    /**
     * 更新
     */
    private ImageView mUpdateImg;
    private FrameLayout mUpdateView;
    private TextView mUpdateDescription, mProgressTv, mInstall, mVersion,mDownload;
    private StripeProgressBar progressBar;
    private Handler handler;
    private String updateUrl;
    private String downloadUrl;

    @Override
    protected int initFragmentContentView() {
        return R.layout.layout_update;
    }


    @Override
    protected void onCreateView(View viewRoot, Bundle savedInstanceState) {
        handler = new Handler(Looper.myLooper());
        mUpdateView = $(R.id.base_myupdateview);
        mProgressTv = $(R.id.base_myupdateview_progress);
        mInstall = $(R.id.base_myupdateview_install);
        mVersion = $(R.id.base_myupdateview_version);
        mDownload = $(R.id.base_myupdateview_download);
        mUpdateImg = $(R.id.base_myupdateview_img);
        progressBar = $(R.id.base_myupdateview_progressBar);
        mUpdateDescription = $(R.id.base_myupdateview_description);
        Glide.with(this).load(R.mipmap.icon_huojian).into(mUpdateImg);



        mInstall.setOnClickListener(v -> {
            if (hasInstallPermission(getActivity())) {
                installApk(getActivity(), getFile());
            } else {
                requestInstallPermission(getActivity());
            }
        });


        mDownload.setOnClickListener(v -> {
            readAndWrite(downloadUrl);
        });

    }

    @Override
    protected void setText(String versionName, String description, String url) {
        super.setText(versionName, description, url);
        mUpdateDescription.setText(description + "");
        mVersion.setText(versionName + "");
    }


    @Override
    public void executeUpdate(String updateUrl) {
        this.updateUrl = updateUrl;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(updateUrl)) {
                    OkHttpUtil.getInstance().get(updateUrl).execute(new RequestCallBack() {
                        @Override
                        public void failed(Call call, Exception e) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    showToast("更新出错");
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
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                setText(versionName, description, url);
                                                show();
                                                downloadUrl = url;
                                                readAndWrite(url);
                                            }
                                        });
                                    }
                                }

                            } catch (PackageManager.NameNotFoundException e) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        showToast("更新出错");
                                    }
                                });
                                e.printStackTrace();
                            } catch (Exception e) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        showToast("更新出错");
                                    }
                                });
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }, 1000);
    }

    @Override
    public void onDownloadSuccess(File file) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                mInstall.setVisibility(View.VISIBLE);
                mDownload.setVisibility(View.GONE);
                mProgressTv.setText("完成");
            }
        });
        handler.postDelayed(() -> {
            setFile(file);
            if (hasInstallPermission(getActivity())) {
                installApk(getActivity(), file);
            } else {
                requestInstallPermission(getActivity());
            }
        }, 2000);
    }

    @Override
    public void onDownloading(int progress) {
        handler.post(() -> {
            progressBar.setProgress(progress);
            mProgressTv.setText(progress + "%");
        });
    }

    @Override
    public void onDownloadFailed(Exception e) {
        handler.post(() -> {
            showToast("下载时出错" + e.getMessage());
            progressBar.setProgress(0);
        });
    }


    /**
     * 获取 读写 权限
     */
    public void readAndWrite(String url) {
        String[] storage;
        if (Build.VERSION.SDK_INT >= 29) {
            storage = new String[]{Permission.MANAGE_EXTERNAL_STORAGE};
        } else {
            if (Build.VERSION.SDK_INT <= 23) {
                int permission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    String[] PERMISSIONS_NAME = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    //没有申请相应的权限就进行权限的申请，如下所示：
                    ActivityCompat.requestPermissions((Activity) getActivity(), PERMISSIONS_NAME, REQUEST_CODE);
                    Toast.makeText(getActivity(), "请在赋予权限之后，再次点击功能", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    DownloadUtil.get().download(url, Environment.getExternalStorageDirectory().getPath() + "/sx_download", "update", DefaultUpdateFragment.this);
                    return;
                }
            } else {
                storage = new String[]{Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE};
            }
        }


        if (XXPermissions.isGranted(getActivity(), storage)) {
            DownloadUtil.get().download(url, Environment.getExternalStorageDirectory().getPath() + "/sx_download", "update", DefaultUpdateFragment.this);
        } else {
            XXPermissions
                    .with(getActivity())
                    .permission(storage)
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(List<String> permissions, boolean all) {
                            if (all) {
                                DownloadUtil.get().download(url, Environment.getExternalStorageDirectory().getPath() + "/sx_download", "update", DefaultUpdateFragment.this);
                            }else {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mDownload.setVisibility(View.VISIBLE);
                                        mInstall.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onDenied(List<String> permissions, boolean never) {
                            if (never) {
                                handler.post(() -> {
                                    showApply("读写").show();
                                });
                            }
                        }
                    });
        }
    }


    /**
     * 弹出对话框
     *
     * @param PermissionName
     */
    private AlertView showApply(String PermissionName) {

        AlertView.Builder builder = new AlertView.Builder()
                .setContext(getActivity())
                .setMessage("您永久的拒绝了" + PermissionName + "权限,需要您前往设置中去手动开启")
                .setStyle(AlertView.Style.Alert)
                .setTitle("权限申请")
                .setDestructive("去开启", "拒绝")
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {
                        AlertView oa = (AlertView) o;
                        switch (position) {
                            case 0:
                                getActivity().startActivity(getAppDetailSettingIntent());
                                break;
                            case 1:
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mDownload.setVisibility(View.VISIBLE);
                                        mInstall.setVisibility(View.GONE);
                                    }
                                });
                                break;
                        }
                        oa.dismiss();
                    }
                });
        return builder.build();
    }

    /**
     * 获取应用详情页面intent（如果找不到要跳转的界面，也可以先把用户引导到系统设置页面）
     *
     * @return
     */
    private Intent getAppDetailSettingIntent() {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getActivity().getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", getActivity().getPackageName());
        }
        return localIntent;
    }
}
