package com.gin.vo;

import com.misaka.annotation.MaskField;
import com.misaka.enums.MaskType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("酒VO")
public class AlcoholVO {

    @ApiModelProperty("代号")
    private String codeName;

    @MaskField(value = MaskType.MIDDLE_MASK)
    @ApiModelProperty("人名")
    private String personName;

    @ApiModelProperty("酒名")
    private String alcoholName;

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getAlcoholName() {
        return alcoholName;
    }

    public void setAlcoholName(String alcoholName) {
        this.alcoholName = alcoholName;
    }
}
