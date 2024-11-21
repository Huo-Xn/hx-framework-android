package wendu.dsbridge.tool;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * gson 解析 工具类
 */
public class GsonUtil {

    /**
     * 将 json 解析成 键值对的形式
     *
     * @param data
     * @return
     */
    public static Map<String, String> toMap(String data) {
        Gson gson = new Gson();
        Map<String, String> dataMap = new HashMap<>();
        try {
            Type type = new TypeToken<Map<String, String>>() {
            }.getType();
            dataMap = gson.fromJson(data, type);
            return dataMap;
        } catch (Exception e) {
            return dataMap;
        }
    }


    /**
     * 将map 转换成json
     *
     * @param map
     * @return
     */
    public static String mapToJson(HashMap<String, String> map) {
        Gson gson = new Gson();
        try {
            return gson.toJson(map);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 将map 转换成json
     *
     * @return
     */
    public static MapToJson mapToJson() {
       return  new MapToJson();
    }

    /**
     * 将map 转换成json
     *
     * @param
     * @return
     */
    public static String mapToJson(String[]... str) {
        if (null == str) {
            return null;
        }
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < str.length; i++) {
            String[] strings = str[i];
            if (null != strings) {
                if (strings.length == 2) {
                    map.put(strings[0], strings[1]);
                }
            }
        }
        return mapToJson(map);
    }



    /**
     * 转换为 实体类
     *
     * @param json
     * @param classOfT
     * @param <T>
     * @return
     */
    public static <T> T fromGson(String json, Class<T> classOfT) {
        Gson gson = new Gson();
        T t = gson.fromJson(json, classOfT);
        return t;
    }



    /**
     * 将实体，转换为 json 字符串
     *
     * @param o
     * @return
     */
    public static String toJson(Object o) {
        Gson gson = new Gson();
        String json = gson.toJson(o);
        return json;
    }


    public static class MapToJson {

        private HashMap<String, Object> map;


        private MapToJson() {
            map = new HashMap<>();
        }

        public MapToJson add(String key, String value) {
            map.put(key, value);
            return this;
        }

        public MapToJson add(String key, double value) {
            map.put(key, value);
            return this;
        }

        public MapToJson add(String key, boolean value) {
            map.put(key, value);
            return this;
        }

        public MapToJson add(String key, int value) {
            map.put(key, value);
            return this;
        }

        public MapToJson add(String key, Object value) {
            map.put(key, value);
            return this;
        }

        public String toJson() {
            return new Gson().toJson(map);
        }

    }

}
