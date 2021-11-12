package com.example.demo.controller;

import com.example.demo.service.ReceiveService;
import com.example.demo.service.UserService;
import com.example.demo.util.Receive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

@Controller
public class ReceiveController {
    @Autowired
    private ReceiveService service;
    @Autowired
    private UserService userService;
    static Folder folder;
    static Message[] messages;
    static MimeMessage msg;
    @RequestMapping("/")
    public String sayHello(){
        return "receive";
    }
    @PostMapping(value = "/receive")
    @ResponseBody
    public ArrayList<Integer> send() throws Exception {
        ArrayList<Integer> list=new ArrayList<>();
        folder=userService.user();
        //未读邮件数
        int f1=folder.getUnreadMessageCount();
        System.out.println(f1);
        list.add(f1);
        //删除邮件数
        int f2=folder.getDeletedMessageCount();
        list.add(f2);
        //新邮件
        int f3=folder.getNewMessageCount();
        list.add(f3);
        //邮件总数
        int f4=folder.getMessageCount();
        list.add(f4);

        return list;

    }
    @PostMapping(value = "/messages")
    @ResponseBody
    public ArrayList<String> messages() throws MessagingException, IOException {
        ArrayList<String> list=new ArrayList<>();
        /*// 得到收件箱中的所有邮件,并解析
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "pop3");    // 使用pop3协议
        props.setProperty("mail.pop3.port", receive.getPort());           // 端口
        props.setProperty("mail.pop3.host", receive.getServicePath());    // pop3服务器
        // 创建Session实例对象
        Session session = Session.getInstance(props);
        Store store = session.getStore("pop3");
        System.out.println(receive.getUsername());
        store.connect(receive.getUsername(), receive.getPassword()); //163邮箱程序登录属于第三方登录所以这里的密码是163给的授权密码而并非普通的登录密码
        // 获得收件箱
        folder = store.getFolder("INBOX");
        *//* Folder.READ_ONLY：只读权限
         * Folder.READ_WRITE：可读可写（可以修改邮件的状态）
         *//*
        folder.open(Folder.READ_WRITE);*/
        folder=userService.user();
        messages = folder.getMessages();
        if (messages == null || messages.length < 1)
            throw new MessagingException("未找到要解析的邮件!");
        for (int i = 0, count = messages.length; i < count; i++) {
            msg = (MimeMessage) messages[i];
            String subject=service.getSubject(msg); //主题
            String from=service.getFrom(msg);       //发件人
            String receive=service.getReceiveAddress(msg, null);   //收件人
            String time=service.getSentDate(msg, null);   //发送时间
            String size=msg.getSize() * 1024 + "kb";
            StringBuffer content = new StringBuffer(30);
            service.getMailTextContent(msg, content);
            String text=(content.length() > 100 ? content.substring(0,100) + "..." : String.valueOf(content)); //邮件正文
            list.add(subject);
            list.add(from);
            list.add(receive);
            list.add(time);
            list.add(size);
            list.add(text);
        }
        return list;
    }
    @PostMapping(value = "/delete")
    @ResponseBody
    public void delete() throws IOException, MessagingException {
        folder=userService.user();
        messages = folder.getMessages();
        System.out.println("dddddddddddddddddddddddddddddddddddddddllllllllllllllllllllll");
        service.deleteMessage(messages);
        //释放资源
        folder.close(true);
        userService.getStore().close();

    }
    @PostMapping(value = "/download")
    @ResponseBody
    public void Download() throws IOException, MessagingException {
        boolean isContainerAttachment = service.isContainAttachment(msg);
        int i=0;
        if(isContainerAttachment){
            service.saveAttachment(msg, "/home/lwy/文档/aaa/文件/"+msg.getSubject()+i++);
        }
    }

}
