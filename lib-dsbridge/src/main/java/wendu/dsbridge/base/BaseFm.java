package wendu.dsbridge.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.HashMap;

import wendu.dsbridge.PriorityBackPressedListener;
import wendu.dsbridge.helper.BackPriority;
import wendu.dsbridge.tool.PermissionsManager;

/**
 * @description:
 * @author: ash
 * @date : 2022/3/17 13:45
 * @email : ash_945@126.com
 */
public abstract class BaseFm extends Fragment {


    public static Toast mToast;
    /**
     * 根容器
     */
    private View viewRoot;

    /**
     * 权限管理
     */
    protected PermissionsManager permissionsManager;

    /**
     * 通信必要组件
     */
    protected Handler handler;

    /**
     * 返回键监听集合
     */
    private HashMap<Integer, PriorityBackPressedListener> integerBackPressedListenerHashMap = new HashMap<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        handler = new Handler(Looper.getMainLooper());
        mToast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
        viewRoot = inflater.inflate(initFragmentContentView(), null);
        viewRoot.setOnClickListener(v -> {
        });
        permissionsManager = new PermissionsManager(getActivity());
        getLifecycle().addObserver(permissionsManager);
        return viewRoot;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        integerBackPressedListenerHashMap = new HashMap<>();
        onCreateView(view, savedInstanceState);
    }


    /**
     * 返回键
     *
     * @return
     */
    public boolean onBackPressed() {
        for (int i = BackPriority.Max.getPriority(); i >= BackPriority.Min.getPriority(); i--) {
            PriorityBackPressedListener backPressedListener = integerBackPressedListenerHashMap.get(i);
            if (null != backPressedListener) {
                try {
                    boolean b = backPressedListener.onPriorityBackPressed();
                    if (b) {
                        return true;
                    }
                } catch (Exception e) {
                    removeOnBackPressedListener(i);
                    e.printStackTrace();
                    continue;
                }
            } else {
                continue;
            }
        }
        return false;
    }


    /**
     * 自定义一个 查找id 的方法
     *
     * @param id
     * @param <T>
     * @return
     */
    public <T extends View> T $(int id) {
        return viewRoot.findViewById(id);
    }


    /**
     * 设置 fragment 的核心容器
     *
     * @return
     */
    @LayoutRes
    protected abstract int initFragmentContentView();


    protected abstract void onCreateView(View viewRoot, Bundle savedInstanceState);

    /**
     * 返回事件的处理优先级， 1-100之间的数字，数字越大，越优先处理
     *
     * @param listener
     */
    public void addOnBackPressedListener(BackPriority backPriority, PriorityBackPressedListener listener) {
        integerBackPressedListenerHashMap.put(backPriority.getPriority(), listener);
    }

    /**
     * 删除该优先级的处理回调
     */
    public void removeOnBackPressedListener(BackPriority backPriority) {
        PriorityBackPressedListener backPressedListener = integerBackPressedListenerHashMap.get(backPriority.getPriority());
        if (null != backPressedListener) {
            removeOnBackPressedListener(backPriority.getPriority());
        }
    }

    /**
     * 删除该key的处理回调
     *
     * @param key
     */
    private void removeOnBackPressedListener(int key) {
        integerBackPressedListenerHashMap.remove(key);
    }


    /**
     * 显示fragment
     *
     * @param fragment
     */
    public void showFragment(@NonNull Fragment fragment) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .show(fragment)
                .commitAllowingStateLoss();
    }

    /**
     * 隐藏fragment
     *
     * @param fragment
     */
    public void hideFragment(@NonNull Fragment fragment) {
        if (null== getActivity().getSupportFragmentManager().findFragmentById(fragment.getId())) {
            return;
        }
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .hide(fragment)
                .commitAllowingStateLoss();
    }


    /**
     * 显示
     */
    public void show() {
        handler.post(()->{
            if (!isVisible()) {
                showFragment(this);
            }
        });
    }

    /**
     * 隐藏
     */
    public void hide() {
        handler.post(()->{
            if (isVisible()) {
                hideFragment(this);
            }
        });

    }


    public void showToast(CharSequence text) {
        handler.post(()->{
            if (mToast != null) {
                mToast.cancel();
                mToast = null;
            }
            mToast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
            mToast.show();
        });

    }


}
