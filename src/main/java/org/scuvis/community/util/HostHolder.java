package org.scuvis.community.util;

import org.scuvis.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息，代替session对象
 *
 * @author Xiyao Li
 * @date 2023/06/09 21:25
 */


@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }
}
