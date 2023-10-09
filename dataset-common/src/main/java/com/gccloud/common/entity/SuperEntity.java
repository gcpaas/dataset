package com.gccloud.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gccloud.common.constant.CommonConst;
import com.gccloud.common.utils.EmptyAsNullDeserializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author liuchengbiao
 * @date 2020-07-07 10:02
 */
@Data
@ApiModel
public class SuperEntity implements Serializable {

    @TableId
    @JsonDeserialize(using = EmptyAsNullDeserializer.class)
    @ApiModelProperty(notes = "主键")
    private String id;

    @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(notes = "创建时间")
    private Date createDate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(notes = "更新时间")
    private Date updateDate;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(notes = "创建用户ID")
    private String createBy;

    @TableField(fill = FieldFill.INSERT_UPDATE, insertStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(notes = "更新用户ID")
    private String updateBy;

    @TableLogic(delval =  CommonConst.DelFlag.DELETE + "", value = CommonConst.DelFlag.NOAMAL + "")
    @ApiModelProperty(notes = "删除标识(0：正常，1：删除)", hidden = true)
    private Integer delFlag = 0;
}
