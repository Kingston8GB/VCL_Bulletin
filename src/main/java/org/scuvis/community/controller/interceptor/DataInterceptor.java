package org.scuvis.community.controller.interceptor;

import org.scuvis.community.entity.LoginTicket;
import org.scuvis.community.entity.User;
import org.scuvis.community.service.DataService;
import org.scuvis.community.service.UserService;
import org.scuvis.community.util.CookieUtil;
import org.scuvis.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 用于检查登录状态（请求是否携带ticket）的拦截器
 *
 * @author Xiyao Li
 * @date 2023/06/09 21:14
 */
@Component
public class DataInterceptor implements HandlerInterceptor {

    @Autowired
    HostHolder hostHolder;

    @Autowired
    DataService dataService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = request.getRemoteHost();

        dataService.recordUV(ip);

        User loginUser = hostHolder.getUser();
        if(loginUser != null){
            dataService.recordDAU(loginUser.getId());
        }
        return true;
    }


}
