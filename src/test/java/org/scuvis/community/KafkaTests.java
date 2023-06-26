package org.scuvis.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Xiyao Li
 * @date 2023/06/18 02:20
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
public class KafkaTests {

    @Autowired
    Producer producer;

    @Autowired
    Comsumer consumer;

    @Test
    public void test(){
        producer.send("test","111");
        producer.send("test","222");

    }
}
@Component
class Producer{
    @Autowired
    KafkaTemplate kafkaTemplate;

    public void send(String topic, String data){
        kafkaTemplate.send(topic,data);
    }
}
@Component
class Comsumer{
    @Autowired
    KafkaTemplate kafkaTemplate;

    @KafkaListener(topics = {"test"})
    public void handle(ConsumerRecord record){
        System.out.println(record.value());
    }
}
