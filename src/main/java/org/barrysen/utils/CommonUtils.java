package org.barrysen.utils;

import lombok.extern.slf4j.Slf4j;
import org.barrysen.constant.CommonConstant;
import org.barrysen.constant.DataBaseConstant;
import org.barrysen.exception.BaseException;
import org.jeecgframework.poi.util.PoiPublicUtil;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 功能：通用工具类
 *
 * @author: Barrysen
 * @date: 2025/1/3 13:37
 */
@Slf4j
public class CommonUtils {

   //中文正则
    private static Pattern ZHONGWEN_PATTERN = Pattern.compile("[\u4e00-\u9fa5]");

    public static String uploadOnlineImage(byte[] data,String basePath,String bizPath,String uploadType){
        String dbPath = null;
        String fileName = "image" + Math.round(Math.random() * 100000000000L);
        fileName += "." + PoiPublicUtil.getFileExtendName(data);
        try {
            if(CommonConstant.UPLOAD_TYPE_LOCAL.equals(uploadType)){
                File file = new File(basePath + File.separator + bizPath + File.separator );
                if (!file.exists()) {
                    file.mkdirs();// 创建文件根目录
                }
                String savePath = file.getPath() + File.separator + fileName;
                File savefile = new File(savePath);
                FileCopyUtils.copy(data, savefile);
                dbPath = bizPath + File.separator + fileName;
            }else {
                InputStream in = new ByteArrayInputStream(data);
                String relativePath = bizPath+"/"+fileName;
                if(CommonConstant.UPLOAD_TYPE_MINIO.equals(uploadType)){
                    dbPath = MinioUtil.upload(in,relativePath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dbPath;
    }

    /**
     * 判断文件名是否带盘符，重新处理
     * @param fileName
     * @return
     */
    public static String getFileName(String fileName){
        //判断是否带有盘符信息
        // Check for Unix-style path
        int unixSep = fileName.lastIndexOf('/');
        // Check for Windows-style path
        int winSep = fileName.lastIndexOf('\\');
        // Cut off at latest possible point
        int pos = (winSep > unixSep ? winSep : unixSep);
        if (pos != -1)  {
            // Any sort of path separator found...
            fileName = fileName.substring(pos + 1);
        }
        //替换上传文件名字的特殊字符
        fileName = fileName.replace("=","").replace(",","").replace("&","").replace("#", "");
        //替换上传文件名字中的空格
        fileName=fileName.replaceAll("\\s","");
        return fileName;
    }

    // java 判断字符串里是否包含中文字符
    public static boolean ifContainChinese(String str) {
        if(str.getBytes().length == str.length()){
            return false;
        }else{
            Matcher m = ZHONGWEN_PATTERN.matcher(str);
            if (m.find()) {
                return true;
            }
            return false;
        }
    }

    /**
     * 统一全局上传
     * @Return: java.lang.String
     */
    public static String upload(MultipartFile file, String bizPath, String uploadType) {
        String url = "";
        if(CommonConstant.UPLOAD_TYPE_MINIO.equals(uploadType)){
            url = MinioUtil.upload(file,bizPath);
        }
        return url;
    }

    /**
     * 统一全局上传 带桶
     * @Return: java.lang.String
     */
    public static String upload(MultipartFile file, String bizPath, String uploadType, String customBucket) {
        String url = "";
        if(CommonConstant.UPLOAD_TYPE_MINIO.equals(uploadType)){
            url = MinioUtil.upload(file,bizPath,customBucket);
        }
        return url;
    }

    /** 当前系统数据库类型 */
    private static String DB_TYPE = "";
    public static String getDatabaseType() {
        if(ObjectConvertUtils.isNotEmpty(DB_TYPE)){
            return DB_TYPE;
        }
        DataSource dataSource = SpringContextUtils.getApplicationContext().getBean(DataSource.class);
        try {
            return getDatabaseTypeByDataSource(dataSource);
        } catch (SQLException e) {
            //e.printStackTrace();
            log.warn(e.getMessage());
            return "";
        }
    }

    /**
     * 获取数据库类型
     * @param dataSource
     * @return
     * @throws SQLException
     */
    private static String getDatabaseTypeByDataSource(DataSource dataSource) throws SQLException{
        if("".equals(DB_TYPE)) {
            Connection connection = dataSource.getConnection();
            try {
                DatabaseMetaData md = connection.getMetaData();
                String dbType = md.getDatabaseProductName().toLowerCase();
                if(dbType.indexOf("mysql")>=0) {
                    DB_TYPE = DataBaseConstant.DB_TYPE_MYSQL;
                }else if(dbType.indexOf("oracle")>=0 ||dbType.indexOf("dm")>=0) {
                    DB_TYPE = DataBaseConstant.DB_TYPE_ORACLE;
                }else if(dbType.indexOf("sqlserver")>=0||dbType.indexOf("sql server")>=0) {
                    DB_TYPE = DataBaseConstant.DB_TYPE_SQLSERVER;
                }else if(dbType.indexOf("postgresql")>=0) {
                    DB_TYPE = DataBaseConstant.DB_TYPE_POSTGRESQL;
                }else {
                    throw new BaseException("数据库类型:["+dbType+"]不识别!");
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }finally {
                connection.close();
            }
        }
        return DB_TYPE;

    }

    /**
     * 文件转字节码
     * @param file
     * @return
     */
    public static byte[] fileToByte(File file) {
        byte[] content = null;
        BufferedInputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            out = new ByteArrayOutputStream(1024);

            byte[] temp = new byte[1024];
            int size = 0;
            while ((size = in.read(temp)) != -1) {
                out.write(temp, 0, size);
            }
            content = out.toByteArray();
        } catch (Exception e) {
            log.error("file to byte stream execute exception", e);
        } finally {
            try {
                if(in != null){
                    in.close();
                }
                if(out != null){
                    out.close();
                }
            } catch (IOException e) {
                log.error("file to byte stream close exception", e);
            }
        }
        return content;
    }

    /**
     * 比较两个List的元素值是否全部一致
     * @param t1
     * @param t2
     * @return
     */
    public static <T> boolean listEquals(List<T> t1, List<T> t2) {
        if((CollectionUtils.isEmpty(t1) || CollectionUtils.isEmpty(t2)) && t1 != t2){
               return  false;
        }
        if(!CollectionUtils.isEmpty(t1) && !CollectionUtils.isEmpty(t2)){
            if(t1 == t2){
                return true;
            }
            if(t1.size() != t2.size()){
                return false;
            }
            for (T t : t1) {
                if (!t2.contains(t)) { // equals比较
                    return false;
                }
            }
        }
        return true;
    }

    public static String replaceBlank(String str) {

        String dest = "";

        if (str != null) {

            Pattern p = Pattern.compile("\\s*|\t|\r|\n");

            Matcher m = p.matcher(str);

            dest = m.replaceAll("");

        }

        return dest;

    }
    /**
     * 验证手机号格式
     *
     * @param phone
     * @return: boolean
     * @author: Barry
     * @date: 2021/8/10 15:33
     */
    public static boolean validatePhone(String phone) {
        String phonePattern = "^1[0-9]{10}$";
        Pattern p = Pattern.compile(phonePattern);
        Matcher matcher = p.matcher(phone);
        return matcher.matches();
    }

    /**
     * 验证邮箱格式
     *
     * @param email
     * @return: boolean
     * @author: Barry
     * @date: 2021/8/10 15:34
     */
    public static boolean validateEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
        Pattern p = Pattern.compile(emailPattern);
        Matcher matcher = p.matcher(email);
        return matcher.matches();
    }
}