package org.jim.xj.nutz;

import java.util.List;

import org.nutz.dao.Chain;
import org.nutz.dao.Condition;
import org.nutz.dao.Dao;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.pager.Pager;
import org.nutz.lang.Each;
import org.nutz.lang.Mirror;
import org.nutz.log.Log;
import org.nutz.log.Logs;


public abstract class EntityService<T> extends Service {

    private Mirror<T> mirror;

    private static final Log log = Logs.get();

    @SuppressWarnings("unchecked")
    public EntityService() {
        try {
            Class<T> entryClass = (Class<T>) Mirror.getTypeParam(getClass(), 0);
            mirror = Mirror.me(entryClass);
            if (log.isDebugEnabled())
                log.debugf("Get TypeParams for self : %s", entryClass.getName());
        }
        catch (Throwable e) {
            if (log.isWarnEnabled())
                log.warn("!!!Fail to get TypeParams for self!", e);
        }
    }

    public EntityService(Dao dao) {
        this();
        this.setDao(dao);
    }

    public EntityService(Dao dao, Class<T> entityType) {
        setEntityType(entityType);
        setDao(dao);
    }

    protected Mirror<T> mirror() {
        return mirror;
    }

    @SuppressWarnings("unchecked")
    protected <C extends T> void setEntityType(Class<C> classOfT) {
        mirror = (Mirror<T>) Mirror.me(classOfT);
    }

    protected Entity<T> getEntity() {
        return dao().getEntity(mirror.getType());
    }

    protected Class<T> getEntityClass() {
        return mirror.getType();
    }

    protected int clear(Condition cnd) {
        return dao().clear(getEntityClass(), cnd);
    }

    protected int clear() {
        return dao().clear(getEntityClass(), null);
    }

    protected List<T> query(Condition cnd, Pager pager) {
        return (List<T>) dao().query(getEntityClass(), cnd, pager);
    }

    protected int each(Condition cnd, Pager pager, Each<T> callback) {
        return dao().each(getEntityClass(), cnd, pager, callback);
    }

    protected int count(Condition cnd) {
        return dao().count(getEntityClass(), cnd);
    }

    protected int count() {
        return dao().count(getEntityClass());
    }

    protected T fetch(Condition cnd) {
        return dao().fetch(getEntityClass(), cnd);
    }

    /**
     * 复合主键专用
     * 
     * @param pks
     *            键值
     * @return 对象 T
     */
    protected T fetchx(Object... pks) {
        return dao().fetchx(getEntityClass(), pks);
    }

    /**
     * 复合主键专用
     * 
     * @param pks
     *            键值
     * @return 对象 T
     */
    protected boolean exists(Object... pks) {
        return null != fetchx(pks);
    }

    protected void update(Chain chain, Condition cnd) {
        dao().update(getEntityClass(), chain, cnd);
    }

    protected void updateRelation(String regex, Chain chain, Condition cnd) {
        dao().updateRelation(getEntityClass(), regex, chain, cnd);
    }

    protected int deletex(Object... pks) {
        return dao().deletex(getEntityClass(), pks);
    }
}
