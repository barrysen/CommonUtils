package org.barrysen.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/*import cn.com.goldwind.ercp.module.api.ApiParamCity;
import cn.com.goldwind.ercp.module.api.ApiParamCityResult;*/
/**
 * 功能：封装http请求的工具类
 *
 * @author: Barrysen
 * @date: 2025/1/3 13:38
 */
@Slf4j
public class HttpUtil {

    public static final MediaType POSTJSON = MediaType.parse("application/json; charset=utf-8");

    public static String postByJson(String url, String json) {
        String resultJson = null;
        // 申明给服务端传递一个json串
        // 创建一个OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().retryOnConnectionFailure(true)
                .connectTimeout(500, TimeUnit.SECONDS).readTimeout(500, TimeUnit.SECONDS).build();
        // 创建一个RequestBody(参数1：数据类型 参数2传递的json串)
        RequestBody requestBody = RequestBody.create(POSTJSON, json);
        // 创建一个请求对象
        Request request = new Request.Builder().url(url).post(requestBody).build();
        // 发送请求获取响应
        try {
            Response response = okHttpClient.newCall(request).execute();
            // 判断请求是否成功
            if (response.isSuccessful()) {
                resultJson = response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultJson;
    }

    /**
     * 慧能气象专用
     *
     * @param url   地址
     * @param useId 用户名
     * @param pwd   密码
     * @return
     * @throws IOException
     */
    public static String HuiNengGet(String url, String useId, String pwd) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder().retryOnConnectionFailure(true)
                .connectTimeout(500, TimeUnit.SECONDS).readTimeout(500, TimeUnit.SECONDS).build();
        //SslUtil.SslParams sslParams = SslUtil.getSslSocketFactory(null, null, null);
        String credential = Credentials.basic(useId, pwd);
        Request request = new Request.Builder().url(url).header("Authorization", credential).build();

       /* client = new OkHttpClient.Builder().connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(60000L, TimeUnit.MILLISECONDS)
                *//** 其他配置 *//*
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                }).sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager).build();
*/
        Response response = client.newCall(request).execute();

        if (!StringUtils.isEmpty(response) && response.code() == 200 && !StringUtils.isEmpty(response.body())) {
            return response.body().string();
        }
        return null;
    }


    /**
     * 通用url get方法
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String infoGet(String url) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder().retryOnConnectionFailure(true)
                .connectTimeout(500, TimeUnit.SECONDS).readTimeout(500, TimeUnit.SECONDS).build();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        if (!StringUtils.isEmpty(response) && response.code() == 200 && !StringUtils.isEmpty(response.body())) {
            String info = response.body().string();
            return info;
        }
        return null;
    }

    /**
     * 返回字符串
     *
     * @param urlParam
     * @param json
     * @return
     */
    public static String modelStringPostByJson(String urlParam, Object json) {
        String resultJson = null;
        // 申明给服务端传递一个json串
        // 创建一个OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().retryOnConnectionFailure(true)
                .connectTimeout(500, TimeUnit.SECONDS).readTimeout(500, TimeUnit.SECONDS).build();
        // 创建一个RequestBody(参数1：数据类型 参数2传递的json串)
        RequestBody requestBody = RequestBody.create(POSTJSON, JSON.toJSONString(json));
        // 创建一个请求对象
        // Request request = new
        // Request.Builder().url("http://127.0.0.1:8089/model/api/" +
        // urlParam).post(requestBody).build();
        Request request = new Request.Builder().url(urlParam).post(requestBody).build();
        // 发送请求获取响应
        try {
            Response response = okHttpClient.newCall(request).execute();
            // 判断请求是否成功
            if (response.isSuccessful()) {
                resultJson = response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultJson;
    }

    //日期格式转化
    public static String date2String(Date date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    public static Date parseDate(String string, String string2) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(string2);
        Date d1 = null;
        try {
            d1 = simpleDateFormat.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d1;

    }

    public static JSONObject doGet(String vesselUrl) {
        // 1. 创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 2. 创建HttpGet对象
        JSONObject jsonObject = null;
        HttpGet httpGet = new HttpGet(vesselUrl);
        CloseableHttpResponse response = null;
        try {
            // 执行GET请求
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 返回json格式：
                String resultStr = EntityUtils.toString(response.getEntity(), "UTF-8");
                jsonObject = JSONObject.parseObject(resultStr);
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    public static String doGetToString(String vesselUrl) {
        // 1. 创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 2. 创建HttpGet对象
        String resultStr = "";
        HttpGet httpGet = new HttpGet(vesselUrl);
        CloseableHttpResponse response = null;
        try {
            // 执行GET请求
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 返回json格式：
                resultStr = EntityUtils.toString(response.getEntity());
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultStr;
    }

    public static JSONObject doPost(JSONObject date, String vesselUrl) {
        CloseableHttpClient client = HttpClients.createDefault();
        // 要调用的接口方法
        HttpPost post = new HttpPost(vesselUrl);
        JSONObject jsonObject = null;
        try {
            if (date != null) {
                StringEntity s = new StringEntity(date.toString(), Charset.forName("UTF-8"));
                post.setEntity(s);
            }
            post.addHeader("Content-Type", "application/json;charset=UTF-8");
            post.addHeader("Accept", "application/json");
            HttpResponse res = client.execute(post);

            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 返回json格式：
                String result = EntityUtils.toString(res.getEntity(), "UTF-8");
                jsonObject = JSONObject.parseObject(result);
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return jsonObject;
    }

    public static String doPostToString(JSONObject date, String vesselUrl) {
        CloseableHttpClient client = HttpClients.createDefault();
        // 要调用的接口方法
        HttpPost post = new HttpPost(vesselUrl);
        String result = "";
        try {
            if (date != null) {
                StringEntity s = new StringEntity(date.toString(), Charset.forName("UTF-8"));
                post.setEntity(s);
            }
            post.addHeader("Content-Type", "application/json;charset=UTF-8");
            post.addHeader("Accept", "application/json");
            HttpResponse res = client.execute(post);

            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 返回json格式：
                result = EntityUtils.toString(res.getEntity(), "UTF-8");
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return result;
    }

    /**
     * @Description 外部物流信息接口
     */
    public static JSONObject doUpdateShipPost(Map<String, Object> date, String url) {

        String jsonText = "{\"placeEnd\":\"" + (String) date.get("placeEnd") + "\",";
        jsonText += "\"placeEndLat\":\"" + (String) date.get("placeEndLat") + "\",";
        jsonText += "\"placeEndLng\":\"" + (String) date.get("placeEndLng") + "\",";
        jsonText += "\"placeStart\":\"" + (String) date.get("placeStart") + "\",";
        jsonText += "\"placeStartLat\":\"" + (String) date.get("placeStartLat") + "\",";
        jsonText += "\"placeStartLng\":\"" + (String) date.get("placeStartLng") + "\",";
        jsonText += "\"projectId\":\"" + (String) date.get("projectId") + "\",";
        jsonText += "\"shipId\":\"" + (String) date.get("shipId") + "\",";
        List<Map<String, Object>> mapList = (List<Map<String, Object>>) date.get("maincomponent");
        String retMaincomponent = "{\\\"maincomponent\\\":[";
        for (Map<String, Object> map : mapList) {
            retMaincomponent += "{\\\"maincomponent\\\":\\\"" + (String) map.get("mainComponent") + "\\\",\\\"wtgtype\\\":\\\"" + (String) map.get("wtgtype") + "\\\",\\\"numberonroute\\\":\\\"" + (String) map.get("numberonroute") + "\\\"},";
        }
        retMaincomponent = retMaincomponent.substring(0, retMaincomponent.length() - 1);
        retMaincomponent += "]}";
        jsonText += "\"waybillContent\":\" " + retMaincomponent + "\"";
        jsonText += "}";


        JSONObject json = (JSONObject) JSONObject.parse(jsonText);
        CloseableHttpClient client = HttpClients.createDefault();
        // 要调用的接口方法
        HttpPost post = new HttpPost(url);
        JSONObject jsonObject = null;
        try {
            StringEntity s = new StringEntity(json.toString());
            post.setEntity(s);
            post.addHeader("Content-Type", "application/json");
            post.addHeader("Accept", "application/json");
            HttpResponse res = client.execute(post);

            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 返回json格式：
                String result = EntityUtils.toString(res.getEntity());
                jsonObject = JSONObject.parseObject(result);
                System.out.println("调用外部接口：根据项目主键与船号修改已提交船信息，成功！" + new Date());
            } else {
                throw new RuntimeException("调用外部接口：根据项目主键与船号修改已提交船信息，失败！" + new Date());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }


    public static JSONObject doAddShipPost(Map<String, Object> date, String url) {

        String jsonText = "{\"placeEnd\":\"" + (String) date.get("placeEnd") + "\",";
        jsonText += "\"placeEndLat\":\"" + (String) date.get("placeEndLat") + "\",";
        jsonText += "\"placeEndLng\":\"" + (String) date.get("placeEndLng") + "\",";
        jsonText += "\"placeStart\":\"" + (String) date.get("placeStart") + "\",";
        jsonText += "\"placeStartLat\":\"" + (String) date.get("placeStartLat") + "\",";
        jsonText += "\"placeStartLng\":\"" + (String) date.get("placeStartLng") + "\",";
        jsonText += "\"projectId\":\"" + (String) date.get("projectId") + "\",";
        jsonText += "\"shipId\":\"" + (String) date.get("shipId") + "\",";
        List<Map<String, Object>> mapList = (List<Map<String, Object>>) date.get("maincomponent");
        String retMaincomponent = "{\\\"maincomponent\\\":[";
        for (Map<String, Object> map : mapList) {
            retMaincomponent += "{\\\"maincomponent\\\":\\\"" + (String) map.get("mainComponent") + "\\\",\\\"wtgtype\\\":\\\"" + (String) map.get("wtgtype") + "\\\",\\\"numberonroute\\\":\\\"" + (String) map.get("numberonroute") + "\\\"},";
        }
        retMaincomponent = retMaincomponent.substring(0, retMaincomponent.length() - 1);
        retMaincomponent += "]}";
        jsonText += "\"waybillContent\":\" " + retMaincomponent + "\"";
        jsonText += "}";


        JSONObject json = (JSONObject) JSONObject.parse(jsonText);
        CloseableHttpClient client = HttpClients.createDefault();
        // 要调用的接口方法
        HttpPost post = new HttpPost(url);
        JSONObject jsonObject = null;
        try {
            StringEntity s = new StringEntity("[" + json.toString() + "]");
            post.setEntity(s);
            post.addHeader("Content-Type", "application/json");
            post.addHeader("Accept", "application/json");
            HttpResponse res = client.execute(post);

            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 返回json格式：
                String result = EntityUtils.toString(res.getEntity());
                jsonObject = JSONObject.parseObject(result);
                System.out.println("调用外部接口：批量提交需要监控的船信息，成功！" + new Date());
            } else {
                throw new RuntimeException("调用外部接口：批量提交需要监控的船信息，失败！" + new Date());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }


    public static JSONObject doDelShipPost(String contractNo, String shippingInformationStr, String url) {

        String[] split = shippingInformationStr.split("：");

        String jsonText = "{\"projectId\":\"" + contractNo + "\",";
        jsonText += "\"shipId\":\"" + split[1].trim() + "\",";
        jsonText += "}";


        JSONObject json = (JSONObject) JSONObject.parse(jsonText);
        CloseableHttpClient client = HttpClients.createDefault();
        // 要调用的接口方法
        HttpPost post = new HttpPost(url);
        JSONObject jsonObject = null;
        try {
            StringEntity s = new StringEntity(json.toString());
            post.setEntity(s);
            post.addHeader("Content-Type", "application/json");
            post.addHeader("Accept", "application/json");
            HttpResponse res = client.execute(post);

            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 返回json格式：
                String result = EntityUtils.toString(res.getEntity());
                jsonObject = JSONObject.parseObject(result);
                System.out.println("调用外部接口：删除船信息，成功！" + new Date());
            } else {
                throw new RuntimeException("调用外部接口：删除船信息，失败！" + new Date());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }


    public static List<Map<String, Object>> selectProjectShipListByCno(String contractNo, String url) {

        String jsonText = "{\"projectId\":\"" + contractNo + "\"}";

        JSONObject json = (JSONObject) JSONObject.parse(jsonText);
        CloseableHttpClient client = HttpClients.createDefault();
        // 要调用的接口方法
        HttpPost post = new HttpPost(url);
        JSONObject jsonObject = null;
        List<Map<String, Object>> retLits = new ArrayList<>();
        try {
            StringEntity s = new StringEntity(json.toString());
            post.setEntity(s);
            post.addHeader("Content-Type", "application/json");
            post.addHeader("Accept", "application/json");
            HttpResponse res = client.execute(post);

            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 返回json格式：
                String result = EntityUtils.toString(res.getEntity());
                JSONObject jsonObject1 = JSONObject.parseObject(result);
                retLits = (List<Map<String, Object>>) jsonObject1.get("data");
                System.out.println("调用外部接口：根据项目编号获取船当前状态，成功！" + new Date());
            } else {
                throw new RuntimeException("调用外部接口：根据项目编号获取船当前状态，失败！" + new Date());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return retLits;
    }
    /**
     * 功能：POST请求（form-data）
     *
     * @param file      Body参数
     * @param vesselUrl 请求地址
     * @return: com.alibaba.fastjson.JSONObject
     * @author: Barry
     * @date: 2022/7/12 9:07 AM
     */
    public static JSONObject doPostForFormData(String filename, File file, String vesselUrl) {
        CloseableHttpClient client = HttpClients.createDefault();
        // 要调用的接口方法
        HttpPost post = new HttpPost(vesselUrl);
        JSONObject jsonObject = null;
        try {
            // 设置请求头 boundary边界不可重复，重复会导致提交失败
            String boundary = "-------------------------" + UUID.randomUUID().toString();
            post.setHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
            post.addHeader("Accept", "application/json");
            // 创建MultipartEntityBuilder
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            // 设置字符编码
            builder.setCharset(StandardCharsets.UTF_8);
            // 模拟浏览器
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            // 设置边界
            builder.setBoundary(boundary);
            String fileName = file.getName();
            if (!ObjectConvertUtils.isEmpty(filename)) {
                fileName = filename;
            }
            // 设置multipart/form-data流文件
            builder.addPart("multipartFile", new FileBody(file, ContentType.APPLICATION_OCTET_STREAM, fileName));
            // application/octet-stream代表不知道是什么格式的文件
            builder.addBinaryBody("media", file, ContentType.create("application/octet-stream"), fileName);
            HttpEntity entity = builder.build();
            post.setEntity(entity);
            HttpResponse res = client.execute(post);

            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 返回json格式：
                String result = EntityUtils.toString(res.getEntity());
                jsonObject = JSONObject.parseObject(result);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return jsonObject;
    }

    /**
     * 功能：POST请求（返回Text）
     *
     * @param date
     * @param vesselUrl
     * @return: com.alibaba.fastjson.JSONObject
     * @author: Barry
     * @date: 2022/8/10 8:47 AM
     */
    public static JSONObject doPostForText(JSONObject date, String vesselUrl) {
        CloseableHttpClient client = HttpClients.createDefault();
        // 要调用的接口方法
        HttpPost post = new HttpPost(vesselUrl);
        JSONObject jsonObject = null;
        try {
            if (date != null) {
                StringEntity s = new StringEntity(date.toString(), Charset.forName("UTF-8"));
                post.setEntity(s);
            }
            HttpResponse res = client.execute(post);

            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 返回json格式：
                String result = EntityUtils.toString(res.getEntity());
                jsonObject = JSONObject.parseObject(result);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return jsonObject;
    }

    /**
     * 功能：POST请求-自定义Header
     *
     * @param headers   Header参数集合
     * @param date      Body参数
     * @param vesselUrl 请求地址
     * @return: com.alibaba.fastjson.JSONObject
     * @author: Barry
     * @date: 2022/7/12 9:07 AM
     */
    public static JSONObject doPostCustomHeaders(Map<String, String> headers, JSONObject date, String vesselUrl) {
        CloseableHttpClient client = HttpClients.createDefault();
        //要调用的接口方法
        HttpPost post = new HttpPost(vesselUrl);
        JSONObject jsonObject = null;
        try {
            if (date != null) {
                StringEntity s = new StringEntity(date.toString(), Charset.forName("UTF-8"));
                post.setEntity(s);
            }
            post.addHeader("Content-Type", "application/json;charset=UTF-8");
            post.addHeader("Accept", "application/json");
            if (headers != null) {
                for (Map.Entry<String, String> stringStringEntry : headers.entrySet()) {
                    post.addHeader(stringStringEntry.getKey(), stringStringEntry.getValue());
                }
            }
            HttpResponse res = client.execute(post);

            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 返回json格式：
                String result = EntityUtils.toString(res.getEntity());
                jsonObject = JSONObject.parseObject(result);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return jsonObject;
    }

    public static JSONObject doPostUrl(JSONObject date, String vesselUrl) {
        CloseableHttpClient client = HttpClients.createDefault();
        // 要调用的接口方法
        HttpPost post = new HttpPost(vesselUrl);
        JSONObject jsonObject = null;
        try {
            StringEntity s = new StringEntity("[" + date.toString() + "]");
            post.setEntity(s);
            post.addHeader("Content-Type", "application/json");
            post.addHeader("Accept", "application/json");
            HttpResponse res = client.execute(post);

            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 返回json格式：
                String result = EntityUtils.toString(res.getEntity());
                jsonObject = JSONObject.parseObject(result);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return jsonObject;
    }


}
