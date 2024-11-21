package wendu.dsbridge.dwebview;

import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;


/**
 * @description:
 * @author: ash
 * @date : 2022/2/25 14:48
 * @email : ash_945@126.com
 */
public abstract class FileChooserListener {

    public abstract void openFileChooser(ValueCallback<Uri> valueCallback, String s, String s1);

    public abstract boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, WebChromeClient.FileChooserParams fileChooserParams);

    /**
     * 图片多选
     */
    public void multipleSelectionPictures() {

    }

    /**
     * 图片单选
     */
    public void selectionPicture() {

    }

    /**
     * 文件多选
     */
    public void multipleSelectionFiles() {

    }


    /**
     * 文件单选
     */
    public void selectionFile() {

    }

}
