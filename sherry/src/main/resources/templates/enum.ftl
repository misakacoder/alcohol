<#assign MYBATIS_PLUS = "MYBATIS_PLUS">
package ${packageName}.${enumPackageName};

<#if orm = MYBATIS_PLUS>
import com.baomidou.mybatisplus.annotation.IEnum;
</#if>
import ${packageName}.${basePackageName}.BaseEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 *
 * @author ${author}
 * @date ${date}
 */
public enum ${enum} implements<#if orm = MYBATIS_PLUS> IEnum<Integer>,</#if> BaseEnum<Integer> {

    DEFAULT(0, "默认");

    private final Integer value;

    private final String label;

    ${enum}(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    @JsonValue
    public String getLabel() {
        return label;
    }
}