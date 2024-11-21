package wendu.dsbridge;

/**
 * 图片处理结果
 *
 * @author: admin
 * @date: 2023/4/19
 */
public interface PhotoResultCallback {
    /**
     *  图片处理结果
     * @param isSuc 是否成功
     * @param code 响应码
     * @param data 返回的数据
     * @param err 错误的信息
     */
    void onPhotoResult(boolean isSuc, int code ,String data ,String err);
}
