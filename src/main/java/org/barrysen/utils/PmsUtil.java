package org.barrysen.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * 功能：excel导入日志文件信息
 *
 * @author: Barrysen
 * @date: 2025/1/3 13:43
 */
@Slf4j
@Component
public class PmsUtil {


    private static String uploadPath;

    @Value("")
    public void setUploadPath(String uploadPath) {
        PmsUtil.uploadPath = uploadPath;
    }

    public static String saveErrorTxtByList(List<String> msg, String name) throws IOException {
        Date d = new Date();
        String saveDir = "logs" + File.separator + DateUtils.yyyyMMdd.get().format(d) + File.separator;
        String saveFullDir = uploadPath + File.separator + saveDir;

        File saveFile = new File(saveFullDir);
        if (!saveFile.exists()) {
            saveFile.mkdirs();
        }
        name += DateUtils.yyyymmddhhmmss.get().format(d) + Math.round(Math.random() * 10000);
        String saveFilePath = saveFullDir + name + ".txt";
        BufferedWriter bw = null;
        try {
            //封装目的地
            bw = new BufferedWriter(new FileWriter(saveFilePath));
            //遍历集合
            for (String s : msg) {
                //写数据
                if (s.indexOf("_") > 0) {
                    String arr[] = s.split("_");
                    bw.write("第" + arr[0] + "行:" + arr[1]);
                } else {
                    bw.write(s);
                }
                //bw.newLine();
                bw.write("\r\n");
            }
            //释放资源
            bw.flush();
        } catch (Exception e) {
            log.info("excel导入生成错误日志文件异常:" + e.getMessage());
        } finally {
            if(bw != null){
                bw.close();
            }
        }
        return saveDir + name + ".txt";
    }

}
