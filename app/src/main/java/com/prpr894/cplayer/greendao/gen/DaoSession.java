package com.prpr894.cplayer.greendao.gen;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.prpr894.cplayer.bean.LiveRoomItemDataBean;

import com.prpr894.cplayer.greendao.gen.LiveRoomItemDataBeanDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig liveRoomItemDataBeanDaoConfig;

    private final LiveRoomItemDataBeanDao liveRoomItemDataBeanDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        liveRoomItemDataBeanDaoConfig = daoConfigMap.get(LiveRoomItemDataBeanDao.class).clone();
        liveRoomItemDataBeanDaoConfig.initIdentityScope(type);

        liveRoomItemDataBeanDao = new LiveRoomItemDataBeanDao(liveRoomItemDataBeanDaoConfig, this);

        registerDao(LiveRoomItemDataBean.class, liveRoomItemDataBeanDao);
    }
    
    public void clear() {
        liveRoomItemDataBeanDaoConfig.clearIdentityScope();
    }

    public LiveRoomItemDataBeanDao getLiveRoomItemDataBeanDao() {
        return liveRoomItemDataBeanDao;
    }

}
