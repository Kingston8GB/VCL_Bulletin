package org.scuvis.community.mq;

import com.alibaba.fastjson2.JSONObject;
import org.scuvis.community.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author Xiyao Li
 * @date 2023/06/26 14:33
 */
@Component
public class EventProducer {
    @Autowired
    KafkaTemplate kafkaTemplate;

    public void fireEvent(Event event){
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
