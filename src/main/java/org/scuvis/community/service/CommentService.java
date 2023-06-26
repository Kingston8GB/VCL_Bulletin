package org.scuvis.community.service;

import org.scuvis.community.dao.CommentMapper;
import org.scuvis.community.dao.DiscussPostMapper;
import org.scuvis.community.entity.Comment;
import org.scuvis.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author Xiyao Li
 * @date 2023/06/17 02:54
 */
@Service
public class CommentService {
    @Autowired
    CommentMapper commentMapper;

    @Autowired
    DiscussPostMapper discussPostMapper;

    @Autowired
    SensitiveFilter sensitiveFilter;

    @Transactional
    public int addComment(Comment comment){
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        // 第一件事：往comment表里插入一条记录
        int affectedRows1 = commentMapper.insertComment(comment);
        // 第二件事：更新帖子对应的评论数
        if (comment.getEntityType() == 1) {
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostMapper.updateCommentCount(comment.getEntityId(), count);
        }
        return affectedRows1;


    }

    public Comment findCommentByEntityId(int entityId){
        List<Comment> comments = commentMapper.selectCommentsByEntityId(entityId);
        return comments != null ? comments.get(0) : null;
    }

}
