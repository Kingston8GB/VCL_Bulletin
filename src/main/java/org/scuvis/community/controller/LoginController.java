package org.scuvis.community.controller;

import com.google.code.kaptcha.Producer;
import org.apache.commons.lang3.StringUtils;
import org.scuvis.community.entity.User;
import org.scuvis.community.service.UserService;
import org.scuvis.community.util.CommunityConstant;
import org.scuvis.community.util.CommunityUtil;
import org.scuvis.community.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.jws.WebParam;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Xiyao Li
 * @date 2023/06/07 16:44
 */
@Controller
public class LoginController implements CommunityConstant {

    @Autowired
    private UserService userService;

    @Autowired
    private Producer producer;

    @Value("server.servlet.context-path")
    private String contextPath;

    @Autowired
    RedisTemplate redisTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("/register")
    public String getRegisterPage() {
        return "/site/register";
    }

    @PostMapping("/register")
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快查收！");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }

    @GetMapping("/activation/{userId}/{activationCode}")
    public String activate(Model model, @PathVariable("userId") int userId,
                           @PathVariable("activationCode") String activationCode) {
        int result = userService.activation(userId, activationCode);
        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg","激活成功！您的账号已经可以登录！");
            model.addAttribute("target","/login"); // 跳转到登录页面
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg","无效操作！此账号已经激活过了！");
            model.addAttribute("target","/index"); // 跳转到登录页面
        } else {
            model.addAttribute("msg","注册失败！");
            model.addAttribute("target","/index"); // 跳转到登录页面
        }
        return "/site/operate-result";
    }

    @GetMapping("/login")
    public String getLoginPage(){
        return "/site/login";
    }

    @GetMapping("/kaptcha")
    // public void getKaptcha(HttpServletResponse response, HttpSession session){
    public void getKaptcha(HttpServletResponse response){
        String text = producer.createText();
        BufferedImage image = producer.createImage(text);

        // 把验证码存入session
        // session.setAttribute("kaptcha",text);
        String owner = CommunityUtil.generateUUID();
        String kaptchaKey = RedisUtil.getKaptchaKey(owner);
        redisTemplate.opsForValue().set(kaptchaKey,text,120, TimeUnit.SECONDS);
        Cookie cookieOwner = new Cookie("owner", owner);
        response.addCookie(cookieOwner);

        response.setContentType("image/png");
        try {
            ServletOutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            LOGGER.error("相应验证码失败——"+ e.getMessage());
        }
    }

    @PostMapping("/login")
    // public String login(Model model, String username, String password, String code, boolean rememberMe, HttpSession session, HttpServletResponse response){
    public String login (Model model, String username, String password, String code, boolean rememberMe, @CookieValue("owner") String owner, HttpServletResponse response){

            // 先判断验证码，如果验证码不对，不用调service
        // String kaptcha = (String) session.getAttribute("kaptcha");
        String kaptcha = null;
        if(StringUtils.isNotBlank(owner)){
            String kaptchaKey = RedisUtil.getKaptchaKey(owner);
            kaptcha = (String) redisTemplate.opsForValue().get(kaptchaKey);
        }


        if(StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码不正确！");
            return "/site/login";
        }

        // 检查账号密码
        // 根据是否勾选“记住我”，传给业务不同的过期时间
        int expiredSeconds = rememberMe ? REMEMBER_EXPIRES_SECONDS : DEFAULT_EXPIRES_SECONDS;

        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if(map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket", (String) map.get("ticket"));
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }

    }

    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        SecurityContextHolder.clearContext();
        return "redirect:/login";
    }

}
