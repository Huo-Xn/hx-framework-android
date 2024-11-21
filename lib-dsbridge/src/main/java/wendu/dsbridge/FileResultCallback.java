package wendu.dsbridge;

/**
 * 文件处理结果
 *
 * @author: admin
 * @date: 2023/4/19
 */
public interface FileResultCallback {
    /**
     *  文件处理结果
     * @param isSuc 是否成功
     * @param code 响应码
     * @param data 返回的数据
     * @param err 错误的信息
     */
    void onFileResult(boolean isSuc, int code ,String data ,String err);
}
