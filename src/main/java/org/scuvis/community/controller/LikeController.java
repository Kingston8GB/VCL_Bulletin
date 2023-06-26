package org.scuvis.community.controller;

import org.scuvis.community.entity.Event;
import org.scuvis.community.mq.EventProducer;
import org.scuvis.community.service.LikeService;
import org.scuvis.community.util.CommunityConstant;
import org.scuvis.community.util.CommunityUtil;
import org.scuvis.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Xiyao Li
 * @date 2023/06/19 15:47
 */
@Controller
public class LikeController implements CommunityConstant {
    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer producer;

    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId) {
        int loginUserId = hostHolder.getUser().getId();

        likeService.like(loginUserId,entityType,entityId,entityUserId);

        Map<String, Object> map = new HashMap<>();

        Long entityLikeCount = likeService.findEntityLikeCount(entityType, entityId);
        int entityLikeStatus = likeService.findEntityLikeStatus(loginUserId, entityType, entityId);

        map.put("likeCount",entityLikeCount);
        map.put("likeStatus",entityLikeStatus);


        if (entityLikeStatus == 1) {
            Event event = new Event();
            event.setUserId(loginUserId)
                    .setEntityType(entityType)
                    .setEntityId(entityId).setEntityUserId(entityUserId)
                    // .putIntoData("postId",discussPostId)
                    .setTopic(TOPIC_LIKE).putIntoData("postId",postId);

            producer.fireEvent(event);
        }


        return CommunityUtil.getJSONString(0,"成功",map);
    }
}
