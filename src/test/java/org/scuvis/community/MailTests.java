package org.scuvis.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.scuvis.community.util.MailClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @author Xiyao Li
 * @date 2023/06/07 16:07
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testTextEmail(){
        mailClient.sendMail("1184142232@qq.com","test","welcome");
    }

    @Test
    public void testHTMLEmail(){
        Context context = new Context();
        context.setVariable("username","lixiyao");

        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);

        mailClient.sendMail("1184142232@qq.com","html",content);
    }
}
