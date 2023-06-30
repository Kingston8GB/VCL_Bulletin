package org.scuvis.community.actuator;

import org.scuvis.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 自定义端点的小demo，检测数据库连接池是否可以正常获取连接
 * @author Xiyao Li
 * @date 2023/06/28 00:18
 */
@Component
@Endpoint(id = "database")
public class DatabaseEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseEndpoint.class);

    @Autowired
    DataSource dataSource;

    // ReadOperation表示用GET请求访问 /community/actuator/database
    @ReadOperation
    public String checkConnection(){
        try(
                Connection conn = dataSource.getConnection()
        ){
            return CommunityUtil.getJSONString(0,"获取连接成功！");
        }catch (SQLException e){
            LOGGER.error("获取连接失败：" + e.getMessage());
            return CommunityUtil.getJSONString(1,"获取连接失败！");
        }

    }
}
