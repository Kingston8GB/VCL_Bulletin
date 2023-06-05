package org.scuvis.community.dao.impl;

import org.scuvis.community.dao.AlphaDao;
import org.springframework.stereotype.Repository;

/**
 * @author Xiyao Li
 * @date 2023/06/03 23:35
 */
@Repository("alphaHibernate")
public class AlphaDaoHibernateImpl implements AlphaDao {
    @Override
    public String select() {
        return "Hibernate";
    }
}
