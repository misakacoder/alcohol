package com.gin.vo;

import com.misaka.annotation.MaskField;
import com.misaka.enums.MaskType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("酒厂VO")
public class WineVO {

    @ApiModelProperty("代号")
    private String codeName;

    @MaskField(value = MaskType.MIDDLE_MASK)
    @ApiModelProperty("人名")
    private String personName;

    @ApiModelProperty("酒名")
    private String wineName;

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

    public String getWineName() {
        return wineName;
    }

    public void setWineName(String wineName) {
        this.wineName = wineName;
    }
}
