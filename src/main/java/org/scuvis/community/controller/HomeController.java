package org.scuvis.community.controller;

import org.scuvis.community.entity.DiscussPost;
import org.scuvis.community.entity.Page;
import org.scuvis.community.service.DiscussPostService;
import org.scuvis.community.service.LikeService;
import org.scuvis.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Xiyao Li
 * @date 2023/06/05 16:43
 */

@Controller
public class HomeController {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    LikeService likeService;

    @GetMapping("/")
    public String root(){
        return "forward:/index";
    }

    @GetMapping({"/index","/index*"})
    public String getIndexPage(Model model, Page page){
        // page已经自动注入到model里了
        page.setRows(discussPostService.findDiscussPostCountByUserId(0));
        page.setPath("/index");


        // 1.先查前10条帖子数据
        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        // 2.再遍历集合，查每条帖子里的userId对应的username，封装到新的List里
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if(list!=null){
            for (DiscussPost discussPost : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post",discussPost);
                map.put("user",userService.findUserById(discussPost.getUserId()));
                map.put("likeCount",likeService.findEntityLikeCount(1,discussPost.getId()));
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        return "/index";
    }

    @GetMapping("/error")
    public String getErrorPage(){
        return "/error/500";
    }

    // 拒绝访问时的提示页面
    @RequestMapping(path = "/denied", method = RequestMethod.GET)
    public String getDeniedPage() {
        return "/error/404";
    }
}
