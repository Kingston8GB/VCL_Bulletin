package org.scuvis.community.controller;

import org.scuvis.community.entity.DiscussPost;
import org.scuvis.community.entity.Page;
import org.scuvis.community.entity.User;
import org.scuvis.community.entity.vo.CommentVO;
import org.scuvis.community.service.DiscussPostService;
import org.scuvis.community.service.LikeService;
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

    @Autowired
    LikeService likeService;

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
        model.addAttribute("likeCount",likeService.findEntityLikeCount(1,discussPostId));
        model.addAttribute("likeStatus",likeService.findEntityLikeStatus(hostHolder.getUser().getId(),1,discussPostId));
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

    @PostMapping("/top")
    @ResponseBody
    public String setTop(int postId){
        int type = discussPostService.findDiscussPostById(postId).getType();

        discussPostService.updateTypeById(postId,type^1);
        return CommunityUtil.getJSONString(0);
    }

    @PostMapping("/wonderful")
    @ResponseBody
    public String setWonderful(int postId){
        int status = discussPostService.findDiscussPostById(postId).getStatus();

        discussPostService.updateStatusById(postId,status^1);
        return CommunityUtil.getJSONString(0);
    }

    @PostMapping("/delete")
    @ResponseBody
    public String setDelete(int postId){
        discussPostService.updateStatusById(postId,2);
        return CommunityUtil.getJSONString(0);
    }
}
