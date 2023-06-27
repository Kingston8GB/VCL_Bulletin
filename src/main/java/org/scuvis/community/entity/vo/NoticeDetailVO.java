package org.scuvis.community.entity.vo;

import lombok.Data;
import org.scuvis.community.entity.Message;
import org.scuvis.community.entity.User;

/**
 * @author Xiyao Li
 * @date 2023/06/26 21:44
 */
@Data
public class NoticeDetailVO {
    Message notice;

    User userOfNotice;
    int entityTypeOfNotice;
    int entityIdOfNotice;

    int postIdOfNotice;

    int count;
    int unreadCount;

    User fromUserOfNotice;
}
