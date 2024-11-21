package wendu.dsbridge.tool;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;

/**
 * @author: admin
 * @date: 2022/8/12
 */
public class OpenDocFiles {

    //android获取一个用于打开HTML文件的intent
    public static Intent getHtmlFileIntent(Context context, String Path) {
        File file = new File(Path);
        Uri uri = Uri.parse(file.toString()).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(file.toString()).build();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        return intent;
    }

    //android获取一个用于打开图片文件的intent
    public static Intent getImageFileIntent(Context context, String Path) {
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Uri uri = Uri.fromFile(file);
        Uri uri = FileProvider
                .getUriForFile(context, context.getPackageName(), file);
        intent.setDataAndType(uri, "image/*");
        return intent;
    }

    //android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent(Context context, String Path) {
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Uri uri = Uri.fromFile(file);
        Uri uri = FileProvider
                .getUriForFile(context, context.getPackageName(), file);
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }

    //android获取一个用于打开文本文件的intent
    public static Intent getTextFileIntent(Context context, String Path) {
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Uri uri = Uri.fromFile(file);
        Uri uri = FileProvider
                .getUriForFile(context, context.getPackageName(), file);
        intent.setDataAndType(uri, "text/plain");
        return intent;
    }

    //android获取一个用于打开音频文件的intent
    public static Intent getAudioFileIntent(Context context, String Path) {
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
//        Uri uri = Uri.fromFile(file);
        Uri uri = FileProvider
                .getUriForFile(context, context.getPackageName(), file);
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }

    //android获取一个用于打开视频文件的intent
    public static Intent getVideoFileIntent(Context context, String Path) {
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
//        Uri uri = Uri.fromFile(file);
        Uri uri = FileProvider
                .getUriForFile(context, context.getPackageName(), file);
        intent.setDataAndType(uri, "video/*");
        return intent;
    }


    //android获取一个用于打开CHM文件的intent
    public static Intent getChmFileIntent(Context context, String Path) {
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Uri uri = Uri.fromFile(file);
        Uri uri = FileProvider
                .getUriForFile(context, context.getPackageName(), file);
        intent.setDataAndType(uri, "application/x-chm");
        return intent;
    }


    //android获取一个用于打开Word文件的intent
    public static Intent getWordFileIntent(Context context, String Path) {
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Uri uri = Uri.fromFile(file);
        Uri uri = FileProvider
                .getUriForFile(context, context.getPackageName(), file);
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    //android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent(Context context, String Path) {
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Uri uri = Uri.fromFile(file);
        Uri uri = FileProvider
                .getUriForFile(context, context.getPackageName(), file);
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

    //android获取一个用于打开PPT文件的intent
    public static Intent getPPTFileIntent(Context context, String Path) {
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Uri uri = Uri.fromFile(file);
        Uri uri = FileProvider
                .getUriForFile(context, context.getPackageName(), file);
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }

    //android获取一个用于打开apk文件的intent
    public static Intent getApkFileIntent(Context context, String Path) {
        File file = new File(Path);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        return intent;
    }


}
