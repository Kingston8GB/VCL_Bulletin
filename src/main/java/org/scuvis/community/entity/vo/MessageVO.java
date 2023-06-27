package org.scuvis.community.entity.vo;

import lombok.Data;
import org.scuvis.community.entity.Message;
import org.scuvis.community.entity.User;

/**
 * @author Xiyao Li
 * @date 2023/06/26 21:44
 */
@Data
public class MessageVO {
    Message latestMessage;

    User userOfMessage;
    int entityTypeOfMessage;
    int entityIdOfMessage;

    int postIdOfMessage;

    int count;
    int unreadCount;

    User fromUserOfMessage;
}
