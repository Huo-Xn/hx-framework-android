package wendu.dsbridge;

/**
 * @author: admin
 * @date: 2023/4/19
 */
public interface QRCodeResultCallback {
    /**
     *  二维码结果处理
     * @param isSuc 是否成功
     * @param data 返回的数据
     * @param err 错误的信息
     */
    void onQRCodeResult(boolean isSuc, String data ,String err);
}
