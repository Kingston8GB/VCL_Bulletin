package org.scuvis.community.controller;

import org.scuvis.community.entity.DiscussPost;
import org.scuvis.community.entity.Page;
import org.scuvis.community.entity.User;
import org.scuvis.community.entity.vo.CommentVO;
import org.scuvis.community.service.DiscussPostService;
import org.scuvis.community.service.UserService;
import org.scuvis.community.util.CommunityUtil;
import org.scuvis.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author Xiyao Li
 * @date 2023/06/15 20:12
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController {
    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @GetMapping("/detail/{discussPostId}")
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
        DiscussPost discussPost = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",discussPost);

        User user = userService.findUserById(discussPost.getUserId());
        model.addAttribute("user",user);

        page.setLimit(6);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(discussPost.getCommentCount());

        List<CommentVO> commentVOS = discussPostService.findCommentsByEntity(1, discussPost.getId(), page.getOffset(), page.getLimit());
        model.addAttribute("comments",commentVOS);
        return "/site/discuss-detail";
    }

    @PostMapping("add")
    @ResponseBody
    public String addDiscussPost(String title, String content){
        User loginUser = hostHolder.getUser();
        if(loginUser == null){
            return CommunityUtil.getJSONString(403,"未登录！");
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setUserId(loginUser.getId());
        discussPost.setCreateTime(new Date());
        discussPostService.addDiscussPost(discussPost);

        return CommunityUtil.getJSONString(200,"帖子添加成功！");
    }
}
