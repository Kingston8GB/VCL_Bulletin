package org.scuvis.community.controller;

import org.scuvis.community.entity.Message;
import org.scuvis.community.entity.Page;
import org.scuvis.community.entity.User;
import org.scuvis.community.entity.vo.MessageVO;
import org.scuvis.community.service.MessageService;
import org.scuvis.community.service.UserService;
import org.scuvis.community.util.CommunityConstant;
import org.scuvis.community.util.CommunityUtil;
import org.scuvis.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author Xiyao Li
 * @date 2023/06/17 15:39
 */
@Controller
public class MessageController implements CommunityConstant {

    @Autowired
    MessageService messageService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    /**
     * 查询某用户的所有会话（私信）
     *
     * @param model
     * @param page
     * @return
     */
    @GetMapping("/letter/list")
    public String getLetterList(Model model, Page page) {
        User user = hostHolder.getUser();
        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));

        // 会话列表
        List<Message> conversationList = messageService.findConversations(
                user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversationList != null) {
            for (Message message : conversationList) {
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", message);
                map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                // 目标用户
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target", userService.findUserById(targetId));

                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);

        // 查询未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        int noticeUnreadCount = messageService.findNoticeUnreadCountByTopic(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "/site/letter";

    }

    @GetMapping("/letter/detail/{conversationId}")
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Model model, Page page) {
        User user = hostHolder.getUser();
        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        List<Message> lettersList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        // 进一步封装
        List<Map<String, Object>> letters = new ArrayList<>();
        if (lettersList != null) {
            for (Message message : lettersList) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);

        model.addAttribute("target", getLetterTarget(conversationId));

        List<Integer> ids = getLetterIds(lettersList);
        int affectedRows = messageService.readMessage(ids);

        return "/site/letter-detail";
    }

    public User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        if (hostHolder.getUser().getId() == id0) {
            return userService.findUserById(id1);
        } else {
            return userService.findUserById(id0);
        }
    }


    @PostMapping("/letter/send")
    @ResponseBody
    public String sendLetter(String toName, String content) {
        User target = userService.findUserByName(toName);
        int loginUserId = hostHolder.getUser().getId();
        if (target == null) {
            return CommunityUtil.getJSONString(1, "目标用户不存在！");
        }
        Message message = new Message();
        message.setFromId(loginUserId);
        message.setToId(target.getId());
        message.setContent(content);
        message.setCreateTime(new Date());

        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }


        messageService.addMessage(message);
        return CommunityUtil.getJSONString(0);
    }


    private List<Integer> getLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();

        if (letterList != null) {
            for (Message message : letterList) {
                if (hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }

        return ids;
    }

    @GetMapping("/notice/list")
    public String getNoticeList(Model model){

        User loginUser = hostHolder.getUser();
        if(loginUser == null){
            throw new IllegalArgumentException("当前用户未登录！");
        }

        // 查评论消息
        MessageVO latestComment = messageService.findLatestMessagesByTopic(loginUser.getId(), TOPIC_COMMENT);
        model.addAttribute("latestComment",latestComment);

        MessageVO latestLike = messageService.findLatestMessagesByTopic(loginUser.getId(), TOPIC_LIKE);
        model.addAttribute("latestLike",latestLike);

        MessageVO latestFollow = messageService.findLatestMessagesByTopic(loginUser.getId(), TOPIC_LIKE);
        model.addAttribute("latestFollow",latestFollow);

        int letterUnreadCount = messageService.findLetterUnreadCount(loginUser.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        // 所有主题算在一起
        int noticeUnreadCount = messageService.findNoticeUnreadCountByTopic(loginUser.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);
        return "/site/notice";
    }
}
