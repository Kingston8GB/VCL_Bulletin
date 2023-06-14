package org.scuvis.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

/**
 * 自定义配置类
 *
 * @author Xiyao Li
 * @date 2023/06/07 17:02
 */

public class CommunityUtil {
    public static String generateUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }

    /**
     * md5加密
     */
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
