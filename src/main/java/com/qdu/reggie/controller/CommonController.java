package com.qdu.reggie.controller;

import com.qdu.reggie.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        // file的文件存储在临时文件夹中，响应结束后就自动删除
        // 因此需要使用transfer方法存储到硬盘上
        // 设置文件名和存储目录 目录在yml文件中配置 文件名采用UUID

        String ext = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf("."));
        String fileName = UUID.randomUUID().toString() + ext;
        String fullPath = basePath + fileName;
        // 判断目录是否存在 不存在创建目录
        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        try {
            file.transferTo(new File(fullPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(fileName);
    }

    // 文件下载
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        String fullPath = basePath + name;
        // 创建文件输入流
        try {
            FileInputStream is = new FileInputStream(fullPath);
            OutputStream os = response.getOutputStream();
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = is.read(bytes)) != -1) {
                os.write(bytes, 0, len);
                os.flush();
            }
            is.close();
            os.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
