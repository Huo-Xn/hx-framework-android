package wendu.dsbridge.tool;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author: admin
 * @date: 2022/8/19
 */
public class OpenFiles {

    //android获取一个用于打开HTML文件的intent
    public static Intent getHtmlFileIntent(String Path) {
        File file = new File(Path);
        Uri uri = Uri.parse(file.toString()).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(file.toString()).build();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, getMimeTypeFromFile(file));//"text/html"
        return intent;
    }

    //android获取一个用于打开图片文件的intent
    public static Intent getImageFileIntent(String Path) {
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, getMimeTypeFromFile(file));//"image/*"
        return intent;
    }

    //android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent(Context context, String Path) {
        File file = new File(Path);
        Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");

        return intent;
    }

    //android获取一个用于打开文本文件的intent
    public static Intent getTextFileIntent(String Path) {
        File file = new File(Path);

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, getMimeTypeFromFile(file));//"text/plain"
        return intent;
    }

    //android获取一个用于打开音频文件的intent
    public static Intent getAudioFileIntent(String Path) {
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, getMimeTypeFromFile(file));//"audio/*"
        return intent;
    }

    //android获取一个用于打开视频文件的intent
    public static Intent getVideoFileIntent(String Path) {
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, getMimeTypeFromFile(file));//"video/*"
        return intent;
    }


    //android获取一个用于打开CHM文件的intent
    public static Intent getChmFileIntent(String Path) {
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, getMimeTypeFromFile(file));//"application/x-chm"
        return intent;
    }


    //android获取一个用于打开Word文件的intent
    public static Intent getWordFileIntent(String Path) {
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, getMimeTypeFromFile(file));//"application/msword"
        return intent;
    }

    //android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent(String Path) {
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, getMimeTypeFromFile(file));
        return intent;
    }

    //android获取一个用于打开PPT文件的intent
    public static Intent getPPTFileIntent(String Path) {
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, getMimeTypeFromFile(file));//"application/vnd.ms-powerpoint"
        return intent;
    }

    //android获取一个用于打开apk文件的intent
    public static Intent getApkFileIntent(String Path) {
        File file = new File(Path);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), getMimeTypeFromFile(file));//"application/vnd.android.package-archive"
        return intent;
    }

    //得到file的类型
    public static String getMimeTypeFromFile(File file) {
        String type = "*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex > 0) {
            //获取文件的后缀名
            String end = fName.substring(dotIndex).toLowerCase(Locale.getDefault());
            //在MIME和文件类型的匹配表中找到对应的MIME类型。
            Map<String, String> map = getMimeMap();
            if (!TextUtils.isEmpty(end) && map.keySet().contains(end)) {
                type = map.get(end);
            }
        }
        return type;
    }


    private static Map<String, String> getMimeMap() {
        Map<String, String> mapSimple = new HashMap<>();
        if (mapSimple.size() == 0) {
            mapSimple.put(".3gp", "video/3gpp");
            mapSimple.put(".apk", "application/vnd.android.package-archive");
            mapSimple.put(".asf", "video/x-ms-asf");
            mapSimple.put(".avi", "video/x-msvideo");
            mapSimple.put(".bin", "application/octet-stream");
            mapSimple.put(".bmp", "image/bmp");
            mapSimple.put(".c", "text/plain");
            mapSimple.put(".chm", "application/x-chm");
            mapSimple.put(".class", "application/octet-stream");
            mapSimple.put(".conf", "text/plain");
            mapSimple.put(".cpp", "text/plain");
            mapSimple.put(".doc", "application/msword");
            mapSimple.put(".docx", "application/msword");
            mapSimple.put(".exe", "application/octet-stream");
            mapSimple.put(".gif", "image/gif");
            mapSimple.put(".gtar", "application/x-gtar");
            mapSimple.put(".gz", "application/x-gzip");
            mapSimple.put(".h", "text/plain");
            mapSimple.put(".htm", "text/html");
            mapSimple.put(".html", "text/html");
            mapSimple.put(".jar", "application/java-archive");
            mapSimple.put(".java", "text/plain");
            mapSimple.put(".jpeg", "image/jpeg");
            mapSimple.put(".jpg", "image/jpeg");
            mapSimple.put(".js", "application/x-javascript");
            mapSimple.put(".log", "text/plain");
            mapSimple.put(".m3u", "audio/x-mpegurl");
            mapSimple.put(".m4a", "audio/mp4a-latm");
            mapSimple.put(".m4b", "audio/mp4a-latm");
            mapSimple.put(".m4p", "audio/mp4a-latm");
            mapSimple.put(".m4u", "video/vnd.mpegurl");
            mapSimple.put(".m4v", "video/x-m4v");
            mapSimple.put(".mov", "video/quicktime");
            mapSimple.put(".mp2", "audio/x-mpeg");
            mapSimple.put(".mp3", "audio/x-mpeg");
            mapSimple.put(".mp4", "video/mp4");
            mapSimple.put(".mpc", "application/vnd.mpohun.certificate");
            mapSimple.put(".mpe", "video/mpeg");
            mapSimple.put(".mpeg", "video/mpeg");
            mapSimple.put(".mpg", "video/mpeg");
            mapSimple.put(".mpg4", "video/mp4");
            mapSimple.put(".mpga", "audio/mpeg");
            mapSimple.put(".msg", "application/vnd.ms-outlook");
            mapSimple.put(".ogg", "audio/ogg");
            mapSimple.put(".pdf", "application/pdf");
            mapSimple.put(".png", "image/png");
            mapSimple.put(".pps", "application/vnd.ms-powerpoint");
            mapSimple.put(".ppt", "application/vnd.ms-powerpoint");
            mapSimple.put(".pptx", "application/vnd.ms-powerpoint");
            mapSimple.put(".prop", "text/plain");
            mapSimple.put(".rar", "application/x-rar-compressed");
            mapSimple.put(".rc", "text/plain");
            mapSimple.put(".rmvb", "audio/x-pn-realaudio");
            mapSimple.put(".rtf", "application/rtf");
            mapSimple.put(".sh", "text/plain");
            mapSimple.put(".tar", "application/x-tar");
            mapSimple.put(".tgz", "application/x-compressed");
            mapSimple.put(".txt", "text/plain");
            mapSimple.put(".wav", "audio/x-wav");
            mapSimple.put(".wma", "audio/x-ms-wma");
            mapSimple.put(".wmv", "audio/x-ms-wmv");
            mapSimple.put(".wps", "application/vnd.ms-works");
            mapSimple.put(".xml", "text/plain");
            mapSimple.put(".xls", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            mapSimple.put(".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            mapSimple.put(".z", "application/x-compress");
            mapSimple.put(".zip", "application/zip");
            mapSimple.put("", "*/*");
        }
        return mapSimple;
    }


    /**
     * 通过url 获取文件的name 类型
     *
     * @param url
     */
    public static String urlToFileName(String url) {
        String fileName = "";
        //如果字符串不为空
        if (!TextUtils.isEmpty(url)) {
            String[] urlSplit = url.split("\\/");
            if (null != urlSplit && urlSplit.length > 1) {
                fileName = urlSplit[urlSplit.length - 1];
            }
        }
        return fileName;
    }

    /**
     * 通过url 获取文件的type 类型
     *
     * @param url
     */
    public static String urlToFileType(String url) {
        String type = "";
        if (!TextUtils.isEmpty(url)) {
            String[] urlSplit = url.split("\\.");
            if (null != urlSplit && urlSplit.length > 1) {
                type = urlSplit[urlSplit.length - 1];
            }
        }
        return type;
    }


    /**
     * 获取打开文档意图
     *
     * @param file
     * @return
     */
    public static Intent getFileIntent(Context context, File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        try {
            Uri uri = Uri.fromFile(file);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //通过FileProvider创建一个content类型的Uri
                uri = FileProvider.getUriForFile(context, context.getPackageName() + ".FileProvider", file);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            //优先使用WPS打开
            //检测是否安装了wps软件，没有安装选择默认打开, 动态的grant URI权限
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            String mimeType = getMIMEType(file);
            intent.setDataAndType(uri, mimeType);
            if (isAvilible(context, "cn.wps.moffice_eng")) {
                intent.setClassName("cn.wps.moffice_eng",
                        "cn.wps.moffice.documentmanager.PreStartActivity2");
            } else {
                intent.addCategory("android.intent.category.DEFAULT");
                Toast.makeText(context, "没有可以打开该文件的工具，将自动寻找工具", Toast.LENGTH_SHORT).show();
            }
            return intent;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Log.e("查看", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("查看", e.getMessage());
        }
        return intent;
    }


    public static boolean isAvilible(Context context, String packageName) {
        // 获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        // 用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        // 从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        // 判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }

    /**
     * 根据 file 获取文件类型
     *
     * @param f
     * @return
     */
    public static String getMIMEType(File f) {
        String type = "";
        String fName = f.getName();
        /* 取得扩展名 */
        String end = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();
        /* 依扩展名的类型决定MimeType */
        if (end.equals("pdf")) {
            type = "application/pdf";//
        } else if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") ||
                end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
            type = "audio/*";
        } else if (end.equals("3gp") || end.equals("mp4")) {
            type = "video/*";
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png") ||
                end.equals("jpeg") || end.equals("bmp")) {
            type = "image/*";
        } else if (end.equals("apk")) {
            type = "application/vnd.android.package-archive";
        } else if (end.equals("pptx") || end.equals("ppt")) {
            type = "application/vnd.ms-powerpoint";
        } else if (end.equals("docx") || end.equals("doc")) {
            type = "application/vnd.ms-word";
        } else if (end.equals("xlsx") || end.equals("xls")) {
            type = "application/vnd.ms-excel";
        } else {
            type = "*/*";
        }
        return type;
    }

}
