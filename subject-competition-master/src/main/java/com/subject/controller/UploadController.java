package com.subject.controller;

import com.subject.commons.result.Result;
import com.subject.service.CosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class UploadController {
    @Autowired
    private CosService cosService;
    @PostMapping("upload")
    public String uploadToCos(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("上传文件为空");
            }
            // 上传到 COS，目录使用 files/conclusion 上传结题附件
            String url = cosService.uploadFile(file, "files/conclusion");
            System.out.println("文件上传成功，URL: " + url);
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    // 轮播图专用上传接口（单独目录 files/carousel）
    @PostMapping("upload/carousel")
    public String uploadCarouselToCos(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("上传文件为空");
            }
            // 轮播图专用目录：files/carousel
            String url = cosService.uploadFile(file, "image");
            System.out.println("轮播图上传成功，URL: " + url);
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("轮播图上传失败: " + e.getMessage());
        }
    }
    /**
     * 删除COS中的文件
     * @param param 包含fileUrl的请求体
     * @return 删除结果
     */
    @PostMapping("file/delete")
    public Result deleteFile(@RequestBody Map<String, String> param) {
        try {
            String fileUrl = param.get("fileUrl");
            if (fileUrl == null || fileUrl.isEmpty()) {
                return Result.error().message("文件URL不能为空");
            }
            
            boolean deleted = cosService.deleteFile(fileUrl);
            if (deleted) {
                return Result.ok().message("文件删除成功");
            } else {
                return Result.error().message("文件删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error().message("删除文件时发生错误: " + e.getMessage());
        }
    }

    /**
     * 现在文件已经上传到 COS，前端可以直接使用保存的 URL 访问 / 下载，
     * 因此该接口可以逐步废弃。
     */
    @PostMapping("download")
    public void download(HttpServletResponse response, @RequestBody Map<String, Object> param) throws Exception {
        // 为了兼容旧数据，这里保留空实现或后续根据需要从 COS 代理下载。
        response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
    }
}
