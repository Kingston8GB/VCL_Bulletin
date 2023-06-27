package org.scuvis.community.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scuvis.community.entity.Comment;
import org.scuvis.community.entity.Message;

import java.util.List;

/**
 * @author Xiyao Li
 * @date 2023/06/16 23:09
 */
@Mapper
public interface MessageMapper {
    List<Message> selectConversations(@Param("userId") int userId,
                                      @Param("offset") int offset, @Param("limit") int limit);

    int selectConversationCount(int userId);

    List<Message> selectLetters(String conversationId, int offset, int limit);

    int selectLetterCount(String conversationId);

    int selectLetterUnreadCount(@Param("userId") int userId, @Param("conversationId") String conversationId);

    int insertMessage(Message message);

    int updateMessageStatus(@Param("ids") List<Integer> ids, @Param("status") int status);

    Message selectLatestMessageByTopic(@Param("userId") int userId, @Param("topic") String topic);

    int selectNoticeCountByTopic(@Param("userId") int userId, @Param("topic") String topic);

    int selectNoticeUnreadCountByTopic(@Param("userId") int userId, @Param("topic") String topic);

    List<Message> selectNoticesByTopic(@Param("userId") int userId, @Param("topic") String topic, @Param("offset") int offset, @Param("limit") int limit);
}
