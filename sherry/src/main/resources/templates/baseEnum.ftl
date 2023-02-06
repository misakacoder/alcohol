package ${packageName}.${basePackageName};

import java.io.Serializable;

/**
 * <p>
 * 枚举基类
 * </p>
 *
 * @author ${author}
 * @date ${date}
 */
public interface BaseEnum<T> extends Serializable {

    T getValue();

    String getLabel();
}