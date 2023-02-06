package ${packageName}.${basePackageName};

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * <p>
 * Service基类
 * </p>
 *
 * @author ${author}
 * @date ${date}
 */
public interface BaseService<T> {

    Long insert(T entity);

    Long insertSelective(T entity);

    Long insertList(List<T> entityList);

    Long deleteByPrimaryKey(Object primaryKey);

    Long delete(T entity);

    Long updateByPrimaryKey(T entity);

    Long updateByPrimaryKeySelective(T entity);

    T selectByPrimaryKey(Object primaryKey);

    default <R> R selectByPrimaryKey(Object primaryKey, Class<R> cls) {
        T result = selectByPrimaryKey(primaryKey);
        return BeanUtil.toBean(result, cls);
    }

    T selectOne(T entity);

    default <R> R selectOne(T entity, Class<R> cls) {
        T result = selectOne(entity);
        return BeanUtil.toBean(result, cls);
    }

    List<T> select(T entity);

    default <R> List<R> select(T entity, Class<R> cls) {
        List<T> resultList = select(entity);
        return BeanUtil.copyToList(resultList, cls);
    }

    <P extends BaseList> List<T> list(P param);

    default <P extends BaseList, R> List<R> list(P param, Class<R> cls) {
        List<T> resultList = list(param);
        return BeanUtil.copyToList(resultList, cls);
    }

    List<T> selectAll();

    default <R> List<R> selectAll(Class<R> cls) {
        List<T> resultList = selectAll();
        return BeanUtil.copyToList(resultList, cls);
    }

    Long count(T entity);

    <P extends BasePage> PageInfo<T> page(P param);

    default <P extends BasePage, R> PageInfo<R> page(P param, Class<R> cls) {
        PageInfo<T> result = page(param);
        return convertPage(result, cls);
    }

    default <R> PageInfo<R> convertPage(PageInfo<T> source, Class<R> cls) {
        if (source != null) {
            PageInfo<R> target = new PageInfo<>();
            BeanUtil.copyProperties(source, target, "list");
            List<R> resultList = BeanUtil.copyToList(source.getList(), cls);
            target.setList(resultList);
            return target;
        }
        return null;
    }
}