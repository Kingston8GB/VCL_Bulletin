package org.scuvis.community.service;

import org.scuvis.community.dao.UserMapper;
import org.scuvis.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Xiyao Li
 * @date 2023/06/05 16:34
 */

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User findUserById(int id){
        return userMapper.selectById(id);
    }
}
