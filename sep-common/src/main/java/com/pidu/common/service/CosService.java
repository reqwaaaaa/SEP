package com.pidu.common.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.pidu.common.config.CosConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 腾讯云COS文件服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnBean(COSClient.class)
public class CosService {

    private final COSClient cosClient;
    private final CosConfig cosConfig;

    /**
     * 上传文件
     *
     * @param file     文件
     * @param folder   文件夹(如: course, video, image)
     * @return 文件访问URL
     */
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String ext = FileUtil.extName(originalFilename);
        
        // 生成文件路径: folder/yyyy/MM/dd/uuid.ext
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fileName = IdUtil.fastSimpleUUID() + "." + ext;
        String key = folder + "/" + datePath + "/" + fileName;

        try (InputStream inputStream = file.getInputStream()) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            PutObjectRequest putRequest = new PutObjectRequest(
                    cosConfig.getBucketName(), key, inputStream, metadata);
            PutObjectResult result = cosClient.putObject(putRequest);
            
            log.info("文件上传成功: {}, ETag: {}", key, result.getETag());
            
            // 返回访问URL
            return getFileUrl(key);
        }
    }

    /**
     * 上传视频文件
     */
    public String uploadVideo(MultipartFile file) throws IOException {
        return uploadFile(file, "video");
    }

    /**
     * 上传课件文件
     */
    public String uploadCourseware(MultipartFile file) throws IOException {
        return uploadFile(file, "courseware");
    }

    /**
     * 上传图片文件
     */
    public String uploadImage(MultipartFile file) throws IOException {
        return uploadFile(file, "image");
    }

    /**
     * 删除文件
     */
    public void deleteFile(String key) {
        try {
            cosClient.deleteObject(cosConfig.getBucketName(), key);
            log.info("文件删除成功: {}", key);
        } catch (Exception e) {
            log.error("文件删除失败: {}", key, e);
        }
    }

    /**
     * 获取文件访问URL
     */
    public String getFileUrl(String key) {
        if (cosConfig.getDomain() != null && !cosConfig.getDomain().isEmpty()) {
            return cosConfig.getDomain() + "/" + key;
        }
        return String.format("https://%s.cos.%s.myqcloud.com/%s",
                cosConfig.getBucketName(), cosConfig.getRegion(), key);
    }

    /**
     * 从URL中提取key
     */
    public String extractKeyFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        // 移除域名部分
        if (url.contains(".myqcloud.com/")) {
            return url.substring(url.indexOf(".myqcloud.com/") + 14);
        }
        if (cosConfig.getDomain() != null && url.startsWith(cosConfig.getDomain())) {
            return url.substring(cosConfig.getDomain().length() + 1);
        }
        return url;
    }
}
