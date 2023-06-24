package org.scuvis.community.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Xiyao Li
 * @date 2023/06/23 20:46
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Followee {
    User followee;
    Date followTime;
}
