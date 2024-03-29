package org.scuvis.community.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scuvis.community.entity.DiscussPost;

import java.util.List;

/**
 * @author Xiyao Li
 * @date 2023/06/05 16:11
 */
@Mapper
public interface DiscussPostMapper {
    List<DiscussPost> selectDiscussPosts(@Param("userId") int userId,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);

    int selectDiscussPostRowsByUserId(@Param("userId") int userId);

    DiscussPost selectDiscussPostById(int id);

    int insertDiscussPost(DiscussPost discussPost);

    int updateCommentCount(@Param("id") int id, @Param("commentCount") int commentCount);

    int updateTypeById(@Param("id") int id, @Param("type") int type);

    int updateStatusById(@Param("id") int id, @Param("status") int status);

}
