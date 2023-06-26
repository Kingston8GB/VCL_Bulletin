package org.scuvis.community.controller;

import com.google.code.kaptcha.Producer;
import org.scuvis.community.annotation.LoginRequired;
import org.scuvis.community.entity.Comment;
import org.scuvis.community.entity.DiscussPost;
import org.scuvis.community.entity.Event;
import org.scuvis.community.mq.EventProducer;
import org.scuvis.community.service.CommentService;
import org.scuvis.community.service.DiscussPostService;
import org.scuvis.community.util.CommunityConstant;
import org.scuvis.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

/**
 * @author Xiyao Li
 * @date 2023/06/17 03:09
 */
@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    CommentService commentService;
    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer producer;

    @Autowired
    DiscussPostService discussPostService;

    @PostMapping(path = "/add/{discussPostId}")
    @LoginRequired
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        int loginUserId = hostHolder.getUser().getId();
        comment.setUserId(loginUserId);
        comment.setStatus(0);
        comment.setCreateTime(new Date());

        commentService.addComment(comment);


        // 发送消息给kafka队列，插入系统通知
        // 先构造event
        Event event = new Event();
        event.setUserId(loginUserId)
             .setEntityType(comment.getEntityType())
             .setEntityId(comment.getEntityId())
             .putIntoData("postId",discussPostId)
             .setTopic("comment");

        if (comment.getEntityType() == 1) {
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        } else if (comment.getEntityType() == 2) {
            Comment target = commentService.findCommentByEntityId(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }

        producer.fireEvent(event);
        return "redirect:/discuss/detail/" + discussPostId;
    }
}
