package org.scuvis.community.service;

import org.scuvis.community.entity.Followee;
import org.scuvis.community.entity.Follower;
import org.scuvis.community.entity.User;
import org.scuvis.community.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Xiyao Li
 * @date 2023/06/23 19:42
 */
@Service
public class FollowService {
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    UserService userService;

    public void follow(int userId, int entityType, int entityId) {
        String followerKey = RedisUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisUtil.getFolloweeKey(userId, entityType);

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                operations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());
                operations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                return operations.exec();
            }
        });
    }

    public void unfollow(int userId, int entityType, int entityId) {
        String followerKey = RedisUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisUtil.getFolloweeKey(userId, entityType);

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                operations.opsForZSet().remove(followerKey, userId, System.currentTimeMillis());
                operations.opsForZSet().remove(followeeKey, entityId, System.currentTimeMillis());
                return operations.exec();
            }
        });
    }

    public Long findFolloweeCount(int entityType, int userId) {
        String followeeKey = RedisUtil.getFolloweeKey(userId, entityType);
        Long count = redisTemplate.opsForZSet().zCard(followeeKey);
        return count;

    }

    public List<Followee> findFollowees(int userId, int entityType, int offset, int limit) {
        String followeeKey = RedisUtil.getFolloweeKey(userId, entityType);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);

        List<Followee> followeeList = new ArrayList<>();
        if (targetIds != null) {
            for (Integer targetId : targetIds) {
                Followee followee = new Followee();
                User userById = userService.findUserById(targetId);
                followee.setFollowee(userById);
                followee.setFollowTime(new Date(redisTemplate.opsForZSet().score(followeeKey, targetId).longValue()));
                followeeList.add(followee);
            }
        }
        return followeeList;
    }

    public boolean hasFollowed(int loginUserId, int entityType, int userId) {
        String followeeKey = RedisUtil.getFolloweeKey(loginUserId, entityType);
        return (redisTemplate.opsForZSet().score(followeeKey, userId) != null);
    }

    public long findFollowerCount(int entityType, int entityId) {
        String followerKey = RedisUtil.getFollowerKey(entityType, entityId);
        Long count = redisTemplate.opsForZSet().zCard(followerKey);
        return count;
    }

    public List<Follower> findFollowers(int entityId, int entityType, int offset, int limit) {
        String followerKey = RedisUtil.getFollowerKey(entityType, entityId);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);

        List<Follower> followerList = new ArrayList<>();
        if (targetIds != null) {

            for (Integer targetId : targetIds) {
                Follower follower = new Follower();
                User userById = userService.findUserById(targetId);
                follower.setFollower(userById);
                follower.setFollowTime(new Date(redisTemplate.opsForZSet().score(followerKey, targetId).longValue()));
                followerList.add(follower);
            }
        }
        return followerList;
    }
}
