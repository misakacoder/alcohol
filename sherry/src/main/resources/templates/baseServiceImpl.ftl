package ${packageName}.${basePackageName};

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * Service基类现类
 * </p>
 *
 * @author ${author}
 * @date ${date}
 */
@Transactional
public class BaseServiceImpl<T> implements BaseService<T> {

    @Autowired
    private BaseMapper<T> baseMapper;

    @Override
    public Long insert(T entity) {
        return baseMapper.insert(entity);
    }

    @Override
    public Long insertSelective(T entity) {
        return baseMapper.insertSelective(entity);
    }

    @Override
    public Long insertList(List<T> entityList) {
        return baseMapper.insertList(entityList);
    }

    @Override
    public Long deleteByPrimaryKey(Object primaryKey) {
        return baseMapper.deleteByPrimaryKey(primaryKey);
    }

    @Override
    public Long delete(T entity) {
        return baseMapper.delete(entity);
    }

    @Override
    public Long updateByPrimaryKey(T entity) {
        return baseMapper.updateByPrimaryKey(entity);
    }

    @Override
    public Long updateByPrimaryKeySelective(T entity) {
        return baseMapper.updateByPrimaryKeySelective(entity);
    }

    @Override
    public T selectByPrimaryKey(Object primaryKey) {
        return baseMapper.selectByPrimaryKey(primaryKey);
    }

    @Override
    public T selectOne(T entity) {
        return baseMapper.selectOne(entity);
    }

    @Override
    public List<T> select(T entity) {
        return baseMapper.select(entity);
    }

    @Override
    public <P extends BaseList> List<T> list(P param) {
        return baseMapper.list(param);
    }

    @Override
    public List<T> selectAll() {
        return baseMapper.selectAll();
    }

    @Override
    public Long count(T entity) {
        return baseMapper.count(entity);
    }

    @Override
    public <P extends BasePage> PageInfo<T> page(P param) {
        return new PageInfo<>(baseMapper.page(param));
    }
}