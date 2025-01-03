package org.barrysen.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 功能：Map装JavaBean工具类
 *
 * @author: Barrysen
 * @date: 2025/1/3 13:41
 */
@Slf4j
public class MapToJavaBeanUtils {
    /**
     * 根据List<Map<String, Object>>数据转换为JavaBean数据
     *
     * @param datas
     * @param beanClass
     * @return java.util.List<T>
     */
    public static <T> List<T> ListMap2JavaBean(List<Map<String, Object>> datas, Class<T> beanClass) {
        // 返回数据集合
        List<T> list = null;
        // 对象字段名称
        String fieldname = "";
        // 对象方法名称
        String methodname = "";
        // 对象方法需要赋的值
        Object methodsetvalue = "";
        try {
            list = new ArrayList<T>();
            // 得到对象所有字段
            Field fields[] = beanClass.getDeclaredFields();
            // 遍历数据
            for (Map<String, Object> mapdata : datas) {
                // 创建一个泛型类型实例
                T t = beanClass.newInstance();
                // 遍历所有字段，对应配置好的字段并赋值
                for (Field field : fields) {
                    // 获取字段名称
                    fieldname = field.getName();
                    if (mapdata.get(fieldname.toLowerCase()) != null) {
                        methodsetvalue = mapdata.get(fieldname.toLowerCase());
                        // 赋值给字段
                        String methodTypeName = "set" + fieldname.substring(0, 1).toUpperCase() + fieldname.substring(1, fieldname.length());
                        Method method = t.getClass().getMethod(methodTypeName, field.getType());
                        method.invoke(t, methodsetvalue);
                    } else if (mapdata.get(fieldname) != null) {
                        methodsetvalue = mapdata.get(fieldname);
                        // 赋值给字段
                        String methodTypeName = "set" + fieldname.substring(0, 1).toUpperCase() + fieldname.substring(1, fieldname.length());
                        Method method = t.getClass().getMethod(methodTypeName, field.getType());
                        method.invoke(t, methodsetvalue);
                    }
                }
                list.add(t);
            }
        } catch (InstantiationException e) {
            log.error("创建beanClass实例异常", e);
        } catch (IllegalAccessException e) {
            log.error("创建beanClass实例异常", e);
        } catch (SecurityException e) {
            log.error("获取[" + fieldname + "] getter setter 方法异常", e);
        } catch (IllegalArgumentException e) {
            log.error("[" + methodname + "] 方法赋值异常", e);
        } catch (NoSuchMethodException e) {
            log.error("[" + methodname + "] 方法赋值异常", e);
        } catch (InvocationTargetException e) {
            log.error("[" + methodname + "] 方法赋值异常", e);
        }
        // 返回
        return list;
    }

    /**
     * 功能：jsonArray转Map（含children的情况）
     *
     * @param jsonArray
     * @return: java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     * @author: Barry
     * @date: 2024/11/26 15:54
     */
    public static List<Map<String, Object>> jsonArrayToMapChildren(JSONArray jsonArray) {
        List<Map<String, Object>> result = new LinkedList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            Map<String, Object> map = new HashMap<>();
            for (String key : jsonObject1.keySet()) {
                Object value = jsonObject1.get(key);
                if (key.equals("children")) {
                    value = jsonArrayToMapChildren(JSONArray.parseArray(jsonObject1.get(key).toString()));
                }
                map.put(key, value);
            }
            result.add(map);
        }
        return result;
    }
}
