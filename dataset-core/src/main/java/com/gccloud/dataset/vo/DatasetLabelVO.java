package com.gccloud.dataset.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/7/5 13:52
 */
@Data
public class DatasetLabelVO {

    @ApiModelProperty("数据集id")
    private String id;

    @ApiModelProperty("数据集名称")
    private String name;

}
