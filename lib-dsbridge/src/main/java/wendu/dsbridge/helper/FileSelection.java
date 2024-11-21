package wendu.dsbridge.helper;

import android.net.Uri;

/**
 * @description:
 * @author: ash
 * @date :
 * @email : ash_945@126.com
 */
public class FileSelection {

    private String filePath;

    private String name;

    private String base64;

    private String fileType;

    public FileSelection(String filePath, String name) {
        this.filePath = filePath;
        this.name = name;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
