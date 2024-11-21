package wendu.dsbridge;

/**
 * @author: admin
 * @date: 2023/4/14
 */
public interface AndroidToH5Action {

    /**
     * 来自Android 的返回事件
     */
    String FROMANDROIDONBACKPRESSED = "fromAndroidOnBackPressed";
    /**
     * 来自Android 的返回的图片
     */
    String FROMANDROIDPHOTO = "fromAndroidPhoto";

    /**
     * 来自Android 的返回的二维码信息
     */
    String FROMANDROIDQRCCODE = "fromAndroidQrcCode";

    /**
     * 来自Android 返回的文件信息
     */
    String FROMCHOOSEFILE = "fromChooseFile";
}
