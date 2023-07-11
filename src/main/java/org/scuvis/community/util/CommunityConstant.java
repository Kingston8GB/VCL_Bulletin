package org.scuvis.community.util;

/**
 * @author Xiyao Li
 * @date 2023/06/07 20:46
 */

public interface CommunityConstant {
    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;

    int ACTIVATION_REPEAT = 0;

    int ACTIVATION_FAILURE = 0;

    int DEFAULT_EXPIRES_SECONDS = 3600 * 12;

    int REMEMBER_EXPIRES_SECONDS = 3600 * 24 * 100;

    String TOPIC_COMMENT = "comment";
    String TOPIC_LIKE = "like";
    String TOPIC_FOLLOW = "follow";
    int NOTICE_FROM_ID = 1;

    String AUTHORITY_ADMIN="admin";
    String AUTHORITY_USER="user";
    String AUTHORITY_MODERATOR="moderator";
}
