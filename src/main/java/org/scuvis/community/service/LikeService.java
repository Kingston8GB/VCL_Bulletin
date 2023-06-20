package org.scuvis.community.service;

import org.scuvis.community.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

/**
 * @author Xiyao Li
 * @date 2023/06/19 15:28
 */
@Service
public class LikeService {
    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 点赞方法
     * @param userId 谁点赞
     * @param entityType 点赞了帖子还是回复
     * @param entityId 点赞了哪篇帖子 / 回复
     */
    public void like(int userId, int entityType, int entityId, int entityUserId){
        // String entityLikeKey = RedisUtil.getEntityLikeKey(entityType,entityId);
        //
        // boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey,userId);
        //
        // if(isMember){
        //     redisTemplate.opsForSet().add(entityLikeKey,userId);
        // }else {
        //     redisTemplate.opsForSet().remove(entityLikeKey,userId);
        // }

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisUtil.getEntityLikeKey(entityType,entityId);
                String userLikeKey = RedisUtil.getUserLikeKey(entityUserId);

                boolean isMember = operations.opsForSet().isMember(entityLikeKey,userId);

                operations.multi();
                if(isMember) {
                    operations.opsForSet().remove(entityLikeKey,userId);
                    operations.opsForValue().decrement(userLikeKey);
                }else{
                    operations.opsForSet().add(entityLikeKey,userId);
                    operations.opsForValue().increment(userLikeKey);
                }
                return operations.exec();

            }
        });
    }

    public Long findEntityLikeCount(int entityType, int entityId){
        String entityLikeKey = RedisUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    public int findEntityLikeStatus(int userId, int entityType, int entityId){
        String entityLikeKey = RedisUtil.getEntityLikeKey(entityType,entityId);

        boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey,userId);
        return isMember == true ? 1 : 0;
    }


    public Object findUserLikeCount(int userId){
        String userLikeKey = RedisUtil.getUserLikeKey(userId);
        return redisTemplate.opsForValue().get(userLikeKey);
    }
}
