package com.itheima.controller;

import cn.hutool.core.io.IoUtil;
import com.itheima.common.R;
import com.sun.deploy.net.HttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${food.path}")
    private  String basePath;
    @PostMapping ("/upload")
    public R<String> upload(MultipartFile file) throws IOException {
        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
        String originalFilename = file.getOriginalFilename();//abc.jpg
        int index = originalFilename.lastIndexOf(".");
        String substring = originalFilename.substring(index);//.jpg
        String fileName= UUID.randomUUID()+substring;//得到新的文件名
        File dir= new File(basePath);
        if(!dir.exists()){
            dir.mkdirs();
        }
        //将临时文件转存到指定的路径中
        file.transferTo(new File(basePath+fileName));

        return R.success(fileName);
    }
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        File file = new File(basePath+name);
        try {
            FileInputStream in =new FileInputStream(file);
            ServletOutputStream out = response.getOutputStream();
            IoUtil.copy(in,out);
            out.close();
        }  catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
