package org.barrysen.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 功能：ok http接口调用方法工具类
 *
 * @author: Barrysen
 * @date: 2025/1/3 13:42
 */
@Slf4j
public class OkHttpClientUtils {

    /**
     * get方法接口返回信息
     * @param url
     * @param readTimeOut
     * @return
     */
    public static String getMethodResponseString(String url, Long readTimeOut){
        if(readTimeOut == null){
            readTimeOut = 30000l;
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(readTimeOut, TimeUnit.MILLISECONDS)
                .build();
        final Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = client.newCall(request);
        Response response = null;
        try {
            response = call.execute();
            if (response != null) {
                if (response.isSuccessful()) {
                    return response.body().string();
                }
                log.info("visit url: " , url);
            }
        } catch (IOException e) {
            log.error("JSON parse Exception :", e);
        }
        return null;
    }

    /**
     * post方法接口返回信息
     * @param url
     * @param readTimeOut
     * @return
     */
    public static String postMethodResponseString(String url, Long readTimeOut,  Object object) {
        if(readTimeOut == null){
            readTimeOut = 30000l;
        }
        String jsonStr = JSONObject.toJSON(object).toString();
        log.info("--post请求--" + jsonStr);
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(readTimeOut, TimeUnit.MILLISECONDS)
                .build();
        final Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json; charest=utf-8"), JSONObject.toJSON(object).toString()))
                .build();
        Call call = client.newCall(request);
        Response response = null;
        try {
            response = call.execute();
            if (response != null) {
                if (response.isSuccessful()) {
                    log.info(MessageUtils.get("public_interface_visit_success"));
                    return response.body().string();
                }
                log.info("visit url: " , url);
            }
        } catch (IOException e) {
            log.error("JSON parse Exception :", e);
        }
        return null;
    }
}
