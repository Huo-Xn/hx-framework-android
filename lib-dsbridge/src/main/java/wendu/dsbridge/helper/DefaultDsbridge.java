package wendu.dsbridge.helper;

import static wendu.dsbridge.AndroidToH5Action.FROMANDROIDONBACKPRESSED;

import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import java.util.HashMap;
import java.util.Map;

import wendu.dsbridge.PriorityBackPressedListener;
import wendu.dsbridge.FromH5MessageCallBack;
import wendu.dsbridge.dwebview.DWebView;
import wendu.dsbridge.tool.GsonUtil;

/**
 * 默认交互
 *
 * @author: admin
 * @date: 2023/4/20
 */
public class DefaultDsbridge implements PriorityBackPressedListener {

    /**
     * 来自h5方面的消息
     */
    private HashMap<String, FromH5MessageCallBack> fromH5MessageCallBacks;

    /**
     * webview
     */
    private final DWebView myWebview;


    /**
     * 占用 返回
     */
    private boolean occupyBack = false;

    public DefaultDsbridge(DWebView myWebview) {
        fromH5MessageCallBacks = new HashMap<>();
        this.myWebview = myWebview;
    }


    /**
     * 向前端 发送消息
     *
     * @param methodNmae 约定方法名 如：‘putData’
     *                   JS接收：dsBridge.register('putData', function (data) {
     *                   alert(data)
     *                   })
     * @param json       传递的数据
     */
    public void sendMessageToH5(String methodNmae, String json) {
        myWebview.callHandler(methodNmae, json);
    }

    /**
     * 来自 前端 发送过来的消息
     *
     * @param methodNmae            约定方法名 如：‘openMessage’
     *                              JS：bridge.call("senMessage",key, json);
     *                              JS：senMessage('openMessage','9');
     * @param fromH5MessageCallBack 回调函数
     */
    public void fromH5Message(String methodNmae, FromH5MessageCallBack fromH5MessageCallBack) {
        fromH5MessageCallBacks.put(methodNmae, fromH5MessageCallBack);
    }


    /**
     * 万金油/任意门 由前端H5 向 Android 传递信息
     *
     * @param obj 方法action 如：‘open***’
     *            例：  - JS: function sendMessage(key, json) {
     *            var obj = {
     *            methodNmae: key,
     *            data:json
     *            }
     *            var data = JSON.stringify(obj);
     *            bridge.call("sendMessage", data);
     *            }
     *            - JS:  <button onclick="sendMessage('openMessage','9')">打开相册多选（max9）</button>
     * @param
     */
    @JavascriptInterface
    public void anyDoor(Object obj) {
        if (null != obj) {
            Map<String, String> map = GsonUtil.toMap(obj.toString());
            String keyName = map.get("methodNmae");
            if (!TextUtils.isEmpty(keyName)) {
                FromH5MessageCallBack fromH5MessageCallBack = fromH5MessageCallBacks.get(keyName);
                String data = map.get("data");
                if (null != data) {
                    if (null != fromH5MessageCallBack) {
                        fromH5MessageCallBack.fromH5Message(data);
                    }
                } else {
                    if (null != fromH5MessageCallBack) {
                        fromH5MessageCallBack.fromH5Message("");
                    }
                }
            }
        }
    }


    /**
     * 自定义返回键的事件处理
     *
     * @param obj
     */
    @JavascriptInterface
    public void androidBack(Object obj) {
        if (null != obj) {
            String b = obj.toString();
            if (!TextUtils.isEmpty(b)) {
                if (TextUtils.equals("1", b) || TextUtils.equals("true", b)) {
                    occupyBack = true;
                }
                if (TextUtils.equals("0", b) || TextUtils.equals("false", b)) {
                    occupyBack = false;
                }
            }
        }
    }


    @Override
    public boolean onPriorityBackPressed() {
        if (occupyBack) {
            //来自Android 的 按了返回键之后所触发的事件
            String json = GsonUtil.mapToJson(new String[]{"url", myWebview.getUrl()});
            sendMessageToH5(FROMANDROIDONBACKPRESSED, json);
        }
        return occupyBack;
    }
}
