package org.scuvis.community.controller;

import org.scuvis.community.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

/**
 * 网站统计
 *
 * @author Xiyao Li
 * @date 2023/07/25 00:45
 */
@Controller
public class DataController {
    @Autowired
    DataService dataService;

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/data")
    public String getDataPage(){
        return "/site/admin/data";
    }

    /**
     * 在data页里获取uv的方法
     * @param start
     * @param end
     * @param model
     * @return
     */
    @PostMapping("/data/uv")
    public String getUV(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end,
                        Model model){
        Long uv = dataService.calculateUV(start, end);
        model.addAttribute("uvResult",uv);
        model.addAttribute("uvStartDate",start);
        model.addAttribute("uvEndDate",end);
        // 回显
        return "forward:/data";
    }

    @PostMapping("/data/uv")
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end,
                        Model model){
        Long dau = dataService.calculateDAU(start, end);
        model.addAttribute("dauResult",dau);
        model.addAttribute("dauStartDate",start);
        model.addAttribute("uvEndDate",end);
        // 回显
        return "forward:/data";
    }

}
