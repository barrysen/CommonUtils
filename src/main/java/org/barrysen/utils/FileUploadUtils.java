package org.barrysen.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.apache.poi.util.IOUtils;
import org.barrysen.constant.CommonConstant;
import org.barrysen.vo.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 功能：通用上传文件（可自定义上传的文件夹）工具类
 *
 * @author: Barrysen
 * @date: 2025/1/3 13:38
 */
@Component
@Slf4j
public class FileUploadUtils {
    private String uploadpath;

    /**
     * 本地：local minio：minio 阿里：alioss
     */
    private String uploadType;

    /**
     * 通用上传文件（可自定义上传的文件夹）
     *
     * @param fileDir  上传文件夹（如：aaa）
     * @param request
     * @param response
     * @return: cn.com.goldwind.ercp.fcc.core.vo.Result<?>
     * @author: Barry
     * @date: 2021/10/21 10:57
     */
    public Result<?> upload(String fileDir, HttpServletRequest request, HttpServletResponse response) {
        Result<Map<String, Object>> result = new Result<>();
        String savePath = "";
        String bizPath = request.getParameter("biz");
        String fileName = "";
        Long fileSize = 0L;
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("file");// 获取上传文件对象
        if (ObjectConvertUtils.isEmpty(bizPath)) {
            if (CommonConstant.UPLOAD_TYPE_OSS.equals(uploadType)) {
                //未指定目录，则用阿里云默认目录 upload
                bizPath = "upload";
                //result.setMessage("使用阿里云文件上传时，必须添加目录！");
                //result.setSuccess(false);
                //return result;
            } else {
                bizPath = "";
            }
        }
        if (CommonConstant.UPLOAD_TYPE_LOCAL.equals(uploadType)) {
            //update-begin-author:lvdandan date:20200928 for:修改JEditor编辑器本地上传
            log.info("上传本地文件");
            savePath = this.uploadLocal(fileDir, file, bizPath);
            fileName = file.getOriginalFilename();// 获取文件名
            fileName = CommonUtils.getFileName(fileName);
            fileSize = file.getSize();
        } else {
            //update-begin-author:taoyan date:20200814 for:文件上传改造
            savePath = CommonUtils.upload(file, bizPath, uploadType);
            //update-end-author:taoyan date:20200814 for:文件上传改造
        }
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("fileName", fileName);
        map.put("filePath", savePath);
        map.put("fileDownFullUrl", "/sys/common/static/" + savePath);
        map.put("fileSize", fileSize);
        Float size = Float.parseFloat(String.valueOf(file.getSize())) / 1024;
        BigDecimal b = new BigDecimal(size);
        // 2表示2位 ROUND_HALF_UP表明四舍五入，
        Float fileSizeF = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        map.put("fileSizeKB", fileSizeF + "KB");
        if (ObjectConvertUtils.isNotEmpty(savePath)) {
            result.setResult(map);
            result.setSuccess(true);
        } else {
            result.setMessage("上传失败！");
            result.setSuccess(false);
        }
        return result;
    }


