package wendu.dsbridge.fragment;

import static wendu.dsbridge.AndroidToH5Action.FROMANDROIDPHOTO;
import static wendu.dsbridge.AndroidToH5Action.FROMANDROIDQRCCODE;
import static wendu.dsbridge.AndroidToH5Action.FROMCHOOSEFILE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.luck.picture.lib.config.PictureConfig;

import wendu.dsbridge.helper.BackPriority;
import wendu.dsbridge.FileResultCallback;
import wendu.dsbridge.FromH5MessageCallBack;
import wendu.dsbridge.PhotoResultCallback;
import wendu.dsbridge.QRCodeResultCallback;
import wendu.dsbridge.R;
import wendu.dsbridge.base.BaseFm;
import wendu.dsbridge.dwebview.DWebView;
import wendu.dsbridge.helper.DefaultDsbridge;
import wendu.dsbridge.helper.FileDsbridge;
import wendu.dsbridge.helper.FragmentViewModel;
import wendu.dsbridge.helper.OpenUrlFileDsbridge;
import wendu.dsbridge.helper.PhotoDsbridge;
import wendu.dsbridge.helper.QRCodeDsbridge;
import wendu.dsbridge.tool.GsonUtil;

/**
 * @author: admin
 * @date: 2023/4/19
 */
public class WebViewFragment extends BaseFm implements QRCodeResultCallback, PhotoResultCallback, FileResultCallback {

    /**
     * webview
     */
    private DWebView myWebview;


    private QRCodeDsbridge mQRCodeDsbridge;
    private PhotoDsbridge mPhotoDsbridge;
    private FileDsbridge mFileDsbridge;
    private OpenUrlFileDsbridge mOpenUrlFileDsbridge;
    private DefaultDsbridge mDefaultDsbridge;

    public QRCodeDsbridge getQRCodeDsbridge() {
        return mQRCodeDsbridge;
    }

    public PhotoDsbridge getPhotoDsbridge() {
        return mPhotoDsbridge;
    }

    public FileDsbridge getFileDsbridge() {
        return mFileDsbridge;
    }

    public OpenUrlFileDsbridge getOpenUrlFileDsbridge() {
        return mOpenUrlFileDsbridge;
    }



    @Override
    protected int initFragmentContentView() {
        return R.layout.layout_webbase;
    }

    @Override
    protected void onCreateView(View viewRoot, Bundle savedInstanceState) {
        myWebview = $(R.id.base_mywebview);

        //初始化 所有交互桥接
        mQRCodeDsbridge = new QRCodeDsbridge(getActivity(), this);
        mPhotoDsbridge = new PhotoDsbridge(getActivity(), this);
        mFileDsbridge = new FileDsbridge(getActivity(), this);
        mOpenUrlFileDsbridge = new OpenUrlFileDsbridge(getActivity());
        mDefaultDsbridge = new DefaultDsbridge(myWebview);

        //将所有的交互桥接添加到监听之中
        addJavascriptObject(mQRCodeDsbridge, "QR");
        addJavascriptObject(mPhotoDsbridge, "Photo");
        addJavascriptObject(mFileDsbridge, "File");
        addJavascriptObject(mOpenUrlFileDsbridge, "OpenUrl");
        addJavascriptObject(mDefaultDsbridge, "Default");
        addJavascriptObject(this, "WebviewFragment");

        //添加返回优先级
        addOnBackPressedListener(BackPriority.Min, myWebview);
        addOnBackPressedListener(BackPriority.$1, mDefaultDsbridge);
        addOnBackPressedListener(BackPriority.$99, mOpenUrlFileDsbridge);
        addOnBackPressedListener(BackPriority.Max, mFileDsbridge);

        new ViewModelProvider(getActivity()).get(FragmentViewModel.class).getFragmentModel().setValue(this);
        getLifecycle().addObserver(myWebview);

    }


    /**
     * 二维码扫码结果
     *
     * @param isSuc 是否成功
     * @param data  返回的数据
     * @param err   错误的信息
     */
    @Override
    public void onQRCodeResult(boolean isSuc, String data, String err) {
        if (isSuc) {
            sendMessageToH5(FROMANDROIDQRCCODE, GsonUtil.mapToJson(new String[]{"data", data}));
        } else {
            showToast(err);
        }
    }

    /**
     * 图片返回结果
     *
     * @param isSuc 是否成功
     * @param code  响应码
     * @param data  返回的数据
     * @param err   错误的信息
     */
    @Override
    public void onPhotoResult(boolean isSuc, int code, String data, String err) {
        if (isSuc) {
            onChossPhoto(code, data);
        } else {
            showToast(err);
        }
    }

    @Override
    public void onFileResult(boolean isSuc, int code, String data, String err) {
        if (isSuc) {
            onChossFile(code, data);
        } else {
            showToast(err);
        }
    }


    /**
     * 文件选择
     *
     * @param fileResultCode
     * @param json
     */
    public void onChossFile(int fileResultCode, String json) {
        sendMessageToH5(FROMCHOOSEFILE, json);
    }

    /**
     * 图片选择
     *
     * @param fileResultCode
     * @param json
     */
    public void onChossPhoto(int fileResultCode, String json) {
        if (fileResultCode == PictureConfig.CHOOSE_REQUEST) {
            if (null != json) {
                sendMessageToH5(FROMANDROIDPHOTO, json);
            } else {
                sendMessageToH5(FROMANDROIDPHOTO, "");
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mQRCodeDsbridge.QRCode(requestCode, resultCode, data);
        mFileDsbridge.customFileResult(getActivity(), requestCode, resultCode, data);
        mPhotoDsbridge.customPhotoResult(requestCode, resultCode, data);
    }

    /**
     * 获取 webview
     *
     * @return
     */
    public DWebView getMyWebview() {
        return myWebview;
    }


    /**
     * 添加 webview 的交互回调
     *
     * @param o
     * @param name
     */
    public void addJavascriptObject(Object o, String name) {
        myWebview.addJavascriptObject(o, name);
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
        mDefaultDsbridge.sendMessageToH5(methodNmae, json);
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
        mDefaultDsbridge.fromH5Message(methodNmae, fromH5MessageCallBack);
    }


}
