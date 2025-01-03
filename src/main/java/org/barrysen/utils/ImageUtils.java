package org.barrysen.utils;

import gui.ava.html.parser.HtmlParser;
import gui.ava.html.parser.HtmlParserImpl;
import gui.ava.html.renderer.ImageRenderer;
import gui.ava.html.renderer.ImageRendererImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Base64;
import java.util.UUID;

/**
 * 功能：Image相关操作类
 *
 * @author: Barrysen
 * @date: 2025/1/3 13:39
 */
@Component
@Slf4j
public class ImageUtils {

    /**
     * 默认图片地址
     */
    private static String defaultSaveImagePath;

    @Value("")
    public void setDefaultSaveImagePath(String defaultSaveImagePath) {
        ImageUtils.defaultSaveImagePath = defaultSaveImagePath;
    }

    /**
     * HTML转图片
     *
     * @param htmlStr       HTML代码
     * @param imageExt      （非必传）图片后缀（如png.jpg  不含.） 默认：png
     * @param saveImagePath （非必传）图片保存路径 为空则上传默认路径
     * @return: java.lang.String 图片保存地址
     * @author: Barry
     * @date: 2022/3/3 13:34
     */
    public static String htmlToImage(String htmlStr, String imageExt, String saveImagePath) throws IOException {
        if (ObjectConvertUtils.isEmpty(imageExt)) {
            imageExt = "png";
        }
        if (ObjectConvertUtils.isEmpty(saveImagePath)) {
            saveImagePath = defaultSaveImagePath;
        }
        HtmlParser htmlParser = new HtmlParserImpl();
        htmlParser.loadHtml(htmlStr);
        // html 是我的html代码
        ImageRenderer imageRenderer = new ImageRendererImpl(htmlParser);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String imageName = uuid + "." + imageExt;
        imageRenderer.saveImage(saveImagePath + imageName);
        return saveImagePath + imageName;
    }


    /**
     * 根据图片地址转换为base64编码字符串
     *
     * @param imgFilePath 图片地址
     * @return: java.lang.String
     * @author: Barry
     * @date: 2022/3/3 14:26
     */
    public static String getImageBase64(String imgFilePath) throws IOException {
        InputStream inputStream = null;
        byte[] data = null;
        inputStream = new FileInputStream(imgFilePath);
        data = new byte[inputStream.available()];
        inputStream.read(data);
        inputStream.close();

        // 加密
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(data);
    }

    /**
     * 获取图片md5加密的值
     *
     * @param imgFilePath 图片地址
     * @return: java.lang.String
     * @author: Barry
     * @date: 2022/3/3 14:29
     */
    public static String getImageMd5(String imgFilePath) throws IOException {
        return DigestUtils.md5Hex(new FileInputStream(imgFilePath));
    }

    /**
     * 功能：预览图片
     *
     * @param imgFilePath 图片绝对地址
     * @param response    输出
     * @return: void
     * @author: Barry
     * @date: 2022/7/21 9:57 AM
     */
    public static void previewImage(String imgFilePath, HttpServletResponse response) throws IOException {
        File file = new File(imgFilePath);
        if (!file.exists()) {
            response.setStatus(404);
            throw new RuntimeException("文件不存在..");
        }
        // 其余处理略
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
        try {
            response.setHeader("Content-Type", "image/" + file.getName().substring(file.getName().lastIndexOf(".") + 1));
            byte b[] = new byte[1024];
            int read;
            while ((read = bis.read(b)) != -1) {
                bos.write(b, 0, read);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            if (bos != null) {
                bos.close();
            }
            if (bis != null) {
                bis.close();
            }
        }
    }
}