    /**
     * 通用上传多个文件（可自定义上传的文件夹）
     *
     * @param fileDir  上传文件夹（如：aaa）
     * @param request
     * @param response
     * @return: cn.com.goldwind.ercp.fcc.core.vo.Result<?>
     * @author: Barry
     * @date: 2021/10/21 10:57
     */
    public Result<?> uploadMultipart(String fileDir, HttpServletRequest request, HttpServletResponse response) {
        Result<List<Map<String, Object>>> result = new Result<>();
        String savePath = "";
        String bizPath = request.getParameter("biz");
        String fileName = "";
        Long fileSize = 0L;
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        List<Map<String, Object>> mapList = new LinkedList<>();
        List<MultipartFile> fileList = multipartRequest.getFiles("file");// 获取上传文件对象
        if (fileList != null && fileList.size() > 0) {
            for (MultipartFile file : fileList) {
                if (ObjectConvertUtils.isEmpty(bizPath)) {
                    if (CommonConstant.UPLOAD_TYPE_OSS.equals(uploadType)) {
                        //未指定目录，则用阿里云默认目录 upload
                        bizPath = "upload";
                        //result.setMessage("使用阿里云文件上传时，必须添加目录！");
                        //result.setSuccess(false);
                        //return result;
                    } else {
                        bizPath = "";
                    }
                }
                if (CommonConstant.UPLOAD_TYPE_LOCAL.equals(uploadType)) {
                    //update-begin-author:lvdandan date:20200928 for:修改JEditor编辑器本地上传
                    savePath = this.uploadLocal(fileDir, file, bizPath);
                    fileName = file.getOriginalFilename();// 获取文件名
                    fileName = CommonUtils.getFileName(fileName);
                    fileSize = file.getSize();
                } else {
                    //update-begin-author:taoyan date:20200814 for:文件上传改造
                    savePath = CommonUtils.upload(file, bizPath, uploadType);
                    //update-end-author:taoyan date:20200814 for:文件上传改造
                }
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("fileName", fileName);
                map.put("filePath", savePath);
                map.put("fileDownFullUrl", "/sys/common/static/" + savePath);
                map.put("fileSize", fileSize);
                Float size = Float.parseFloat(String.valueOf(file.getSize())) / 1024;
                BigDecimal b = new BigDecimal(size);
                // 2表示2位 ROUND_HALF_UP表明四舍五入，
                Float fileSizeF = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                map.put("fileSizeKB", fileSizeF + "KB");
                mapList.add(map);

            }
        }
        if (mapList.size() > 0) {
            result.setResult(mapList);
            result.setSuccess(true);
        } else {
            result.setMessage("上传失败！");
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 本地文件上传
     *
     * @param fileDir 文件夹
     * @param mf      文件
     * @param bizPath 自定义路径
     * @return
     */
    private String uploadLocal(String fileDir, MultipartFile mf, String bizPath) {
        try {
            String ctxPath = uploadpath + (!ObjectConvertUtils.isEmpty(fileDir) ? ("/" + fileDir) : "");
            String fileName = UUID.randomUUID().toString().replace("-", "") + "_" + System.currentTimeMillis();
            File file = new File(ctxPath + File.separator + bizPath + File.separator);
            if (!file.exists()) {
                file.mkdirs();// 创建文件根目录
            }
            String orgName = mf.getOriginalFilename();// 获取文件名
            orgName = CommonUtils.getFileName(orgName);
            if (ObjectConvertUtils.isEmpty(orgName)) {
                return null;
            }
            String[] split = orgName.split("\\.");
            if (split != null && split.length > 1) {
                fileName += "." + split[split.length - 1];
            }
//            if (orgName.indexOf(".") != -1) {
//                fileName = orgName.substring(0, orgName.lastIndexOf(".")) + "_" + System.currentTimeMillis() + orgName.substring(orgName.indexOf("."));
//            } else {
//                fileName = orgName + "_" + System.currentTimeMillis();
//            }
            String savePath = file.getPath() + File.separator + fileName;
            File savefile = new File(savePath);
            if (!savefile.exists()) {
                savefile.createNewFile();
            }
            log.info("文件地址：" + savePath);
            FileCopyUtils.copy(mf.getBytes(), savefile);
            if (!savefile.exists()) {
                log.info("文件不存在！");
            }
            String dbpath = null;
            if (ObjectConvertUtils.isNotEmpty(bizPath)) {
                dbpath = bizPath + File.separator + fileName;
            } else {
                dbpath = fileName;
            }
            if (dbpath.contains("\\")) {
                dbpath = dbpath.replace("\\", "/");
            }
            dbpath = fileDir + "/" + dbpath;
            return dbpath;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return "";
    }


    /**
     * File 转 MultipartFile
     *
     * @param file
     * @throws Exception
     */
    public static MultipartFile fileToMultipartFile(File file) {
        FileInputStream input = null;
        MultipartFile multipartFile = null;
        try {
            input = new FileInputStream(file);
            multipartFile = new MockMultipartFile("file", file.getName(), ContentType.APPLICATION_OCTET_STREAM.toString(), IOUtils.toByteArray(input));
        } catch (Exception e) {
            log.error("FileUploadUtils." + Thread.currentThread().getStackTrace()[1].getMethodName() + " parse exception :", e);
        }
        return multipartFile;
    }
}
