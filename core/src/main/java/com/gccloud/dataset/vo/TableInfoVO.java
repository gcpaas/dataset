package com.gccloud.dataset.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 数据库表/视图信息
 * @author hongyang
 * @version 1.0
 * @date 2023/6/6 14:42
 */
@Data
public class TableInfoVO {

    @ApiModelProperty(value = "表名")
    private String name;

    @ApiModelProperty(value = "状态，用于表示该表是否已经创建过原始数据集 0：未创建 1：已创建")
    private Integer status;

}
