package org.scuvis.community.service;

import com.alibaba.fastjson.JSONObject;
import org.scuvis.community.dao.MessageMapper;
import org.scuvis.community.dao.UserMapper;
import org.scuvis.community.entity.Message;
import org.scuvis.community.entity.vo.MessageVO;
import org.scuvis.community.entity.vo.NoticeDetailVO;
import org.scuvis.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Xiyao Li
 * @date 2023/06/17 15:36
 */
@Service
public class MessageService {
    @Autowired
    MessageMapper messageMapper;

    @Autowired
    SensitiveFilter sensitiveFilter;

    @Autowired
    UserMapper userMapper;

    public List<Message> findConversations(int userId, int offset, int limit){
        return messageMapper.selectConversations(userId, offset, limit);
    }

    public int findConversationCount(int userId){
        return messageMapper.selectConversationCount(userId);
    }

    public List<Message> findLetters(String conversationId, int offset, int limit){
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    public int findLetterCount(String conversationId){
        return messageMapper.selectLetterCount(conversationId);
    }

    public int findLetterUnreadCount(int userId,String conversationId){
        return messageMapper.selectLetterUnreadCount(userId,conversationId);
    }

    public int addMessage(Message message){
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }

    public int readMessage(List<Integer> ids){
        if(ids == null || ids.isEmpty()){
            return 0;
        }
        return messageMapper.updateMessageStatus(ids,1);

    }

    public MessageVO findLatestMessagesByTopic(int userId, String topic){
        Message latestMessage = messageMapper.selectLatestMessageByTopic(userId, topic);
        MessageVO messageVO = new MessageVO();
        if(latestMessage != null){
            messageVO.setLatestMessage(latestMessage);

            String content = HtmlUtils.htmlUnescape(messageVO.getLatestMessage().getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
            messageVO.setUserOfMessage(userMapper.selectById(userId));
            messageVO.setEntityIdOfMessage((Integer) data.get("entityId"));
            messageVO.setEntityTypeOfMessage((Integer) data.get("entityType"));
            if(!"follow".equals(topic)){
                messageVO.setPostIdOfMessage((Integer) data.get("postId"));
            }

            messageVO.setCount(messageMapper.selectNoticeCountByTopic(userId,topic));
            messageVO.setUnreadCount(messageMapper.selectNoticeUnreadCountByTopic(userId, topic));

        }
        return messageVO;
    }

    public int findNoticeCountByTopic(int userId, String topic){
        return messageMapper.selectNoticeCountByTopic(userId, topic);
    }
    public int findNoticeUnreadCountByTopic(int userId, String topic){
        return messageMapper.selectNoticeUnreadCountByTopic(userId, topic);
    }

    public List<NoticeDetailVO> findNoticesByTopic(int userId, String topic, int offset, int limit){
        List<Message> noticeList = messageMapper.selectNoticesByTopic(userId, topic, offset, limit);
        List<NoticeDetailVO> noticeDetailVOList = new ArrayList();
        if(noticeList != null){
            for (Message notice : noticeList) {
                NoticeDetailVO noticeDetailVO = new NoticeDetailVO();
                noticeDetailVO.setNotice(notice);
                noticeDetailVO.setUserOfNotice(userMapper.selectById(userId));

                String content = notice.getContent();
                String contentUnescaped = HtmlUtils.htmlUnescape(content);
                Map map = com.alibaba.fastjson2.JSONObject.parseObject(contentUnescaped, HashMap.class);
                noticeDetailVO.setEntityIdOfNotice((Integer) map.get("entityId"));
                noticeDetailVO.setEntityTypeOfNotice((Integer) map.get("entityType"));
                if(!"follow".equals(topic)){

                    noticeDetailVO.setPostIdOfNotice((Integer) map.get("postId"));
                }


                noticeDetailVO.setFromUserOfNotice(userMapper.selectById(notice.getFromId()));
                noticeDetailVO.setUserOfNotice(userMapper.selectById((Integer) map.get("userId")));

                noticeDetailVOList.add(noticeDetailVO);
            }


            List<Integer> ids = getLetterIds(noticeList,userId);
            if (!ids.isEmpty()) {
                messageMapper.updateMessageStatus(ids,1);
            }
        }
        return noticeDetailVOList;
    }

    private List<Integer> getLetterIds(List<Message> letterList,int loginUserId) {
        List<Integer> ids = new ArrayList<>();

        if (letterList != null) {
            for (Message message : letterList) {
                if (loginUserId == message.getToId() && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }

        return ids;
    }
}
