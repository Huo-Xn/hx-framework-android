package wendu.dsbridge.helper;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import wendu.dsbridge.fragment.BaseLoadMaskFragment;
import wendu.dsbridge.fragment.BaseUpdateMaskFragment;
import wendu.dsbridge.fragment.DefaultErrorMaskFragment;
import wendu.dsbridge.fragment.DefaultUpdateFragment;
import wendu.dsbridge.fragment.DefaultUsualMaskFragment;

/**
 * @author: admin
 * @date: 2023/4/17
 */
public class MaskManager {

    private  Handler handler;
    private FragmentManager supportFragmentManager;

    /**
     * 通常的遮罩层
     */
    private BaseLoadMaskFragment mUsualMaskFragment;

    /**
     * 更新的遮罩层
     */
    private BaseUpdateMaskFragment mUpdateFragment;

    /**
     * 发送错误的遮罩层
     */
    private BaseLoadMaskFragment mErrorMaskFragment;

    /**
     * 自定义遮罩层
     */
    private BaseLoadMaskFragment mCustomMaskFragment;


    public MaskManager(FragmentManager supportFragmentManager) {
        handler = new Handler(Looper.getMainLooper());
        this.supportFragmentManager = supportFragmentManager;
        mUsualMaskFragment = new DefaultUsualMaskFragment();
        mErrorMaskFragment = new DefaultErrorMaskFragment();
        mUpdateFragment = new DefaultUpdateFragment();
        setUsualMask(mUsualMaskFragment);
        setErrorMask(mErrorMaskFragment);
        setUpdateMask(mUpdateFragment);
    }


    /**
     * 添加遮罩层的 fragment
     *
     * @param fragment 需要继承自 MaskFragment 的 fragment
     */
    public void setUsualMask(BaseLoadMaskFragment fragment) {
        if (null != fragment) {
            if (null != mUsualMaskFragment) {
                removeFragment(mUsualMaskFragment);
            }
            mUsualMaskFragment = fragment;
            addFragmentToContent(mUsualMaskFragment);
            hideFragment(mUsualMaskFragment);
        }
    }

    /**
     * 添加遮罩层的 fragment
     *
     * @param fragment 需要继承自 MaskFragment 的 fragment
     */
    public void setErrorMask(BaseLoadMaskFragment fragment) {
        if (null != fragment) {
            if (null != mErrorMaskFragment) {
                removeFragment(mErrorMaskFragment);
            }
            mErrorMaskFragment = fragment;
            addFragmentToContent(mErrorMaskFragment);
            hideFragment(mErrorMaskFragment);
        }
    }

    /**
     * 添加遮罩层的 fragment
     *
     * @param fragment 需要继承自 MaskFragment 的 fragment
     */
    public void setUpdateMask(BaseUpdateMaskFragment fragment) {
        if (null != fragment) {
            if (null != mUpdateFragment) {
                removeFragment(mUpdateFragment);
            }
            mUpdateFragment = fragment;
            addFragmentToContent(mUpdateFragment);
            hideFragment(mUpdateFragment);
        }
    }


    /**
     * 添加遮罩层的 fragment
     *
     * @param fragment 需要继承自 MaskFragment 的 fragment
     */
    public void addCustomMask(BaseLoadMaskFragment fragment) {
        if (null != fragment) {
            if (null != mCustomMaskFragment) {
                removeFragment(mCustomMaskFragment);
            }
            mCustomMaskFragment = fragment;
            addFragmentToContent(mCustomMaskFragment);
            hideFragment(mCustomMaskFragment);
        }

    }

    /**
     * 显示常规遮罩
     */
    public void showUsualMask() {
        handler.post(() -> {
            showFragment(mUsualMaskFragment);
        });
    }


    /**
     * 隐藏常规遮罩
     */
    public void dismissUsualMask() {
        handler.post(() -> {
            hideFragment(mUsualMaskFragment);
        });
    }

    /**
     * 显示错误遮罩
     */
    public void showErrorMask() {
        handler.post(() -> {
            showFragment(mErrorMaskFragment);
        });
    }


    /**
     * 隐藏错误遮罩
     */
    public void dismissErrorMask() {
        handler.post(() -> {
            hideFragment(mErrorMaskFragment);
        });
    }


    /**
     * 显示更新遮罩
     */
    public void showUpdateMask() {
        handler.post(() -> {
            showFragment(mUpdateFragment);
        });
    }


    /**
     * 隐藏更新遮罩
     */
    public void dismissUpdateMask() {
        handler.post(() -> {
            hideFragment(mUpdateFragment);
        });
    }

    /**
     * 显示自定义遮罩
     */
    public void showCustomMask() {
        handler.post(() -> {
            showFragment(mCustomMaskFragment);
        });
    }


    /**
     * 隐藏自定义遮罩
     */
    public void dismissCustomMask() {
        handler.post(() -> {
            hideFragment(mCustomMaskFragment);
        });
    }


    /**
     * 添加 fragment
     *
     * @param containerViewId 容器
     * @param fragment
     */
    private void addFragment(@IdRes int containerViewId, @NonNull Fragment fragment) {
        if (null == fragment) {
            return;
        }
        supportFragmentManager
                .beginTransaction()
                .add(containerViewId, fragment)
                .commitAllowingStateLoss();
    }

    /**
     * 向根布局添加 fragment
     *
     * @param fragment
     */
    private void addFragmentToContent(@NonNull Fragment fragment) {
        addFragment(android.R.id.content, fragment);
    }


    /**
     * 添加 fragment
     *
     * @param containerViewId 容器
     * @param fragment
     * @param fragmentNmae    “your fragment's name”
     */
    private void addFragment(@IdRes int containerViewId, @NonNull Fragment fragment, String fragmentNmae) {
        if (null == fragment) {
            return;
        }
        supportFragmentManager
                .beginTransaction()
                .add(containerViewId, fragment, fragmentNmae)
                .commitAllowingStateLoss();
    }

    /**
     * 向根布局添加 fragment
     *
     * @param fragment
     * @param fragmentNmae “your fragment's name”
     */
    private void addFragmentToContent(@NonNull Fragment fragment, String fragmentNmae) {
        addFragment(android.R.id.content, fragment, fragmentNmae);
    }

    /**
     * 移除fragment
     *
     * @param fragment
     */
    private void removeFragment(@NonNull Fragment fragment) {
        supportFragmentManager
                .beginTransaction()
                .remove(fragment)
                .commitAllowingStateLoss();

    }


    /**
     * 替换fragment
     *
     * @param containerViewId
     * @param fragment
     */
    private void replaceFragment(@IdRes int containerViewId, @NonNull Fragment fragment) {
        if (null == fragment) {
            return;
        }
        supportFragmentManager
                .beginTransaction()
                .replace(containerViewId, fragment)
                .commitAllowingStateLoss();
    }

    /**
     * 替换fragment
     *
     * @param fragment
     */
    private void replaceFragmentToContent(@NonNull Fragment fragment) {
        if (null == fragment) {
            return;
        }
        replaceFragment(android.R.id.content, fragment);
    }

    /**
     * 显示fragment
     *
     * @param fragment
     */
    private void showFragment(@NonNull Fragment fragment) {
        supportFragmentManager
                .beginTransaction()
                .show(fragment)
                .commitAllowingStateLoss();
    }

    /**
     * 隐藏fragment
     *
     * @param fragment
     */
    private void hideFragment(@NonNull Fragment fragment) {
        supportFragmentManager
                .beginTransaction()
                .hide(fragment)
                .commitAllowingStateLoss();
    }
}
