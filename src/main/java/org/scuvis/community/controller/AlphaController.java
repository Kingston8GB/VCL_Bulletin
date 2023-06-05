package org.scuvis.community.controller;

import org.scuvis.community.service.AlphaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author Xiyao Li
 * @date 2023/06/03 23:01
 */
@RestController
@RequestMapping("alpha")
public class AlphaController {
    @Autowired
    AlphaService alphaService;

    @RequestMapping("/hello")
    public String sayHello(){
        return "hello spring boot!";
    }

    @RequestMapping("data")
    public String getData(){
        return alphaService.find();
    }

    @RequestMapping("http")
    public String getRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        while(headerNames.hasMoreElements()){
            String name = headerNames.nextElement();
            String value = httpServletRequest.getHeader(name);
            System.out.println(name + ": " + value);
        }
        httpServletResponse.setContentType("text/html;charset:utf-8");
        return httpServletRequest.getMethod() + " " + httpServletRequest.getContextPath() + "" + httpServletRequest.getServletPath();
    }

    // 服务端获取GET请求的数据
    // 传参数
    @RequestMapping(path = "getParameters",method = RequestMethod.GET)
    public String getGETRequestData(@RequestParam(name = "id",required = false,defaultValue = "100") Integer id,
                                    @RequestParam(name = "name",required = true, defaultValue = "xiaoming") String name){
        return "id = " + id + ", name = " + name;
    }

    // 以路径的形式传参
    @RequestMapping(value = "getPathVariable/{id}",method = RequestMethod.GET)
    public String getPathVariable(@PathVariable(name = "id") Integer id){
        return "id = " + id;
    }

    // 从static/html/student.html的表单中获得POST请求
    @RequestMapping(value = "student",method = RequestMethod.POST)
    public String getPostRequestFromForm(String name, String age){
        System.out.println(name);
        System.out.println(age);
        return name + " " + age;
    }

    // 响应html数据，而不是body里面的String
    // 方法一：model和view封装在一起，再返回给dispatcherServlet
    @GetMapping(value = "teacher1")
    public ModelAndView getTeacher1(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("name","zhangsan");
        mav.addObject("age",30);
        mav.setViewName("/demo/view");
        return mav;
    }
    // 方法二：model放在形参里，以String形式返回view视图的路径
    // 注意：用方法二就不能写@RestController或@ResponseBody了
    @GetMapping(value = "teacher2")
    public String getTeacher2(Model model){
        model.addAttribute("name","lisi");
        model.addAttribute("age",40);
        return "/demo/view";
    }

    // 更常用的：响应JSON数据（例如在 异步请求:该昵称已经被占用 中）
    // 目的：把service得到的Java对象转化为JSON字符串，返回前端之后可以转为
    @GetMapping(path = "/emps")
    public List<Map<String,Object>> emps(){
        List<Map<String,Object>> emps = new ArrayList<>();
        Map<String, Object> emp = new HashMap<>();
        emp.put("name","zhangsan");
        emp.put("age",50);
        emps.add(emp);

        emp = new HashMap<>();
        emp.put("name","wangwu");
        emp.put("age","50");
        emps.add(emp);

        return emps;
    }

}
