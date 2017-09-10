package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by dubo on 2017/9/9.
 */

public class JsonUtil {
    /**
     *
     * 描述：将json转化为列表.
     * @param json
     * @param typeToken new TypeToken<ArrayList<?>>() {}.getType();
     * @return
     */
    public static List<?> fromJson(String json, TypeToken typeToken) {
        List<?> list = null;
        try {
            GsonBuilder gsonb = new GsonBuilder();
            Gson gson = gsonb.create();
            Type type = typeToken.getType();
            list = gson.fromJson(json,type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     *
     * 描述：将json转化为对象.
     * @param json
     * @param clazz
     * @return
     */
    public static Object fromJson(String json,Class clazz) {
        Object obj = null;
        try {
            GsonBuilder gsonb = new GsonBuilder();
            Gson gson = gsonb.create();
            obj = gson.fromJson(json,clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
}
