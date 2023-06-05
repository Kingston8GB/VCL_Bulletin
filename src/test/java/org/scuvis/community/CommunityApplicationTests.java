package org.scuvis.community;

import org.scuvis.community.dao.AlphaDao;
import org.scuvis.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
class CommunityApplicationTests implements ApplicationContextAware {
	@Autowired
	private SimpleDateFormat simpleDateFormat;

	@Autowired
	@Qualifier("alphaHibernate")
	private AlphaDao alphaDao;

	private ApplicationContext applicationContext;

	@Test
	public void testApplicationContext() {
		// System.out.println(applicationContext);
		AlphaDao alphaDao =  applicationContext.getBean("alphaHibernate",AlphaDao.class);
		String result = alphaDao.select();
		System.out.println(result);
	}

	@Test
	/**
	 * 测试init（@poststruct)、destroy(@predestroy)、@scope(singleton/prototype)
	 */
	public void testBeanManagement(){
		AlphaService alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);

		// 再实例化一次，看看是不是同一个实例
		alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);
	}


	@Test
	/**
	 * 测试把simpleDateFormat装配到容器中,这次使用@autowired进行依赖注入
	 */
	public void testBeanConfigAndDI(){
		System.out.println(simpleDateFormat.format(new Date()));
		System.out.println(alphaDao.select());
	}


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
