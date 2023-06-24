package org.scuvis.community.controller;

import org.apache.commons.lang3.StringUtils;
import org.scuvis.community.annotation.LoginRequired;
import org.scuvis.community.entity.User;
import org.scuvis.community.service.FollowService;
import org.scuvis.community.service.LikeService;
import org.scuvis.community.service.UserService;
import org.scuvis.community.util.CommunityUtil;
import org.scuvis.community.util.HostHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.jws.WebParam;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

/**
 * 处理用户相关的请求，如账号设置
 *
 * @author Xiyao Li
 * @date 2023/06/09 23:32
 */
@Controller
@RequestMapping("/user")
public class UserController {
    public static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    FollowService followService;

    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage(){
        return "/site/setting";
    }

    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage == null){
            model.addAttribute("error","您还没有上传头像！");
            return "/site/setting";
        }
        // 给文件生成一个随机的名字，保存在硬盘上
        String originalFilename = headerImage.getOriginalFilename();
        // 截取扩展名
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件格式不正确！");
            return "/site/setting";
        }
        String newFileName = CommunityUtil.generateUUID() + suffix;
        File dest = new File(uploadPath + "/" + newFileName);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            LOGGER.error("上传文件失败！" + e.getMessage());
            throw new RuntimeException("上传文件异常",e);
        }

        // 更新当前用户头像路径（web路径）
        int affectedRows = userService.updateHeader(hostHolder.getUser().getId(), domain + contextPath + "/user/header/" + newFileName);

        return "redirect:/index";

    }

    @GetMapping("/header/{filename}")
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response){
        // 根据web路径，找到服务器上存放的路径，向浏览器响应图片
        String newFilename = uploadPath + "/" + filename;
        String suffix = newFilename.substring(newFilename.lastIndexOf("."));
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(newFilename);
                ServletOutputStream os = response.getOutputStream();
        ){
            byte[] buffer = new byte[1024];
            int b = 0;
            while((b = fis.read(buffer)) != -1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            LOGGER.error("读取头像失败：" + e.getMessage());
        }
    }

    @PostMapping("/changePassword")
    public String changePassword(Model model, String oldPassword, String newPassword, String confirmNewPassword){
        if(StringUtils.isBlank(oldPassword)){
            model.addAttribute("oldPasswordMsg","原密码不得为空！");
            return "/site/setting";
        }
        if(StringUtils.isBlank(newPassword)){
            model.addAttribute("newPasswordMsg","新密码不得为空！");
            return "/site/setting";
        }
        if(StringUtils.isBlank(confirmNewPassword)){
            model.addAttribute("confirmNewPasswordMsg","新密码不得为空！");
            return "/site/setting";
        }

        // if(oldPassword.length() < 3){
        //     model.addAttribute("oldPasswordMsg","原密码不得少于3位！");
        //     return "/site/setting";
        // }
        if(!confirmNewPassword.equals(newPassword)){
            model.addAttribute("confirmNewPasswordMsg","两次输入的密码不一致！");
            return "/site/setting";
        }

        User loginUser = hostHolder.getUser();
        Map<String, Object> map = userService.updatePassword(loginUser, oldPassword, newPassword);
        if(map != null && !map.isEmpty()){
            for (String s : map.keySet()) {
                model.addAttribute(s,map.get(s));
            }
            // model.addAttribute(map.,"两次输入的密码不一致！");
            return "/site/setting";
        }

        if(oldPassword.equals(newPassword)){
            model.addAttribute("newPasswordMsg","新旧密码不得一致！");
            return "/site/setting";
        }
        return "redirect:/index";
    }

    @GetMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable("userId") int userId,Model model){
        User userById = userService.findUserById(userId);
        if(userById == null){
            throw new IllegalArgumentException("错误的用户参数！");
        }
        int userLikeCount = likeService.findUserLikeCount(userId);

        long followerCount = followService.findFollowerCount(3, userId);
        Long followeeCount = followService.findFolloweeCount(3, userId);
        boolean hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), 3, userId);

        model.addAttribute("userLikeCount",userLikeCount);
        model.addAttribute("userId",userId);
        model.addAttribute("user",userById);
        model.addAttribute("followerCount",followerCount);
        model.addAttribute("followeeCount",followeeCount);
        model.addAttribute("hasFollowed",hasFollowed);
        return "/site/profile";
    }
}
