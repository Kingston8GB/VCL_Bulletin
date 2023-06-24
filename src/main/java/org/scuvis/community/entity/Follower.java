package org.scuvis.community.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Xiyao Li
 * @date 2023/06/23 21:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Follower {
    User follower;
    Date followTime;
}
