package com.example.sendmail.controller;


import com.example.sendmail.service.IEmailService;
import com.example.sendmail.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private IEmailService emailService;
    @RequestMapping("/")
    public String sayHello(){
        return "send";
    }

    @RequestMapping("sendEmail")
    @ResponseBody
    public boolean sendEmail(@RequestParam("file") MultipartFile[] file, String to, String subject, String contentText){
        if(file.length>0) {
            List list = new ArrayList();
        for(int i=0;file.length>i;i++) {
            // 获取文件名称,包含后缀
            String fileName = file[i].getOriginalFilename();
            // 存放在这个路径下：该路径是该工程目录下的static文件下：(注：该文件可能需要自己创建)
            // 放在static下的原因是，存放的是静态文件资源，即通过浏览器输入本地服务器地址，加文件名时是可以访问到的
            String path = "/home/lwy/文档/lwy";
            try {
                // 该方法是对文件写入的封装，在util类中，导入该包即可使用
                long t = System.currentTimeMillis();// 获得当前系统毫秒数,
                fileName = t + fileName;
                FileUtil.fileupload(file[i].getBytes(), path, fileName);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            list.add(path + fileName);
        }
            return  emailService.sendAttachmentMail(to,subject,contentText,list);
        }
        return  emailService.sendAttachmentMail(to,subject,contentText);
    }



    @GetMapping(value = "/login")
    public String login(){
        return "login" ;
    }
    /**
     * 上传文件
     * @param
     * @return
     */
    @RequestMapping("addFile")
    @ResponseBody
    public String addItem(@RequestParam("file") MultipartFile file){
        if(!file.isEmpty()) {
            // 获取文件名称,包含后缀
            String fileName = file.getOriginalFilename();
            String path = "/home/lwy/文档/lwy";
//			String path = ClassUtils.getDefaultClassLoader().getResource("").getPath()
            try {
                // 该方法是对文件写入的封装，在util类中，导入该包即可使用
                long t = System.currentTimeMillis();// 获得当前系统毫秒数,
                fileName=t+fileName;
                FileUtil.fileupload(file.getBytes(), path, fileName);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // 接着创建对应的实体类，将以下路径进行添加，然后通过数据库操作方法写入
            System.out.println(path+fileName);

            Timestamp timestamp=new Timestamp(System.currentTimeMillis());
            fileName="file/"+fileName;

        }
        return "1";
    }
}
