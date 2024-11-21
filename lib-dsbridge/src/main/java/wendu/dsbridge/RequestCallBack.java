package wendu.dsbridge;

import okhttp3.Call;

/**
 * @author: admin
 * @date: 2023/4/24
 */
public interface RequestCallBack {

    void failed(Call call, Exception e);

    void success(int code,Call call, String body);
}
