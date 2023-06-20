package org.scuvis.community.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scuvis.community.entity.Comment;
import org.scuvis.community.entity.User;

import java.util.List;

/**
 * @author Xiyao Li
 * @date 2023/06/16 23:40
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReplyVO {
    private Comment reply;

    private User userOfReply;

    private User target;

    private int replyLikeCount;

    private int replyLikeStatus;
}
