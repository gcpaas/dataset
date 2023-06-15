package com.gccloud.dataset.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 数据库表/视图字段信息
 * @author hongyang
 * @version 1.0
 * @date 2023/6/6 14:42
 */
@Data
public class FieldInfoVO {

    @ApiModelProperty(value = "列名")
    private String columnName;

    @ApiModelProperty(value = "列名注释")
    private String columnComment;

    @ApiModelProperty(value = "列类型")
    private String columnType;


}
