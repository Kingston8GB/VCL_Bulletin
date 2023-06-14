package org.scuvis.community.dao;

import org.apache.ibatis.annotations.*;
import org.scuvis.community.entity.LoginTicket;

/**
 * @author Xiyao Li
 * @date 2023/06/08 22:06
 */

@Mapper
public interface LoginTicketMapper {
    @Insert({"insert into login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"})
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({"select id,user_id,ticket,status,expired ",
            "from login_ticket where ticket=#{ticket}"})
    LoginTicket selectLoginTicketByTicket(String ticket);

    @Update({"update login_ticket set status=#{status} where ticket=#{ticket}"})
    int updateStatus(String ticket, int status);
}
