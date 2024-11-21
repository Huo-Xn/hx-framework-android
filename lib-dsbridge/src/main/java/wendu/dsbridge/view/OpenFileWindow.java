package wendu.dsbridge.view;

import static wendu.dsbridge.tool.OpenFiles.getFileIntent;
import static wendu.dsbridge.tool.OpenFiles.urlToFileName;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.heiko.stripeprogressbar.StripeProgressBar;

import java.io.File;

import wendu.dsbridge.DownloadListener;
import wendu.dsbridge.R;
import wendu.dsbridge.net.DownloadUtil;

/**
 * @author: admin
 * @date: 2023/4/20
 */
public class OpenFileWindow extends PopupWindow implements DownloadListener {

    private Context context;
    private Handler handler;
    private ImageView img;
    private StripeProgressBar progressBar;
    private TextView text;
    private View inflate;

    public OpenFileWindow(Context context) {
        super(context);
        this.context = context;
        handler = new Handler(Looper.getMainLooper());
        inflate = LayoutInflater.from(context).inflate(R.layout.layout_mask_defaultopenfile, null);
        setContentView(inflate);
        img = inflate.findViewById(R.id.openfile_mask_img);
        progressBar = inflate.findViewById(R.id.openfile_mask_progressBar);
        text = inflate.findViewById(R.id.openfile_mask_text);
        View out = inflate.findViewById(R.id.openfile_mask_out);

        Glide.with(context).load(R.mipmap.icon_fanshu).into(img);

        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable());
        setOutsideTouchable(true);

        out.setOnClickListener(v -> {
            dismiss();
        });

    }

    /**
     * 根据url  拿到当前的文件类型
     *
     * @param url
     */
    public void openFile(String url) {
        show();
        String name = urlToFileName(url);
        DownloadUtil.get().download(url, Environment.getExternalStorageDirectory().getPath() + "/sx_download", name, this);
    }


    @Override
    public void onDownloadSuccess(File file) {
        handler.post(() -> {
            if (isShowing()) {
                Intent fileIntent = getFileIntent(context, file);
                context.startActivity(fileIntent);
                dismiss();
            }
        });
    }

    public void show() {
        showAtLocation(inflate, Gravity.BOTTOM,0,0);
    }

    @Override
    public void onDownloading(int progress) {
        handler.post(() -> {
            if (isShowing()) {
                progressBar.setProgress(progress);
            }
        });
    }

    @Override
    public void onDownloadFailed(Exception e) {
        handler.post(() -> {
            Toast.makeText(context, "下载时出错", Toast.LENGTH_SHORT).show();
            dismiss();
        });
    }


}
