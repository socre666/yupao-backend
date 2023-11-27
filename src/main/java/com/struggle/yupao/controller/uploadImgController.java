package com.struggle.yupao.controller;


import com.struggle.yupao.utils.QiniuOssUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Controller
@RequestMapping("upload")
@CrossOrigin(origins = {"http://localhost:3000"}, allowCredentials = "true")
public class uploadImgController {
    @PostMapping("/img")
    @ResponseBody
    public void up1img(@RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return;
        }
        String fileName = file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();
        QiniuOssUtils utils = new QiniuOssUtils();
        String upload = utils.upload(inputStream, fileName);
        System.out.println(upload);
    }
}
