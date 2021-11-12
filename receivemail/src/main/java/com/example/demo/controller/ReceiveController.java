package com.example.demo.controller;

import com.example.demo.service.ReceiveService;
import com.example.demo.util.Receive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.*;
import java.io.IOException;
import java.security.interfaces.RSAKey;
import java.util.ArrayList;
import java.util.Properties;

@Controller
public class ReceiveController {
    @Autowired
    private Receive receive;
    @Autowired
    private ReceiveService service;
    Folder folder;
    Message[] messages;
    @RequestMapping("/")
    public String sayHello(){
        return "receive";
    }
    @PostMapping(value = "/receive")
    @ResponseBody
    public ArrayList<Integer> send() throws Exception {
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
        /* Folder.READ_ONLY：只读权限
         * Folder.READ_WRITE：可读可写（可以修改邮件的状态）
         */
        folder.open(Folder.READ_WRITE); //打开收件箱
        ArrayList<Integer> list=new ArrayList<>();
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
    public String messages() throws MessagingException {
        // 得到收件箱中的所有邮件,并解析
        messages = folder.getMessages();
        if (messages == null || messages.length < 1)
            throw new MessagingException("未找到要解析的邮件!");
        return null;
    }
    @PostMapping(value = "/delete")
    @ResponseBody
    public void delete() throws IOException, MessagingException {
        service.deleteMessage(messages);

    }
}
