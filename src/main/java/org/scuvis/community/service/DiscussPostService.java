package org.scuvis.community.service;

import org.apache.ibatis.annotations.Param;
import org.scuvis.community.dao.CommentMapper;
import org.scuvis.community.dao.DiscussPostMapper;
import org.scuvis.community.dao.UserMapper;
import org.scuvis.community.entity.Comment;
import org.scuvis.community.entity.DiscussPost;
import org.scuvis.community.entity.User;
import org.scuvis.community.entity.vo.CommentVO;
import org.scuvis.community.entity.vo.ReplyVO;
import org.scuvis.community.util.HostHolder;
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
 * @date 2023/06/05 16:31
 */
@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    // @Autowired
    // private UserMapper userMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    public int findDiscussPostCountByUserId(int userId) {
        return discussPostMapper.selectDiscussPostRowsByUserId(userId);
    }

    public int addDiscussPost(DiscussPost discussPost) {
        if (discussPost == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));

        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));

        return discussPostMapper.insertDiscussPost(discussPost);
    }

    public DiscussPost findDiscussPostById(int discussPostId) {
        return discussPostMapper.selectDiscussPostById(discussPostId);
    }

    public List<CommentVO> findCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        List<CommentVO> commentVOList = new ArrayList<>();
        List<Comment> comments = commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
        if (comments != null) {
            for (Comment comment : comments) {
                CommentVO commentVO = new CommentVO();
                // Map<String, Object> commentVO = new HashMap<>();
                commentVO.setComment(comment);
                User userOfComment = userMapper.selectById(comment.getUserId());
                commentVO.setUserOfComment(userOfComment);

                List<ReplyVO> replyVOList = new ArrayList<>();
                List<Comment> replys = commentMapper.selectCommentsByEntity(2, comment.getId(), 0, Integer.MAX_VALUE);
                if (replys != null) {
                    for (Comment reply : replys) {
                        ReplyVO replyVO = new ReplyVO();
                        replyVO.setReply(reply);
                        User userOfReply = userMapper.selectById(reply.getUserId());
                        replyVO.setUserOfReply(userOfReply);
                        User targetUser = reply.getTargetId() == 0 ? null : userMapper.selectById(reply.getTargetId());
                        replyVO.setTarget(targetUser);

                        int replyLikeCount = Math.toIntExact(likeService.findEntityLikeCount(2, reply.getId()));
                        replyVO.setReplyLikeCount(replyLikeCount);
                        replyVO.setReplyLikeStatus(hostHolder.getUser() == null ? 0 : likeService.findEntityLikeStatus(hostHolder.getUser().getId(), 2, reply.getId()));
                        replyVOList.add(replyVO);

                    }
                }
                commentVO.setReplyVOList(replyVOList);
                int commentLikeCount = Math.toIntExact(likeService.findEntityLikeCount(2, comment.getId()));

                commentVO.setCommentLikeCount(commentLikeCount);
                commentVO.setCommentLikeStatus(hostHolder.getUser() == null ? 0 : likeService.findEntityLikeStatus(hostHolder.getUser().getId(), 2, comment.getId()));


                commentVO.setReplyCount(commentMapper.selectCountByEntity(2, comment.getId()));
                commentVOList.add(commentVO);
            }
        }
        return commentVOList;
    }

    public int updateCommentCount(int commentCount, int id) {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }

    public int updateTypeById(int id, int type){
        return discussPostMapper.updateTypeById(id, type);
    }

    public int updateStatusById(int id, int status){
        return discussPostMapper.updateStatusById(id, status);
    }
}
