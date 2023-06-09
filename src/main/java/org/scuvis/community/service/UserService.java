package org.scuvis.community.service;

import org.apache.commons.lang3.StringUtils;
import org.scuvis.community.dao.LoginTicketMapper;
import org.scuvis.community.dao.UserMapper;
import org.scuvis.community.entity.LoginTicket;
import org.scuvis.community.entity.User;
import org.scuvis.community.util.CommunityUtil;
import org.scuvis.community.util.MailClient;
import org.scuvis.community.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.scuvis.community.util.CommunityConstant.*;

/**
 * @author Xiyao Li
 * @date 2023/06/05 16:34
 */

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    LoginTicketMapper loginTicketMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    RedisTemplate redisTemplate;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id){
        User cacheUser = null;
        cacheUser = getCache(id);
        if(cacheUser == null){
            cacheUser = initCache(id);
        }
        // return userMapper.selectById(id);
        return cacheUser;
    }


    public Map<String,Object> register(User user){
        Map<String,Object> map = new HashMap<>();

        // 对空值的处理
        if(user == null){
            throw new IllegalArgumentException("参数不能为空！！");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空！");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空！");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空！");
            return map;
        }

        // 验证账号、邮箱是否已存在
        User u = userMapper.selectByName(user.getUsername());
        if(u != null){
            map.put("usernameMsg","该账号已存在！");
            return map;
        }
        u = userMapper.selectByEmail(user.getEmail());
        if(u != null){
            map.put("emailMsg","该邮箱已存在！");
            return map;
        }

        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());

        int affectedRows = userMapper.insertUser(user);

        // 用模板引擎构造HTML形式的邮件内容
        Context context = new Context();
        context.setVariable("email",user.getUsername());
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation", context);

        mailClient.sendMail(user.getEmail(),"注册验证",content);
        return map;
    }

    public int activation(int userId, String activationCode){
        // User u = userMapper.selectById(userId);

        User cacheUser = null;
        cacheUser = getCache(userId);
        if(cacheUser == null){
            cacheUser = initCache(userId);
        }

        // 已经激活了
        if(cacheUser.getStatus() == 1){
            return ACTIVATION_REPEAT;
        }else if(activationCode.equals(cacheUser.getActivationCode())){
            userMapper.updateStatus(userId,1);
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }

    public Map<String,Object> login(String username, String password, int expiredSeconds){
        Map<String, Object> map = new HashMap<>();
        if(StringUtils.isBlank(username)){
            map.put("userNameMsg","账号不能为空！");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空！");
            return map;
        }

        // 验证账号
        User u = userMapper.selectByName(username);
        if(u == null){
            map.put("usernameMsg","账号不存在！");
            return map;
        }
        // 验证账号是否已激活
        if(u.getStatus() == 0){
            map.put("usernameMsg","账号已注册，但未激活！");
            return map;
        }

        // 验证密码
        password = CommunityUtil.md5(password + u.getSalt());
        if(!u.getPassword().equals(password)){
            map.put("passwordMsg","密码错误！");
            return map;
        }

        // 生成登录凭证，存在数据库里（相当于session）
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(u.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        // loginTicketMapper.insertLoginTicket(loginTicket);

        // 改为存入redis
        String ticketKey = RedisUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(ticketKey,loginTicket);


        // 把凭证返回给客户端
        map.put("ticket",loginTicket.getTicket());

        return map;
    }

    public void logout(String ticket){
        // loginTicketMapper.updateStatus(ticket,1);
        String ticketKey = RedisUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);

        if (loginTicket!=null) {
            loginTicket.setStatus(1);
        }
        redisTemplate.opsForValue().set(ticketKey,loginTicket);
    }

    public LoginTicket findLoginTicket(String ticket){
        String ticketKey = RedisUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        // return loginTicketMapper.selectLoginTicketByTicket(ticket);
    }

    public int updateHeader(int userId, String headerUrl){
        clearCache(userId);
        return userMapper.updateHeader(userId, headerUrl);
    }

    /**
     * 修改密码
     * @param user 当前登录用户（controller层从hostHolder里取到的）
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改密码的提示信息(一些格式的判断直接在controller层处理，这一层只做业务）
     */
    public Map<String,Object> updatePassword(User user,String oldPassword,String newPassword){
        Map<String, Object> map = new HashMap<>();

        // User u = userMapper.selectByName(user.getUsername());
        String oldPasswordByMd5 = CommunityUtil.md5(oldPassword + user.getSalt());
        if(!oldPasswordByMd5.equals(user.getPassword())){
            map.put("oldPasswordMsg","原密码错误！");
            return map;
        }
        String newPasswordByMd5 = CommunityUtil.md5(newPassword + user.getSalt());
        int affectedRows = userMapper.updatePassword(user.getId(), newPasswordByMd5);
        return map;
    }

    public User findUserByName(String username){
        return userMapper.selectByName(username);
    }


    // 1.优先从缓存中取值
    private User getCache(int userId) {
        String redisKey = RedisUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }

    // 2.取不到时初始化缓存数据
    private User initCache(int userId) {
        User user = userMapper.selectById(userId);
        String redisKey = RedisUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    // 3.数据变更时清除缓存数据
    private void clearCache(int userId) {
        String redisKey = RedisUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }

    public Collection<? extends GrantedAuthority> getAuthorities(int userId) {
        User user = this.findUserById(userId);

        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {
                switch (user.getType()) {
                    case 1:
                        return AUTHORITY_ADMIN;
                    case 2:
                        return AUTHORITY_MODERATOR;
                    default:
                        return AUTHORITY_USER;
                }
            }
        });
        return list;
    }
}
