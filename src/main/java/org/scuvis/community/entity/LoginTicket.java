package org.scuvis.community.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Xiyao Li
 * @date 2023/06/08 22:04
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginTicket {
    private int id;
    private int userId;
    private String ticket;
    private int status;

    private Date expired;
}
