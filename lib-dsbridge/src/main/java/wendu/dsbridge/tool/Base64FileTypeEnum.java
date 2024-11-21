package wendu.dsbridge.tool;

/**
 * @author: admin
 * @date: 2022/8/11
 */

/**
 * base64文件类型,前缀
 *
 * @author
 */
public enum Base64FileTypeEnum {

    // 文件类型
    BASE64_FILETYPE_DOC(".doc", "data:application/msword;base64"),
    BASE64_FILETYPE_DOCX(".docx", "data:application/vnd.openxmlformats-officedocument.wordprocessingml.document;base64"),
    BASE64_FILETYPE_XLS(".xls", "data:application/vnd.ms-excel;base64"),
    BASE64_FILETYPE_XLSX(".xlsx", "data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64"),
    BASE64_FILETYPE_PDF(".pdf", "data:application/pdf;base64"),
    BASE64_FILETYPE_PPT(".ppt", "data:application/vnd.ms-powerpoint;base64"),
    BASE64_FILETYPE_PPTX(".pptx", "data:application/vnd.openxmlformats-officedocument.presentationml.presentation;base64"),
    BASE64_FILETYPE_TXT(".txt", "data:text/plain;base64"),

    // 图片类型
    BASE64_FILETYPE_PNG(".png", "data:image/png;base64"),
    BASE64_FILETYPE_JPG(".jpg", "data:image/jpeg;base64"),
    BASE64_FILETYPE_JPEG(".jpeg", "data:image/jpeg;base64"),
    BASE64_FILETYPE_GIF(".gif", "data:image/gif;base64"),
    BASE64_FILETYPE_SVG(".svg", "data:image/svg+xml;base64"),
    BASE64_FILETYPE_ICO(".ico", "data:image/x-icon;base64"),
    BASE64_FILETYPE_BMP(".bmp", "data:image/bmp;base64"),

    //音视频
    BASE64_FILETYPE_WMV(".wmv", "data:video/x-ms-wmv;base64"),
    BASE64_FILETYPE_MP4(".mp4", "data:video/mpeg4;base64"),
    BASE64_FILETYPE_AVI(".avi", "data:video/avi;base64"),
    BASE64_FILETYPE_WMA(".wma", "data:video/wma;base64"),
    BASE64_FILETYPE_MP3(".mp3", "data:audio/mp3;base64"),
    BASE64_FILETYPE_3GP(".3gp", "data:video/3gpp;base64"),
    BASE64_FILETYPE_FLV(".flv", "data:video/x-flv;base64"),
    BASE64_FILETYPE_MKV(".mkv", "data:video/x-matroska;base64"),

//    // 二进制流
//    BASE64_FILETYPE_OCTET_STREAM("octet-stream", "data:application/octet-stream;base64,"),
    ;

    private Base64FileTypeEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }

    private String code;
    private String value;

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public static String getFileType(String value) {
        Base64FileTypeEnum[] types = values();
        for (Base64FileTypeEnum x : types) {
            if (x.getCode().equals("." + value)) {
                return x.getValue();
            }
        }
        return null;
    }
}
