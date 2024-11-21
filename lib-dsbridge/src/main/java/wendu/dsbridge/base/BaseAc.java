package wendu.dsbridge.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import wendu.dsbridge.NetworkLisener;
import wendu.dsbridge.receiver.NetWorkBroadcastReceiver;
import wendu.dsbridge.tool.PermissionsManager;
import wendu.dsbridge.view.NetworkAnomalyBox;


/**
 * @description: 与 uniapp 一致
 * @author: ash
 * @date : 2022/3/16 9:23
 * @email : ash_945@126.com
 */
public abstract class BaseAc extends AppCompatActivity implements NetworkLisener {

    /**
     * 网络监听广播接收者
     */
    protected static NetWorkBroadcastReceiver netWorkBroadcastReceiver;
    /**
     * 吐司
     */
    protected Toast mToast;

    /**
     * 网络异常监听
     */
    protected NetworkAnomalyBox networkAnomalyBox;
    /**
     * 权限管理
     */
    protected PermissionsManager permissionsManager;


    /**
     * 通信必要组件
     */
    protected Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler(Looper.getMainLooper());
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        //创建网络监听
        netWorkBroadcastReceiver = new NetWorkBroadcastReceiver(this);
        netWorkBroadcastReceiver.init();
        netWorkBroadcastReceiver.addNetworkLisener(this);
        permissionsManager = new PermissionsManager(this);
        getLifecycle().addObserver(netWorkBroadcastReceiver);
        getLifecycle().addObserver(permissionsManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (null == networkAnomalyBox) {
            networkAnomalyBox = new NetworkAnomalyBox(this);
        }
    }

    /**
     * 添加 fragment
     *
     * @param containerViewId 容器
     * @param fragment
     */
    public void addFragment(@IdRes int containerViewId, @NonNull Fragment fragment) {
        if (null == fragment) {
            return;
        }
        getSupportFragmentManager()
                .beginTransaction()
                .add(containerViewId, fragment)
                .commitAllowingStateLoss();
    }

    /**
     * 向根布局添加 fragment
     *
     * @param fragment
     */
    public void addFragmentToContent(@NonNull Fragment fragment) {
        addFragment(android.R.id.content, fragment);
    }


    /**
     * 添加 fragment
     *
     * @param containerViewId 容器
     * @param fragment
     * @param fragmentNmae    “your fragment's name”
     */
    public void addFragment(@IdRes int containerViewId, @NonNull Fragment fragment, String fragmentNmae) {
        if (null == fragment) {
            return;
        }
        getSupportFragmentManager()
                .beginTransaction()
                .add(containerViewId, fragment, fragmentNmae)
                .commitAllowingStateLoss();
    }

    /**
     * 向根布局添加 fragment
     *
     * @param fragment
     * @param fragmentNmae “your fragment's name”
     */
    public void addFragmentToContent(@NonNull Fragment fragment, String fragmentNmae) {
        addFragment(android.R.id.content, fragment, fragmentNmae);
    }

    /**
     * 移除fragment
     *
     * @param fragment
     */
    public void removeFragment(@NonNull Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(fragment)
                .commitAllowingStateLoss();

    }


    /**
     * 替换fragment
     *
     * @param containerViewId
     * @param fragment
     */
    public void replaceFragment(@IdRes int containerViewId, @NonNull Fragment fragment) {
        if (null == fragment) {
            return;
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(containerViewId, fragment)
                .commitAllowingStateLoss();
    }

    /**
     * 替换fragment
     *
     * @param fragment
     */
    public void replaceFragmentToContent(@NonNull Fragment fragment) {
        if (null == fragment) {
            return;
        }
        replaceFragment(android.R.id.content, fragment);
    }

    /**
     * 显示fragment
     *
     * @param fragment
     */
    public void showFragment(@NonNull Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .show(fragment)
                .commitAllowingStateLoss();
    }

    /**
     * 隐藏fragment
     *
     * @param fragment
     */
    public void hideFragment(@NonNull Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .hide(fragment)
                .commitAllowingStateLoss();
    }


    /**
     * 网络情况监听
     *
     * @param network 是否存在网络
     * @param type    网络类型
     */
    @Override
    public void networkStatus(boolean network, String type) {
        if (network) {
            runOnUiThread(() -> {
                networkAnomalyBox.dismiss();
            });
        } else {
            runOnUiThread(() -> {
                networkAnomalyBox.show();
            });
        }
    }


    public void showToast(CharSequence text) {
        runOnUiThread(() -> {
                    if (mToast != null) {
                        mToast.cancel();
                        mToast = null;
                    }
                    mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
                    mToast.show();
                }
        );
    }


    //获取状态栏的高度
    public int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }


    /**
     * 获取跟布局
     *
     * @return
     */
    protected View getContentView() {
        View view = findViewById(android.R.id.content);
        return view;
    }


    /**
     * 自定义一个 查找id 的方法
     *
     * @param id
     * @param <T>
     * @return
     */
    public <T extends View> T $(int id) {
        return super.findViewById(id);
    }


    //记录用户首次点击返回键的时间
    private long firstTime = 0;

    /**
     * 点击两次返回退出
     */
    protected void doubleClickFinish() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) {
            showToast("再次点击退出程序");
            firstTime = secondTime;
        } else {
            finish();
        }
    }


}



