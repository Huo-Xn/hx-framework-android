package wendu.dsbridge;

import java.io.File;

/**
 * @author: admin
 * @date: 2023/4/18
 */
public interface DownloadListener {

    /**
     * 下载成功之后的文件
     */
    void onDownloadSuccess(File file);

    /**
     * 下载进度
     */
    void onDownloading(int progress);

    /**
     * 下载异常信息
     */

    void onDownloadFailed(Exception e);
}
