package org.barrysen.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 功能：json工具类
 *
 * @author: Barrysen
 * @date: 2025/1/3 13:39
 */
public class JsonUtils {

    public static ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // 忽略未知属性

    /**
     * 将对象转换成json字符串
     * @param object
     * @return
     * @throws Exception
     */
    public static String toJson(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }

    /**
     * 将json字符串转换成对象
     * @param json
     * @param valueType
     * @return
     * @throws Exception
     */
    public static <T> T toObject(String json, Class<T> valueType) throws Exception {
        return objectMapper.readValue(json, valueType);
    }

    /**
     * json转对象(容器对象类型)
     * List<Bean> : (json, List.class, Bean.class)
     * Map<Bean1, Bean2> : (json, Map.class, Bean1.class, Bean2.class)
     * @param json
     * @param parametrized 容器类型
     * @param parameterClasses 实体类型
     * @return
     * @throws Exception
     */
    public static <T> T toObject(String json, Class<?> parametrized, Class<?>... parameterClasses) throws Exception {
        return objectMapper.readValue(json, objectMapper.getTypeFactory().constructParametricType(parametrized, parameterClasses));
    }

    /**
     * json转对象(容器嵌套类型)
     * List<List<Object>> : (json, new TypeReference<List<List<Object>>>(){})
     * Map<String, List<List<Object>>> : (json, new TypeReference<Map<String, List<List<Object>>>(){})
     * @param json
     * @param valueTypeRef
     * @return
     * @throws Exception
     */
    public static <T> T toObject(String json, TypeReference<T> valueTypeRef) throws Exception {
        return objectMapper.readValue(json, valueTypeRef);
    }
}
