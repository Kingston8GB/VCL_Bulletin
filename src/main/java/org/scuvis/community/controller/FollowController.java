package org.scuvis.community.controller;

import org.scuvis.community.entity.Followee;
import org.scuvis.community.entity.Follower;
import org.scuvis.community.entity.Page;
import org.scuvis.community.entity.User;
import org.scuvis.community.service.FollowService;
import org.scuvis.community.service.UserService;
import org.scuvis.community.util.CommunityUtil;
import org.scuvis.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Xiyao Li
 * @date 2023/06/23 19:50
 */
@Controller
public class FollowController {
    @Autowired
    HostHolder hostHolder;

    @Autowired
    FollowService followService;

    @Autowired
    UserService userService;

    @PostMapping("/follow")
    @ResponseBody
    public String follow(int entityType, int entityId){
        // User user = hostHolder.getUser();
        // if(user == null){
        //     throw new IllegalArgumentException("当前用户未登录！");
        // }
        System.out.println("follow方法！");
        followService.follow(hostHolder.getUser().getId(), entityType, entityId);
        return CommunityUtil.getJSONString(0, "关注成功！", null);

    }

    @PostMapping("/unfollow")
    @ResponseBody
    public String unfollow(int entityType, int entityId){
        User user = hostHolder.getUser();
        if(user == null){
            throw new IllegalArgumentException("当前用户未登录！");
        }

        followService.unfollow(user.getId(), entityType,entityId);
        return CommunityUtil.getJSONString(0, "取消关注成功！", null);

    }

    @GetMapping("/followees/{userId}")
    public String findFollowees(@PathVariable("userId") int userId, Model model, Page page){
        User userById = userService.findUserById(userId);
        if(userById == null){
            throw new IllegalArgumentException("该用户id不存在！");
        }

        page.setLimit(5);
        page.setPath("/followees/" + userId);
        page.setRows(Math.toIntExact(followService.findFolloweeCount(3, userId)));
        List<Followee> followeeList = followService.findFollowees(userId,3,page.getOffset(),page.getLimit());

        if(followeeList!=null){
            Map<String, Object> map = new HashMap<>();
            for (Followee followee : followeeList) {
                User u = followee.getFollowee();
                map.put("hasFollowed",hasFollowed(u.getId()));
            }
        }

        model.addAttribute("followees",followeeList);

        return "/site/followee";
    }

    private boolean hasFollowed(int userId) {
        return followService.hasFollowed(hostHolder.getUser().getId(), 3, userId);
    }

    @GetMapping("/followers/{userId}")
    public String findFollowers(@PathVariable("userId") int userId, Model model, Page page){
        User userById = userService.findUserById(userId);
        if(userById == null){
            throw new IllegalArgumentException("该用户id不存在！");
        }

        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setRows(Math.toIntExact(followService.findFollowerCount(3, userId)));
        List<Follower> followerList = followService.findFollowers(userId,3,page.getOffset(),page.getLimit());

        if(followerList!=null){
            Map<String, Object> map = new HashMap<>();
            for (Follower follower : followerList) {
                User u = follower.getFollower();
                map.put("hasFollowed",hasFollowed(u.getId()));
            }
        }

        model.addAttribute("followers",followerList);

        return "/site/followee";
    }
}
