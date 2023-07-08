package com.gccloud.dataset.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Excel数据源表头DTO
 * @author hongyang
 * @version 1.0
 * @date 2023/7/3 10:45
 */
@Data
public class ExcelHeaderDTO {

    @ApiModelProperty(value = "表头序号")
    private Integer index;

    @ApiModelProperty(value = "表头名称")
    private String title;

    @ApiModelProperty(value = "字段名称")
    private String columnName;

    @ApiModelProperty(value = "字段类型")
    private String columnType;


}
