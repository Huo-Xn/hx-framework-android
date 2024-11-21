package wendu.dsbridge.helper;

import java.util.HashMap;

/**
 * @author: admin
 * @date: 2023/4/24
 */
public class UpdateRequest {

    private String url;
    private RequestType requestType;
    private HashMap<String, Object> param;
    private HashMap<String, String> header;

    public UpdateRequest() {
        param = new HashMap<>();
        header = new HashMap<>();
    }

    /**
     * 设置url
     *
     * @param url
     * @return
     */
    public UpdateRequest setUrl(String url) {
        this.url = url;
        return this;
    }

    /**
     * 设置 请求类型
     *
     * @param requestType
     * @return
     */
    public UpdateRequest setRequestType(RequestType requestType) {
        this.requestType = requestType;
        return this;
    }

    /**
     * 添加请求参数
     *
     * @param key
     * @param value
     */
    public UpdateRequest setParam(String key, Object value) {
        if (null != param) {
            param.put(key, value);
        } else {
            param = new HashMap<>();
            param.put(key, value);
        }
        return this;
    }

    /**
     * 添加请求头
     *
     * @param key
     * @param value
     */
    public UpdateRequest setHeader(String key, String value) {
        if (null != header) {
            header.put(key, value);
        } else {
            header = new HashMap<>();
            header.put(key, value);
        }
        return this;
    }


}
