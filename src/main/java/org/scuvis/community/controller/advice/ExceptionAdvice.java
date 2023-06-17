package org.scuvis.community.controller.advice;

import org.scuvis.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Xiyao Li
 * @date 2023/06/17 22:38
 */
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {
    public static final Logger LOGGER = LoggerFactory.getLogger(ExceptionAdvice.class);
    @ExceptionHandler
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOGGER.error("服务器发生异常：" + e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            LOGGER.error(element.toString());
        }

        String xRequestedWith = request.getHeader("x-requested-with");
        if("XMLHttpRequest".equals(xRequestedWith)){
            response.setContentType("application/plain;chaset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1,"服务器异常"));
        }else{
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}
