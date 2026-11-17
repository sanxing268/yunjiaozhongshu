package com.subject.service;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class CosService {

    private final COSClient cosClient;

    @Value("${cos.bucketName}")
    private String bucketName;

    @Value("${cos.baseUrl}")
    private String baseUrl;

    public CosService(COSClient cosClient) {
        this.cosClient = cosClient;
    }

    /**
     * 上传文件到腾讯云 COS，返回可直接访问的完整 URL
     */
    public String uploadFile(MultipartFile file, String directory) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            originalFilename = "file_" + System.currentTimeMillis();
        }
        
        // 使用原始文件名，但处理特殊字符和冲突
        String key = buildKeyWithOriginalName(directory, originalFilename);
        
        System.out.println("开始上传文件到COS:");
        System.out.println("  - 原始文件名: " + originalFilename);
        System.out.println("  - 存储路径: " + key);
        System.out.println("  - 文件大小: " + file.getSize() + " bytes");
        System.out.println("  - Bucket: " + bucketName);

        try (InputStream inputStream = file.getInputStream()) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            String contentType = file.getContentType();
            if (contentType != null) {
                metadata.setContentType(contentType);
            }
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream, metadata);
            cosClient.putObject(putObjectRequest);
            System.out.println("COS上传成功");
        } catch (Exception e) {
            System.err.println("COS上传失败: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("上传到COS失败: " + e.getMessage(), e);
        }

        // 返回完整的 HTTPS 访问地址，例如：
        // https://staroy-1349382502.cos.ap-guangzhou.myqcloud.com/{key}
        String url = baseUrl.endsWith("/") ? baseUrl + key : baseUrl + "/" + key;
        System.out.println("返回URL: " + url);
        return url;
    }

    /**
     * 从完整的COS URL中提取文件key
     * 例如：https://staroy-1349382502.cos.ap-guangzhou.myqcloud.com/images/projectView/xxx.jpg
     * 提取：images/projectView/xxx.jpg
     */
    private String extractKeyFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        // 移除baseUrl前缀，获取key
        if (url.startsWith(baseUrl)) {
            String key = url.substring(baseUrl.length());
            // 移除开头的斜杠
            if (key.startsWith("/")) {
                key = key.substring(1);
            }
            return key;
        }
        // 如果URL不包含baseUrl，尝试直接提取路径部分
        // 例如：images/projectView/xxx.jpg 或 /images/projectView/xxx.jpg
        String key = url;
        if (key.startsWith("/")) {
            key = key.substring(1);
        }
        return key;
    }

    /**
     * 删除COS中的文件
     * @param fileUrl 文件的完整URL或key
     * @return 是否删除成功
     */
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            System.out.println("删除文件失败：URL为空");
            return false;
        }
        
        try {
            String key = extractKeyFromUrl(fileUrl);
            if (key == null || key.isEmpty()) {
                System.out.println("删除文件失败：无法从URL提取key: " + fileUrl);
                return false;
            }
            
            System.out.println("开始删除COS文件:");
            System.out.println("  - 原始URL: " + fileUrl);
            System.out.println("  - 提取的key: " + key);
            System.out.println("  - Bucket: " + bucketName);
            
            cosClient.deleteObject(bucketName, key);
            System.out.println("COS文件删除成功: " + key);
            return true;
        } catch (Exception e) {
            System.err.println("删除COS文件失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 使用原始文件名构建存储key，保留原始文件名
     * 为了避免文件名冲突，在文件名前添加时间戳
     * 同时处理特殊字符，确保文件名安全
     */
    private String buildKeyWithOriginalName(String directory, String originalFilename) {
        String prefix = (directory == null || directory.isEmpty()) ? "" : directory.trim();
        if (!prefix.isEmpty() && !prefix.endsWith("/")) {
            prefix = prefix + "/";
        }
        
        // 处理文件名：移除或替换特殊字符，避免路径问题
        String safeFilename = originalFilename;
        // 替换可能造成问题的字符
        safeFilename = safeFilename.replaceAll("[\\\\/:*?\"<>|]", "_");
        // 移除前后空格
        safeFilename = safeFilename.trim();
        
        // 如果文件名包含中文或特殊字符，进行URL编码处理
        // 但为了保持可读性，先尝试直接使用，如果COS不支持再编码
        
        // 添加时间戳前缀避免文件名冲突，格式：时间戳_原文件名
        // 例如：20231218123456_我的文档.docx
        String timestamp = String.valueOf(System.currentTimeMillis());
        String finalFilename = timestamp + "_" + safeFilename;
        
        return prefix + finalFilename;
    }
    
    /**
     * 旧方法：使用UUID生成文件名（保留用于向后兼容）
     */
    private String buildKey(String directory, String ext) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String prefix = (directory == null || directory.isEmpty()) ? "" : directory.trim();
        if (!prefix.isEmpty() && !prefix.endsWith("/")) {
            prefix = prefix + "/";
        }
        return prefix + uuid + ext;
    }
}


