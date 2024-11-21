package wendu.dsbridge.tool;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 创建一个缓存
 *
 * @author: admin
 * @date: 2023/5/22
 */
public class SP {

    private static volatile SP mInstance = new SP();


    public static SP getInstance() {
        if (mInstance == null) {
            mInstance = new SP();
        }
        return mInstance;
    }

    private SP() {

    }

    private Context context;
    private SharedPreferences sp;
    private SharedPreferences typeShare;

    /**
     * 声明加入 context 对象
     *
     * @param context
     */
    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * 声明name
     *
     * @param name
     */
    public void createSP(String name) {
        if (null != context) {
            sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
            typeShare = context.getSharedPreferences(name + "_TYPE", Context.MODE_PRIVATE);
        }
    }


    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param key
     * @param object
     */
    public void put(String key, Object object) {
        String type = object.getClass().getSimpleName();
        SharedPreferences.Editor editor = sp.edit();
        SharedPreferences.Editor typeEditor = typeShare.edit();

        if ("String".equals(type)) {
            editor.putString(key, (String) object);
            typeEditor.putString(key, "String");
        } else if ("Integer".equals(type)) {
            editor.putInt(key, (Integer) object);
            typeEditor.putString(key, "Integer");
        } else if ("Boolean".equals(type)) {
            editor.putBoolean(key, (Boolean) object);
            typeEditor.putString(key, "Boolean");
        } else if ("Float".equals(type)) {
            editor.putFloat(type, (Float) object);
            typeEditor.putString(key, "Float");
        } else if ("Long".equals(type)) {
            editor.putLong(type, (Long) object);
            typeEditor.putString(key, "Long");
        }
        editor.commit();
        typeEditor.commit();
    }


    public String getString(String key, String defaultValue) {
        return sp.getString(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        return sp.getInt(key, defaultValue);
    }

    public float getFloat(String key, float defaultValue) {
        return sp.getFloat(key, defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }

    public long getLong(String key, long defaultValue) {
        return sp.getLong(key, defaultValue);
    }


    public String getString(String key) {
        return getString(key, "");
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public float getFloat(String key) {
        return getFloat(key, 0f);
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public long getLong(String key) {
        return getLong(key, 0l);
    }
}
