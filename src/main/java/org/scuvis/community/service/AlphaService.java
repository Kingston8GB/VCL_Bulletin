package org.scuvis.community.service;

import org.scuvis.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author Xiyao Li
 * @date 2023/06/04 00:16
 */

// @Scope()
@Service
public class AlphaService {
    @Autowired
    AlphaDao alphaDao;

    public AlphaService() {
        System.out.println("AlphaService构造器！");
    }

    @PostConstruct
    public void init(){
        System.out.println("初始化AlphaService！");
    }

    @PreDestroy
    public void destroy(){
        System.out.println("销毁AlphaService！");
    }

    public String find(){
        return alphaDao.select();
    }
}
