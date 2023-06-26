package org.scuvis.community.mq;

import com.alibaba.fastjson2.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.scuvis.community.entity.Event;
import org.scuvis.community.entity.Message;
import org.scuvis.community.service.MessageService;
import org.scuvis.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Xiyao Li
 * @date 2023/06/26 14:36
 */
@Component
public class EventConsumer implements CommunityConstant {
    @Autowired
    KafkaTemplate kafkaTemplate;
    @Autowired
    private MessageService messageService;

    @KafkaListener(topics = {"comment", "like", "follow"})
    public void handleEvent(ConsumerRecord record) {
        if (record == null || record.value() == null){
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null) return;
        Message message = new Message();
        message.setFromId(NOTICE_FROM_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(TOPIC_COMMENT);
        message.setCreateTime(new Date());

        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());

        if (!event.getData().isEmpty()) {
            // 把一个map里的东西全放到另一个，用putAll方法
            content.putAll(event.getData());
        }

        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }
}
