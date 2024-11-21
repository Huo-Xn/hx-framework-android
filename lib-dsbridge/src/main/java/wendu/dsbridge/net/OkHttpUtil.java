package wendu.dsbridge.net;

import android.text.TextUtils;

import androidx.lifecycle.LifecycleObserver;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import wendu.dsbridge.RequestCallBack;
import wendu.dsbridge.helper.RequestType;

public class OkHttpUtil implements LifecycleObserver {


    private static volatile OkHttpUtil mInstance = new OkHttpUtil();
    private HashMap<String, String> communalHeader;
    private OkHttpClient.Builder builder;
    private OkHttpClient mClient;

    public static OkHttpUtil getInstance() {
        if (null == mInstance) {
            mInstance = new OkHttpUtil();
        }
        return mInstance;
    }


    private OkHttpUtil() {
        builder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);
        communalHeader = new HashMap<>();
    }

    /**
     * 设置链接超时时间
     *
     * @param timeout 链接超时时间
     *                min 10
     */
    public OkHttpUtil setConnectTimeout(long timeout) {
        if (timeout <= 0) {
            timeout = 10;
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
    public OkHttpUtil setReadTimeout(long timeout) {
        if (timeout <= 0) {
            timeout = 10;
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
    public OkHttpUtil setWriteTimeout(long timeout) {
        if (timeout <= 0) {
            timeout = 10;
        }
        builder.writeTimeout(timeout, TimeUnit.SECONDS);
        return mInstance;
    }


    /**
     * 添加一个拦截器
     *
     * @param interceptor
     */
    public OkHttpUtil setWriteTimeout(Interceptor interceptor) {
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
    public OkHttpUtil addCommunalHeader(String key, String val) {
        communalHeader.put(key, val);
        return mInstance;
    }

    /**
     * 清空一个公共的请求头
     *
     * @return
     */
    public OkHttpUtil clearCommunalHeader() {
        communalHeader.clear();
        return mInstance;
    }

    /**
     * get请求
     *
     * @param url
     * @return
     */
    public MyRequest get(String url) {
        mClient = builder.build();
        return new MyRequest(RequestType.GET, url);
    }

    /**
     * get请求
     *
     * @param url
     * @return
     */
    public MyRequest post(String url) {
        mClient = builder.build();
        return new MyRequest(RequestType.POST, url);
    }


    public class MyRequest {

        private final String APPLICATION_JSON = "application/json; charset=utf-8";

        private String contentType = "";
        private ArrayList<StringBuffer> queryList;
        private HashMap<String, Object> bodyMap;
        private String myJson = "";
        private String url = "";
        private Request.Builder builder;
        private RequestType requestType;
        private RequestBody requestBody;
        private Call call;
        private boolean notUsedCommunalHeader = false;


        private MyRequest() {
            queryList = new ArrayList<>();
        }

        private MyRequest(RequestType requestType, String url) {
            this.url = url;
            this.requestType = requestType;
            queryList = new ArrayList<>();
            builder = new Request.Builder();
        }


        /**
         * 替换url 中的 路径方式拼接的参数
         * 如： http://www.baidu.con/laogin/{name}
         *
         * @param paramkey 如：  http://www.baidu.con/laogin/{name}   ，就需要传递   ”name“
         * @param value    替代这个 name的值
         * @return
         */
        public MyRequest replaceUrlParam(String paramkey, String value) {
            if (!TextUtils.isEmpty(paramkey)  &&   !TextUtils.isEmpty(value)   ){
                url = url.replaceAll("\\{" + paramkey + "\\}", value);
            }
            return this;
        }


        /**
         * 添加 header
         *
         * @param key
         * @param value
         */
        public MyRequest addHeader(String key, String value) {
            if (!TextUtils.isEmpty(key)) {
                builder.addHeader(key, value);
            }
            return this;
        }

        /**
         * 添加 query
         *
         * @return
         */
        public MyRequest addQueryBody(String key, String value) {
            if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                StringBuffer queryBuffer = new StringBuffer();
                queryBuffer
                        .append(key)
                        .append("=")
                        .append(value);
                queryList.add(queryBuffer);
            }
            return this;
        }

        /**
         * 添加 Body
         *
         * @return
         */
        public MyRequest addBody(String key, String value) {
            if (null == bodyMap) {
                bodyMap = new HashMap<>();
            }
            if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                bodyMap.put(key, value);
            }
            myJson = "";
            return this;
        }

        /**
         * 添加Body
         *
         * @return
         */
        public MyRequest setJsonBody(String josn) {
            if (!TextUtils.isEmpty(josn)) {
                myJson = josn;
            }
            bodyMap.clear();
            return this;
        }

        /**
         * 添加Body
         *
         * @return
         */
        public MyRequest setBody(Object o) {
            if (null != o) {
                myJson = new Gson().toJson(o);
            }
            bodyMap.clear();
            return this;
        }

        /**
         * 取消请求
         */
        public void cancel() {
            if (null != call) {
                call.cancel();
            }
        }

        /**
         * 是否使用公共的请求头
         *
         * @param notUsedCommunalHeader
         */
        public MyRequest setNotUsedCommunalHeader(boolean notUsedCommunalHeader) {
            this.notUsedCommunalHeader = notUsedCommunalHeader;
            return this;
        }

        /**
         * 执行get
         *
         * @param callBack
         */
        public void execute(RequestCallBack callBack) {
            if (!notUsedCommunalHeader) {
                if (communalHeader.size() > 0) {
                    for (Map.Entry<String, String> stringStringEntry : communalHeader.entrySet()) {
                        builder.addHeader(stringStringEntry.getKey(), stringStringEntry.getValue());
                    }
                }
            }
            if (requestType == RequestType.GET) {
                builder.get();
            } else if (requestType == RequestType.POST) {
                if (TextUtils.isEmpty(contentType)) {
                    contentType = APPLICATION_JSON;
                }

                if (null != bodyMap && bodyMap.size() > 0) {
                    myJson = new Gson().toJson(bodyMap);
                }
                RequestBody requestBody = RequestBody.create(MediaType.parse(contentType), myJson);
                builder.post(requestBody);
            }
            StringBuffer queryStr = new StringBuffer();
            if (null != queryList && queryList.size() > 0) {
                for (int i = 0; i < queryList.size(); i++) {
                    if (i == 0) {
                        queryStr.append("?");
                        queryStr.append(queryList.get(i));
                    } else {
                        queryStr.append("&");
                        queryStr.append(queryList.get(i));
                    }
                }
            }
            builder.url(url + queryStr);
            Request request = builder.build();
            call = mClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (null != callBack) {
                        callBack.failed(call, e);
                    }
                }

                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        if (null != response) {
                            ResponseBody body = response.body();
                            if (null != body) {
                                if (null != callBack) {
                                    callBack.success(response.code(), call,body.string());
                                }
                            }
                        }
                    } catch (Exception e) {
                        if (null != callBack) {
                            callBack.failed(call, e);
                        }
                        e.printStackTrace();
                    }
                }
            });
        }


    }


}

//日志拦截器，可加可不加
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);