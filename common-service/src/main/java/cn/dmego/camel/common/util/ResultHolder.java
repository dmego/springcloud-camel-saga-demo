package cn.dmego.camel.common.util;

import cn.dmego.camel.common.constant.HolderType;
import cn.dmego.camel.common.dto.BaseDTO;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @className: ResultHolder
 *
 * @description: 幂等控制工具类
 * @author: ZengKai<dmeago@gmail.com>
 * @date: 2020/12/8 17:37
 **/
public class ResultHolder {

    private static final Map<Class<?>, Map<String, HolderType>> MAP = new ConcurrentHashMap<>();

    private static final String IT = ":";

    public static void setResult(Class<?> actionClass, String key, HolderType v) {
        Map<String, HolderType> results = MAP.get(actionClass);

        if (results == null) {
            synchronized (MAP) {
                if (results == null) {
                    results = new ConcurrentHashMap<>();
                    MAP.put(actionClass, results);
                }
            }
        }

        results.put(key, v);
    }

    public static HolderType getResult(Class<?> actionClass, String key) {
        Map<String, HolderType> results = MAP.get(actionClass);
        if (results != null) {
            return results.get(key);
        }

        return null;
    }

    public static void removeResult(Class<?> actionClass, String key) {
        Map<String, HolderType> results = MAP.get(actionClass);
        if (results != null) {
            results.remove(key);
        }
    }

    public static String createKey(BaseDTO base, String key) {
        return base.getHolderId() + IT + key;
    }


}
