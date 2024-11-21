package wendu.dsbridge.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import java.util.ArrayList;
import java.util.HashMap;

import wendu.dsbridge.NetworkLisener;

/**
 * @author: admin
 * @date: 2022/6/21
 * <p>
 * 网络监听 广播接受者
 */
public class NetWorkBroadcastReceiver extends BroadcastReceiver implements LifecycleObserver {


    private Context context;

    private final ArrayList<NetworkLisener> networkLiseners;

    public NetWorkBroadcastReceiver(Context context) {
        this.context = context;
        networkLiseners = new ArrayList<>();
    }

    public void addNetworkLisener(NetworkLisener lisener) {
        networkLiseners.add(lisener);
    }

    /**
     * 初始化
     */
    public void init() {
        //创建网络情况监听
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        if (null != context) {
            context.registerReceiver(this, intentFilter);
        }
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        //每当 网络发生变化该方法就会被调用一次
        //每次该方法启动，就调用一次网络检查方法重新检查一次网络类型
        getNetWorkType(context);

    }


    /**
     * 注销
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void unregisterReceiver() {
        if (null != context) {
            context.unregisterReceiver(this);
        }
    }


    /**
     * 获取网络类型
     *
     * @param context
     * @return
     */
    private int getNetWorkType(Context context) {
        int netType = 0;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            //没有网络
            for (NetworkLisener networkLisener : networkLiseners) {
                if (null != networkLisener) {
                    networkLisener.networkStatus(false, "无网络");
                }
            }

            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = 1;
            // wifi
            for (NetworkLisener networkLisener : networkLiseners) {
                if (null != networkLisener) {
                    networkLisener.networkStatus(true, "wifi");
                }
            }
        } else if (nType == ConnectivityManager.TYPE_MOBILE) {
            int nSubType = networkInfo.getSubtype();
            TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (nSubType == TelephonyManager.NETWORK_TYPE_UMTS && !mTelephony.isNetworkRoaming()) {
                netType = 2;// 3G
            } else if (nSubType == TelephonyManager.NETWORK_TYPE_LTE && !mTelephony.isNetworkRoaming()) {
                netType = 4;// 4G
            } else {
                netType = 3;// 2G
            }
            for (NetworkLisener networkLisener : networkLiseners) {
                if (null != networkLisener) {
                    networkLisener.networkStatus(true, "移动网络");
                }
            }
        }
        return netType;
    }


}
