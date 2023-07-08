package com.gccloud.dataset.dto;

import com.gccloud.common.dto.SearchDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用于excel数据源的解析
 * @author hongyang
 * @version 1.0
 * @date 2023/6/21 17:50
 */
@Data
public class ExcelParseDTO extends SearchDTO {

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "表头行数")
    private Integer headRowNum;

}
