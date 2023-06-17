package org.scuvis.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.scuvis.community.util.MailClient;
import org.scuvis.community.util.SensitiveFilter;
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
public class SensitiveTests {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter(){
        String text = "我的梦想是去嫖￥娼！";
        String result = sensitiveFilter.filter(text);

        System.out.println(result);
    }
}
