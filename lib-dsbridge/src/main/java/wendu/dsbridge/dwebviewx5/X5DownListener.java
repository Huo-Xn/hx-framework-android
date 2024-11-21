package wendu.dsbridge.dwebviewx5;

/**
 * @description:
 * @author: ash
 * @date : 2022/2/23 16:37
 * @email : ash_945@126.com
 */
public interface X5DownListener {

    /**
     * 下载成功
     * @param code
     * 110 无需下载
     * 100 下载成功
     * @param msg
     */
    void onDownloadSuccess(int code,String msg);

    /**
     * 下载失败
     * @param code
     * @param msg
     */
    void onDownloadFail(int code,String msg);

    /**
     * 安装成功
     * @param code
     * 200 安装成功
     * 232 安装成功并删除安装包
     * @param msg
     */
    void onInstallSuccess(int code,String msg);

    /**
     * 安装失败
     * @param code
     * @param msg
     */
    void onInstallFail(int code,String msg);

    /**
     * 下载进度
     * @param progress
     */
    void onDownloadProgress(int progress);

}
