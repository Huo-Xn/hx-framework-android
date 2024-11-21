package wendu.dsbridge;

import static wendu.dsbridge.AndroidToH5Action.FROMANDROIDPHOTO;
import static wendu.dsbridge.AndroidToH5Action.FROMANDROIDQRCCODE;
import static wendu.dsbridge.AndroidToH5Action.FROMCHOOSEFILE;

import wendu.dsbridge.tool.GsonUtil;

/**
 * 关于 文件处理提供的默认回调
 *
 * @author: admin
 * @date: 2023/4/14
 */
public interface ResultControlCallback {

    /**
     * 二维码结果处理
     *
     * @param data
     */
    void onQRCodeResult(String data);

    /**
     * 默认的文件处理结果
     *
     * @param json
     */
    void onFileResult(String json);

    /**
     * 默认的图片处理结果
     *
     * @param data
     */
    void onPhotoResult(String data);

    /**
     * 自定义文件处理结果
     *
     * @param resultCode
     * @param json
     */
    void onChossFile(int resultCode, String json);

    /**
     * 自定义图片处理结果
     *
     * @param resultCode
     * @param json
     */
    void onChossPhoto(int resultCode, String json);
}
