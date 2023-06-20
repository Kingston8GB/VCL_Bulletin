package org.scuvis.community.util;

/**
 * @author Xiyao Li
 * @date 2023/06/19 15:25
 */

public class RedisUtil {
    public static final String PREFIX_LIKE_ENTITY = "like:entity:";
    public static final String PREFIX_LIKE_USER = "like:user:";

    public static final String SPLIT = ":";

    /**
     * 某个实体收到的赞
     * @param entityType 帖子还是评论
     * @param entityId 帖子/评论的id
     * @return key，用于查询赞的成员
     */
    public static String getEntityLikeKey(int entityType, int entityId){
        return PREFIX_LIKE_ENTITY + entityType + SPLIT + entityId;
    }

    /**
     * 某个用户收到的赞（需要和实体收到的赞一起更新）
     * @param userId 用户id
     * @return key，用于查询某个用户收到的赞有多少
     */
    public static String getUserLikeKey(int userId){
        return PREFIX_LIKE_USER + userId;
    }

}
