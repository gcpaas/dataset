package com.gccloud.dataset.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/6/21 17:55
 */
@Data
public class ExcelParseVO {

    @ApiModelProperty(value = "列信息")
    private List<Map<String, Object>> headMap;

    @ApiModelProperty(value = "预览数据")
    private List<Map<String, Object>> dataList;
}
