package org.scuvis.community;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.scuvis.community.dao.AlphaDao;
import org.scuvis.community.dao.DiscussPostMapper;
import org.scuvis.community.dao.MessageMapper;
import org.scuvis.community.dao.UserMapper;
import org.scuvis.community.entity.DiscussPost;
import org.scuvis.community.entity.Message;
import org.scuvis.community.entity.User;
import org.scuvis.community.service.AlphaService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
class MapperTests {
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private DiscussPostMapper discussPostMapper;

	@Autowired
	MessageMapper messageMapper;

	@Test
	public void testSelectAndInsert(){
		System.out.println(userMapper.selectById(10));
		System.out.println(userMapper.selectByName("lihonghe"));
		User user = new User();
		user.setEmail("xxx@qq.com");
		user.setType(1);
		System.out.println("insert affectedRows = " + userMapper.insertUser(user));
	}

	@Test
	public void testSelectPosts(){
		// List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0, 0, 20);
		// for (DiscussPost discussPost : discussPosts) {
		// 	System.out.println(discussPost);
		// }

		int count = discussPostMapper.selectDiscussPostRowsByUserId(149);
		System.out.println(count);
	}

	@Test
	public void testSelectLetters(){
		List<Message> messages = messageMapper.selectConversations(111, 0, 15);
		for (Message message : messages) {
			System.out.println(message);
		}

		System.out.println(messageMapper.selectConversationCount(111));

		List<Message> letters = messageMapper.selectLetters("111_112", 0, 15);
		for (Message letter : letters) {
			System.out.println(letter);
		}

		System.out.println(messageMapper.selectLetterCount("111_112"));

		System.out.println(messageMapper.selectLetterUnreadCount(131, "111_131"));

	}
}