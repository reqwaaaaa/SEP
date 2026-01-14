package com.pidu.system.controller;

import com.pidu.auth.annotation.RequireLogin;
import com.pidu.common.config.CosConfig;
import com.pidu.common.result.Result;
import com.pidu.common.service.CosService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传下载控制器
 */
@Slf4j
@Api(tags = "文件管理")
@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${file.upload-path:./uploads}")
    private String uploadPath;

    @Value("${file.access-url:http://localhost:8081/file/download}")
    private String accessUrl;

    @Autowired(required = false)
    private CosService cosService;

    @Autowired
    private CosConfig cosConfig;

    @ApiOperation("上传文件")
    @PostMapping("/upload")
    @RequireLogin
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file,
                                               @RequestParam(value = "type", defaultValue = "file") String type) {
        if (file.isEmpty()) {
            return Result.fail("请选择文件");
        }
        
        try {
            // 如果启用了COS，优先使用COS上传
            if (cosConfig.isEnabled() && cosService != null) {
                String url = cosService.uploadFile(file, type);
                Map<String, String> result = new HashMap<>();
                result.put("filename", file.getOriginalFilename());
                result.put("url", url);
                result.put("path", url);
                result.put("size", String.valueOf(file.getSize()));
                result.put("storage", "cos");
                return Result.success(result);
            }
            
            // 本地存储
            String originalFilename = file.getOriginalFilename();
            String ext = originalFilename != null && originalFilename.contains(".") ? 
                    originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String newFilename = UUID.randomUUID().toString().replace("-", "") + ext;
            
            // 按日期分目录
            String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            Path dirPath = Paths.get(uploadPath, type, dateDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            
            // 保存文件
            Path filePath = dirPath.resolve(newFilename);
            file.transferTo(filePath.toFile());
            
            // 返回文件信息
            Map<String, String> result = new HashMap<>();
            result.put("filename", originalFilename);
            result.put("url", accessUrl + "/" + type + "/" + dateDir + "/" + newFilename);
            result.put("path", type + "/" + dateDir + "/" + newFilename);
            result.put("size", String.valueOf(file.getSize()));
            result.put("storage", "local");
            
            log.info("文件上传成功: {}", filePath);
            return Result.success(result);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.fail("文件上传失败: " + e.getMessage());
        }
    }

    @ApiOperation("上传视频(课程)")
    @PostMapping("/upload/video")
    @RequireLogin
    public Result<Map<String, String>> uploadVideo(@RequestParam("file") MultipartFile file) {
        return upload(file, "video");
    }

    @ApiOperation("上传课件")
    @PostMapping("/upload/courseware")
    @RequireLogin
    public Result<Map<String, String>> uploadCourseware(@RequestParam("file") MultipartFile file) {
        return upload(file, "courseware");
    }

    @ApiOperation("上传图片")
    @PostMapping("/upload/image")
    @RequireLogin
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        return upload(file, "image");
    }

    @ApiOperation("下载文件")
    @GetMapping("/download/**")
    public ResponseEntity<Resource> download(@RequestParam(required = false) String path,
                                             javax.servlet.http.HttpServletRequest request) {
        try {
            // 从URL路径中获取文件路径
            String filePath = path;
            if (filePath == null || filePath.isEmpty()) {
                String requestUri = request.getRequestURI();
                String prefix = "/file/download/";
                int idx = requestUri.indexOf(prefix);
                if (idx >= 0) {
                    filePath = requestUri.substring(idx + prefix.length());
                }
            }
            
            if (filePath == null || filePath.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Path file = Paths.get(uploadPath, filePath);
            if (!Files.exists(file)) {
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new FileSystemResource(file);
            String filename = file.getFileName().toString();
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString())
                    .replace("+", "%20");
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + encodedFilename + "\"; filename*=UTF-8''" + encodedFilename)
                    .body(resource);
        } catch (Exception e) {
            log.error("文件下载失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @ApiOperation("预览文件")
    @GetMapping("/preview/**")
    public ResponseEntity<Resource> preview(javax.servlet.http.HttpServletRequest request) {
        try {
            String requestUri = request.getRequestURI();
            String prefix = "/file/preview/";
            int idx = requestUri.indexOf(prefix);
            if (idx < 0) {
                return ResponseEntity.notFound().build();
            }
            String filePath = requestUri.substring(idx + prefix.length());
            
            Path file = Paths.get(uploadPath, filePath);
            if (!Files.exists(file)) {
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new FileSystemResource(file);
            String contentType = Files.probeContentType(file);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (Exception e) {
            log.error("文件预览失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
