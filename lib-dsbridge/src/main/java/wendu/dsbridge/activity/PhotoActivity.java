package wendu.dsbridge.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bigkoo.alertview.AlertView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.luck.picture.lib.photoview.PhotoView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import wendu.dsbridge.PermissionsCallback;
import wendu.dsbridge.R;
import wendu.dsbridge.tool.Base64Util;
import wendu.dsbridge.tool.PermissionsManager;

/**
 * @author: admin
 * @date: 2023/4/18
 */
public class PhotoActivity extends AppCompatActivity implements PermissionsCallback {

    private FrameLayout mDocumentatioinroot;


    private Handler handler;
    private String url = "";
    private PhotoView photoView;
    private ImageView photoBack;
    private AlertView alertView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        handler = new Handler(Looper.getMainLooper());

        photoView = findViewById(R.id.photoview);
        photoBack = findViewById(R.id.photoback);

        url = getIntent().getStringExtra("url");
        if (url.startsWith("data:image")) {
            Bitmap bitmap = Base64Util.base64ToBitmap(url);
            Glide.with(PhotoActivity.this)
                    .load(bitmap)
//                    .placeholder(R.mipmap.icon_placeholder)
                    .listener(new RequestListener() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                            Log.e("Glide", "Load failed", e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }

                    })
                    .into(photoView);
        }else {
            //展示图片
            Glide.with(PhotoActivity.this)
                    .load(url)
                    .listener(new RequestListener() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                            Log.e("Glide", "Load failed", e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }

                    })
                    .into(photoView);
        }

        createAlert();


        photoBack.setOnClickListener(v -> {
            finish();
        });

        photoView.setOnLongClickListener(v -> {
            alertView.show();
            return false;
        });


    }

    private void createAlert() {
        alertView = new AlertView.Builder()
                .setContext(this)
                .setStyle(AlertView.Style.ActionSheet)
                .setTitle("选择操作")
                .setDestructive("保存到相册")
                .setMessage(null)
                .setCancelText("取消")
                .setOthers(null)
                .setOnItemClickListener((o, position) -> {
                    AlertView oa = (AlertView) o;
                    switch (position) {
                        case 0:
                            new PermissionsManager(this)
                                    .readAndWrite(this);
                            break;
                    }
                    oa.dismiss();
                })
                .build();
    }

    @Override
    public void granted() {
        try {
            Bitmap bm = ((BitmapDrawable) ((ImageView) photoView).getDrawable()).getBitmap();
            saveBitmap(PhotoActivity.this, bm);
            Toast.makeText(PhotoActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(PhotoActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != alertView) {
            alertView.dismiss();
            alertView = null;
        }

    }

    /*
     * 保存文件，文件名为当前日期
     */
    public static void saveBitmap(Context context, Bitmap bitmap) throws Exception {
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String bitName = format.format(new Date()) + ".JPEG";
        String fileName;
        File file;
        if (Build.BRAND.equals("Xiaomi")) { // 小米手机
            fileName = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/" + bitName;
        } else { // Meizu 、Oppo
            fileName = Environment.getExternalStorageDirectory().getPath() + "/DCIM/" + bitName;
        }
        file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out;
        out = new FileOutputStream(file);
        // 格式为 JPEG，照相机拍出的图片为JPEG格式的，PNG格式的不能显示在相册中
        if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
            out.flush();
            out.close();
            // 插入图库
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), bitName, null);
            file.delete();
        }

        // 发送广播，通知刷新图库的显示
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));
    }


}
