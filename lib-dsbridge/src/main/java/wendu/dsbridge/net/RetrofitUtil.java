package wendu.dsbridge.net;

import android.text.TextUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @description:
 * @author: ash
 * @date : 2021/7/8 17:24
 * @email : ash_945@126.com
 */
public class RetrofitUtil implements Interceptor {


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        if (communalHeader.size() > 0) {
            for (Map.Entry<String, String> stringStringEntry : communalHeader.entrySet()) {
                builder.addHeader(stringStringEntry.getKey(), stringStringEntry.getValue());
            }
        }
        Request request = builder.build();
        Response response = chain.proceed(request);
        return response;
    }

    private static volatile RetrofitUtil mInstance = new RetrofitUtil();
    private OkHttpClient.Builder builder;
    private Retrofit.Builder retrofitBuilder;
    private HashMap<String, String> communalHeader;


    public static RetrofitUtil getInstance() {
        if (mInstance == null) {
            mInstance = new RetrofitUtil();
        }
        return mInstance;
    }


    private RetrofitUtil() {
        communalHeader = new HashMap<>();

        builder = new OkHttpClient.Builder()
                .addInterceptor(this)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS);

        OkHttpClient client = builder.build();

        retrofitBuilder = new Retrofit.Builder()
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create());
    }

    /**
     * 添加一个拦截器
     *
     * @param interceptor
     */
    public RetrofitUtil setWriteTimeout(Interceptor interceptor) {
        builder.addInterceptor(interceptor);
        return mInstance;
    }

    /**
     * 添加一个公共的请求头
     *
     * @param key
     * @param val
     * @return
     */
    public RetrofitUtil addCommunalHeader(String key, String val) {
        communalHeader.put(key, val);
        return mInstance;
    }

    /**
     * 清空一个公共的请求头
     *
     * @return
     */
    public RetrofitUtil clearCommunalHeader() {
        communalHeader.clear();
        return mInstance;
    }


    /**
     * 设置 baseUrl
     *
     * @param baseUrl
     */
    public RetrofitUtil setBaseUrl(String baseUrl) {
        if (TextUtils.isEmpty(baseUrl)) {
            return mInstance;
        }
        retrofitBuilder.baseUrl(baseUrl);
        return mInstance;
    }


    /**
     * 设置链接超时时间
     *
     * @param timeout 链接超时时间
     *                min 10
     */
    public RetrofitUtil setConnectTimeout(long timeout) {
        if (timeout <= 0) {
            timeout = 30;
        }
        builder.connectTimeout(timeout, TimeUnit.SECONDS);
        return mInstance;
    }

    /**
     * 设置读取超时时间
     *
     * @param timeout 读取超时时间
     *                min 10
     */
    public RetrofitUtil setReadTimeout(long timeout) {
        if (timeout <= 0) {
            timeout = 30;
        }
        builder.readTimeout(timeout, TimeUnit.SECONDS);
        return mInstance;
    }

    /**
     * 设置写入超时时间
     *
     * @param timeout 写入超时时间
     *                min 10
     */
    public RetrofitUtil setWriteTimeout(long timeout) {
        if (timeout <= 0) {
            timeout = 30;
        }
        builder.writeTimeout(timeout, TimeUnit.SECONDS);
        return mInstance;
    }


    public <T> T getService(Class<T> service) {
        Retrofit build = retrofitBuilder.build();
        return build.create(service);
    }




}
