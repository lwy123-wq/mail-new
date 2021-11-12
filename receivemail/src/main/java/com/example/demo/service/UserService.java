package com.example.demo.service;

import com.example.demo.util.Receive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.*;
import java.util.Properties;

@Service
public class UserService {
    Folder folder;
    @Autowired
    private Receive receive;
    public Folder user() throws MessagingException {
        // 得到收件箱中的所有邮件,并解析
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
        folder.open(Folder.READ_WRITE);
        return folder;
    }
}
