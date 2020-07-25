package me.chan.util;

import com.alibaba.fastjson.JSONObject;

public class JSONUtil {

    public static <T> T parseJSONStr2Obj(String text, Class<T> clazz) {

        T t = JSONObject.parseObject(text, clazz);
        return t;
    }

    public static <T> T parseJSON2Obj(JSONObject json, Class<T> clazz) {
        return JSONObject.toJavaObject(json, clazz);
    }
}
