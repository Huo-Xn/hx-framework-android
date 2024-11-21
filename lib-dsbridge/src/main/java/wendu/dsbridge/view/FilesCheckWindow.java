package wendu.dsbridge.view;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnDismissListener;
import com.bigkoo.alertview.OnItemClickListener;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import wendu.dsbridge.R;
import wendu.dsbridge.base.BaseRecyclerViewAdapter;
import wendu.dsbridge.tool.GetFilePathFromUri;

/**
 * @author: admin
 * @date: 2022/12/6
 */
public class FilesCheckWindow {

    private AlertView alertView;
    private BaseRecyclerViewAdapter<String[]> adapter;
    private ItemClick itemClick;

    public FilesCheckWindow(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.popupwindow_selectfiles, null);
        setAdapter();
        RecyclerView mFilesRcy = view.findViewById(R.id.filesrcy);
        mFilesRcy.setLayoutManager(new LinearLayoutManager(context));
        mFilesRcy.setAdapter(adapter);
        alertView = new AlertView.Builder()
                .setContext(context)
                .setStyle(AlertView.Style.ActionSheet)
                .setTitle("一次只能上传一个文件，请重新选择")
                .setMessage(null)
                .setCancelText("取消")
                .setOthers(null)
                .setDestructive()
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {
                    }
                })
                .build()
                .addExtView(view);
    }


    private void setAdapter() {
        adapter = new BaseRecyclerViewAdapter<String[]>(R.layout.item_popupwindow_selectfiles) {
            @Override
            public void onBindChildViewHolder(View itemView, int position, ArrayList<String[]> mList) {
                String[] strings = mList.get(position);
                ImageView img = itemView.findViewById(R.id.item_popupwindow_selectfiles_image);
                TextView txt = itemView.findViewById(R.id.item_popupwindow_selectfiles_text);
                txt.setText(strings[1]);

                if (TextUtils.equals(strings[2], "jpg") || TextUtils.equals(strings[2], "png") || TextUtils.equals(strings[2], "jpeg")) {
                    Glide.with(itemView.getContext()).load(new File(strings[0])).into(img);
                } else if (TextUtils.equals(strings[2], "pdf")) {
                    Glide.with(itemView.getContext()).load(R.mipmap.pdf).into(img);
                } else if (TextUtils.equals(strings[2], "doc") || TextUtils.equals(strings[2], "docx")) {
                    Glide.with(itemView.getContext()).load(R.mipmap.doc).into(img);
                } else if (TextUtils.equals(strings[2], "xlsx")) {
                    Glide.with(itemView.getContext()).load(R.mipmap.xls).into(img);
                } else {
                    Glide.with(itemView.getContext()).load(R.mipmap.othe).into(img);
                }
            }
        };

        adapter.setItemClick(new BaseRecyclerViewAdapter.ItemClick<String[]>() {
            @Override
            public void setOnItemClick(View v, int position, ArrayList<String[]> mList) {
                String[] strings = mList.get(position);
                if (null != itemClick) {
                    itemClick.click(strings);
                }
                dismiss();
            }
        });
    }


    public void setData(Context context, Intent data, ItemClick itemClick) {
        this.itemClick = itemClick;
        ClipData clipData = data.getClipData();
        int itemCount = clipData.getItemCount();
        ArrayList<String[]> items = new ArrayList<>();
        for (int i = 0; i < itemCount; i++) {
            ClipData.Item itemAt = clipData.getItemAt(i);
            Uri uri = itemAt.getUri();
            if (null != uri) {
                String fileAbsolutePath = GetFilePathFromUri.getFileAbsolutePath(context, uri);
                File file = new File(fileAbsolutePath);
                if (null != file) {
                    String[] split = fileAbsolutePath.split("\\.");
                    String fileType = "";
                    if (null != split && split.length > 1) {
                        fileType = split[split.length - 1];
                    }
                    items.add(new String[]{fileAbsolutePath, file.getName(), fileType});
                }
            }
        }
        adapter.setList(items);


    }


    public void show() {
        alertView.show();
    }


    public void dismiss() {
        alertView.dismiss();
    }

    public boolean isShowing() {
        return alertView.isShowing();
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        alertView.setOnDismissListener(onDismissListener);
    }


    public interface ItemClick {
        void click(String[] data);
    }

}
