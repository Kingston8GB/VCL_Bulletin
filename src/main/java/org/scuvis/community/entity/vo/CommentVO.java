package org.scuvis.community.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scuvis.community.entity.Comment;
import org.scuvis.community.entity.User;

import java.util.List;

/**
 * @author Xiyao Li
 * @date 2023/06/16 23:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentVO {
    private Comment comment;

    private User userOfComment;

    private List<ReplyVO> replyVOList;

    private int replyCount;
}
