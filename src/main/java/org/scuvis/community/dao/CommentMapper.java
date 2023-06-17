package org.scuvis.community.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scuvis.community.entity.Comment;

import java.util.List;

/**
 * @author Xiyao Li
 * @date 2023/06/16 23:09
 */
@Mapper
public interface CommentMapper {
    List<Comment> selectCommentsByEntity(@Param("entityType") int entityType,
                                         @Param("entityId") int entityId,
                                         @Param("offset") int offset, @Param("limit") int limit);

    int selectCountByEntity(@Param("entityType") int entityType,
                            @Param("entityId") int entityId);


    int insertComment(Comment comment);
}
