package org.scuvis.community.util;

/**
 * @author Xiyao Li
 * @date 2023/06/19 15:25
 */

public class RedisUtil {
    public static final String PREFIX_LIKE_ENTITY = "like:entity:";

    public static final String SPLIT = ":";

    public static String getEntityLikeKey(int entityType, int entityId){
        return PREFIX_LIKE_ENTITY + entityType + SPLIT + entityId;
    }
}
