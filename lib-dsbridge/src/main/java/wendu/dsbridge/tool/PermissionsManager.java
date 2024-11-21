package wendu.dsbridge.tool;

import static com.hjq.permissions.XXPermissions.REQUEST_CODE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.ArrayList;
import java.util.List;

import wendu.dsbridge.PermissionsCallback;

public class PermissionsManager implements LifecycleObserver {

    private Handler handler;
    private Context context;

    public PermissionsManager(Context context) {
        handler = new Handler(Looper.getMainLooper());
        this.context = context;
    }





    /**
     * 获取 读写 权限
     *
     * @param callback
     */
    public void readAndWrite(PermissionsCallback callback) {
        String[] storage;
        if (Build.VERSION.SDK_INT >= 29) {
            storage = new String[]{Permission.MANAGE_EXTERNAL_STORAGE};
        } else {
            if (Build.VERSION.SDK_INT <= 23) {
                int permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    String[] PERMISSIONS_NAME = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    //没有申请相应的权限就进行权限的申请，如下所示：
                    ActivityCompat.requestPermissions((Activity) context, PERMISSIONS_NAME, REQUEST_CODE);
                    Toast.makeText(context, "请在赋予权限之后，再次点击功能", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (null != callback) {
                        callback.granted();
                    }
                    return;
                }
            } else {
                storage = new String[]{Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE};
            }
        }


        if (XXPermissions.isGranted(context, storage)) {
            if (null != callback) {
                callback.granted();
            }
        } else {
            XXPermissions
                    .with(context)
                    .permission(storage)
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(List<String> permissions, boolean all) {
                            if (all) {
                                if (null != callback) {
                                    callback.granted();
                                }
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
     * 获取相机权限
     *
     * @param callback
     */
    public void camera(PermissionsCallback callback) {
        String[] permissions = {Permission.CAMERA};

        if (XXPermissions.isGranted(context, permissions)) {
            if (null != callback) {
                callback.granted();
            }
        } else {
            XXPermissions
                    .with(context)
                    .permission(permissions)
                    .request(new OnPermissionCallback() {
                        @Override
                        public void onGranted(List<String> permissions, boolean all) {
                            if (all) {
                                if (null != callback) {
                                    callback.granted();
                                }
                            }
                        }

                        @Override
                        public void onDenied(List<String> permissions, boolean never) {
                            if (never) {
                                handler.post(() -> {
                                    showApply("相机").show();
                                });
                            }
                        }
                    });
        }

    }


    /**
     * 定位权限
     *
     * @param callback
     */
    public void locationPermissions(PermissionsCallback callback) {
        String[] permissions = {Permission.ACCESS_FINE_LOCATION, Permission.ACCESS_COARSE_LOCATION};

        if (XXPermissions.isGranted(context, permissions)) {
            if (null != callback) {
                callback.granted();
            }
        } else {
            XXPermissions
                    .with(context)
                    .permission(permissions)
                    .request(new OnPermissionCallback() {
                        @Override
                        public void onDenied(List<String> permissions, boolean never) {
                            if (never) {
                                handler.post(() -> {
                                    showApply("定位").show();
                                });
                            }
                        }

                        @Override
                        public void onGranted(List<String> permissions, boolean all) {
                            if (all) {
                                if (null != callback) {
                                    callback.granted();
                                }
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
                .setContext(context)
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
                                context.startActivity(getAppDetailSettingIntent());
                                break;
                            case 1:
                                break;
                        }
                        oa.dismiss();
                    }
                });
        return builder.build();
    }


    //判断手机上是否安装了指定的百度地图，高德地图等软件
    private boolean isAvilible(String packageName) {
        // 获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        // 用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        // 从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        // 判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
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
            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        return localIntent;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        handler = null;
        context = null;
    }

}
