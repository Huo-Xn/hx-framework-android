package wendu.dsbridge.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.luck.picture.lib.photoview.PhotoView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import wendu.dsbridge.PermissionsCallback;
import wendu.dsbridge.R;
import wendu.dsbridge.tool.PermissionsManager;

/**
 * @author: admin
 * @date: 2022/8/11
 */
public class DocumentationActivity extends AppCompatActivity {
    //    private AgentWeb.PreAgentWeb ready;

    private FrameLayout mDocumentatioinroot;


    private ImageView pdfLoad;
    private TextView pdfTxt;
    private Handler handler;

    private String url = "";
    private PhotoView photoView;
    private ImageView photoBack;
    private AlertView alertView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documentatioin);
        handler = new Handler(Looper.getMainLooper());


        mDocumentatioinroot = findViewById(R.id.documentatioin);
        pdfLoad = findViewById(R.id.pdfloading);
        pdfTxt = findViewById(R.id.pdfloadingtxt);
        photoView = findViewById(R.id.photoview);
        photoBack = findViewById(R.id.photoback);

        photoBack.setOnClickListener(v -> {
            finish();
        });


        Glide.with(this)
                .load(R.mipmap.icon_fanshu)
                .into(pdfLoad);

        alertView = new AlertView.Builder()
                .setContext(DocumentationActivity.this)
                .setStyle(AlertView.Style.ActionSheet)
                .setTitle("选择操作")
                .setDestructive("保存到相册")
                .setMessage(null)
                .setCancelText("取消")
                .setOthers(null)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {
                        AlertView oa = (AlertView) o;
                        switch (position) {
                            case 0:
                                try {
                                    Bitmap bm = ((BitmapDrawable) ((ImageView) photoView).getDrawable()).getBitmap();
                                    saveBitmap(DocumentationActivity.this, bm);
                                    Toast.makeText(DocumentationActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(DocumentationActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                                }
                                break;
                        }
                        oa.dismiss();
                    }
                })
                .build();


        new PermissionsManager(this).readAndWrite(new PermissionsCallback() {

            @Override
            public void granted() {
                Intent intent = getIntent();
                url = intent.getStringExtra("pdfUrl");

                String[] split = url.split("\\.");
                if (null != split && split.length > 0) {
                    String fileType = split[split.length - 1];
                    if (TextUtils.equals(fileType, "jpg") || TextUtils.equals(fileType, "png")) {
                        Glide.with(DocumentationActivity.this)
                                .load(url)
                                .into(photoView);
                        photoBack.setVisibility(View.VISIBLE);
                        pdfLoad.setVisibility(View.GONE);
                        pdfTxt.setVisibility(View.GONE);
                        photoView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                alertView.show();

                                return false;
                            }
                        });
                        return;
                    }
                }

            }
        });


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
        alertView.dismiss();
        if (null != alertView) {
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



//                NestedScrollAgentWebView webView = new NestedScrollAgentWebView(DocumentationActivity.this);
//                ready = AgentWeb
//                        .with(DocumentationActivity.this)
//                        .setAgentWebParent(mDocumentatioinroot, new FrameLayout.LayoutParams(-1, -1))//传入AgentWeb 的父控件 ，如果父控件为 RelativeLayout ， 那么第二参数需要传入 RelativeLayout.LayoutParams
//                        .useDefaultIndicator()
//                        .setWebView(webView)// 使用默认进度条
//                        .setMainFrameErrorView(null)
//                        .setAgentWebUIController(new AgentWebUIControllerImplBase() {
//                            @Override
//                            public void onMainFrameError(WebView view, int errorCode, String description, String failingUrl) {
//                                handler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        pdfTxt.setText("出错啦~请点击重试");
//                                        pdfLoad.setOnClickListener(v -> {
//                                            ready.go(url);
//                                        });
//                                        pdfTxt.setOnClickListener(v -> {
//                                            ready.go(url);
//                                        });
//                                    }
//                                });
//
//                                super.onMainFrameError(view, errorCode, description, failingUrl);
//                            }
//                        })
//                        .createAgentWeb()
//                        .ready();
//                ready.go(url);



//    String finalName = name;
//    File file = new File(path + "/" + name);
//                    if (file!=null) {
//                        intent = OpenFiles.getPdfFileIntent(DocumentationActivity.this, file.getAbsolutePath());
//                        startActivity(intent);
//                    }else{
//                        DownloadUtil.get().download(url, path, name, new DownloadUtil.OnDownloadListener() {
//                            @Override
//                            public void onDownloadSuccess(File file) {
//                                Log.d("查看",""+file.getAbsolutePath());
//                                handler.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        int dotIndex = file.getName().lastIndexOf(".");
//                                        if (dotIndex > 0) {
//                                            //获取文件的后缀名
//                                            String type = file.getName().substring(dotIndex).toLowerCase(Locale.getDefault());
//                                            Intent intent = null;
//                                            if (TextUtils.equals(type, ".jpg")) {
//                                                intent = OpenFiles.getImageFileIntent(file.getAbsolutePath());
//                                                startActivity(intent);
//                                            } else if (TextUtils.equals(type, ".png")) {
//                                                intent = OpenFiles.getImageFileIntent(file.getAbsolutePath());
//                                                startActivity(intent);
//                                            } else if (TextUtils.equals(type, ".doc")) {
//                                                intent = OpenFiles.getWordFileIntent(file.getAbsolutePath());
//                                                startActivity(intent);
//                                            } else if (TextUtils.equals(type, ".docx")) {
//                                                intent = OpenFiles.getWordFileIntent(file.getAbsolutePath());
//                                                startActivity(intent);
//                                            } else if (TextUtils.equals(type, ".xls")) {
//                                                intent = OpenFiles.getExcelFileIntent(file.getAbsolutePath());
//                                                startActivity(intent);
//                                            } else if (TextUtils.equals(type, ".xlsx")) {
//                                                intent = OpenFiles.getExcelFileIntent(file.getAbsolutePath());
//                                                startActivity(intent);
//                                            } else if (TextUtils.equals(type, ".pdf")) {
//                                                try {
//                                                    intent = OpenFiles.getPdfFileIntent(DocumentationActivity.this, file.getAbsolutePath());
//                                                    startActivity(intent);
//                                                }catch (Exception e){
//                                                    e.printStackTrace();
//                                                }
//
//                                            }
//
//                                        }
//                                    }
//                                },2000);
//
//                            }
//
//                            @Override
//                            public void onDownloading(int progress) {
//                                handler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        pdfTxt.setText("文件下载中" + progress + "%");
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void onDownloadFailed(Exception e) {
//                                handler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Log.e("查看",""+e.getMessage());
//                                        pdfTxt.setText(e.getMessage());
//                                    }
//                                });
//                            }
//                        });

}
