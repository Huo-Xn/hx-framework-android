package wendu.dsbridge.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import wendu.dsbridge.R;
import wendu.dsbridge.tool.StatusBar;

/**
 * @author: admin
 * @date: 2023/4/13
 */
public class NetworkAnomalyBox {

    private View view;
    /**
     * 网络异常 容器
     */
    private LinearLayout mInternet;

    /**
     * 网络异常文字提示
     */
    private TextView mInternetText;

    public NetworkAnomalyBox(Activity activity) {

        view = LayoutInflater.from(activity).inflate(R.layout.layout_networkanomalybox, null);
        mInternet = view.findViewById(R.id.base_mywebview_internet);
        mInternetText = view.findViewById(R.id.base_mywebview_internet_text);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = StatusBar.getNavBarHeight(activity) + 30;

//        setContentView(view);
//        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
//        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
//        setFocusable(true);
//        setBackgroundDrawable(new ColorDrawable());
//        setOutsideTouchable(true);
        activity.getWindow().addContentView(view, layoutParams);
    }


    public void show() {
        if (view.getVisibility() == View.VISIBLE) {
            return;
        }
        view.setVisibility(View.VISIBLE);
//        showAtLocation(view, Gravity.TOP,0,0);
    }

    public void dismiss() {
        if (view.getVisibility() == View.GONE) {
            return;
        }
        view.setVisibility(View.GONE);
    }

}
