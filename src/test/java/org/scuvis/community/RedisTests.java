package org.scuvis.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Xiyao Li
 * @date 2023/06/18 02:20
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTests {

    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void testString(){
        String key = "test:count";
        redisTemplate.opsForValue().set(key,1);

        System.out.println(redisTemplate.opsForValue().get(key));
    }
}
