package wendu.dsbridge.tool;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import wendu.dsbridge.LocationListener;

/**
 * @author: admin
 * @date: 2022/8/17
 */
public class LocationManager implements LifecycleObserver {

    private LocationListener locationListener;
    private LocationListener onecLocationListener;
    private boolean onec = false;
    private static volatile LocationManager mInstance = new LocationManager();
    private OkHttpClient mClient;
    public double latitude;
    public double longitude;
    public String province;
    public String city;
    public String district;
    public String streetNumber;


    public static LocationManager getInstance() {
        return mInstance;
    }

    private Handler handler;

    private LocationManager() {
        handler = new Handler(Looper.getMainLooper());
    }


    public void init(Context context) {

        try {
            mLocationClient = new AMapLocationClient(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mLocationClient.setLocationListener(mapLocationListener);
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setInterval(5000);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        if (null != mLocationClient) {
            mLocationClient.setLocationOption(mLocationOption);
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }
    }

    public AMapLocationClient mLocationClient = null;
    public AMapLocationListener mapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    latitude = aMapLocation.getLatitude();
                    longitude = aMapLocation.getLongitude();
                    province = aMapLocation.getProvince();
                    city = aMapLocation.getCity();
                    district = aMapLocation.getDistrict();
                    streetNumber = aMapLocation.getStreetNum();
                    String text = "经度: " + longitude + "\n"
                            + "纬度: " + latitude + "\n"
                            + "详细位置: " + province + city + district + streetNumber;
                    Log.d("查看", text);
                    if (null != locationListener) {
                        locationListener.onLocationChanged();
                    }
                    if (null != onecLocationListener) {
                        if (onec) {
                            onecLocationListener.onLocationChanged();
                            onec = false;
                        }
                    }
                } else {
                    Log.e("AmapError", "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        }
    };
    public AMapLocationClientOption mLocationOption = null;


    public String sHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length() - 1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setOnceLocationListener(LocationListener locationListener) {
        this.onecLocationListener = locationListener;
        onec = true;
    }

    public void setLocationListener(LocationListener locationListener) {
        this.locationListener = locationListener;
    }

    public void removeOnceLocationListener() {
        this.onecLocationListener = null;
        onec = false;
    }

    public void removeLocationListener() {
        this.locationListener = null;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    protected void onDestroy() {
        mLocationClient.onDestroy();
    }


}
