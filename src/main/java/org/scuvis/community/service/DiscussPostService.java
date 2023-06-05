package org.scuvis.community.service;

import org.scuvis.community.dao.DiscussPostMapper;
import org.scuvis.community.dao.UserMapper;
import org.scuvis.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit){
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    public int findDiscussPostCountByUserId(int userId){
        return discussPostMapper.selectDiscussPostRowsByUserId(userId);
    }
}
