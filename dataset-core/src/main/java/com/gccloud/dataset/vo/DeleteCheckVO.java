package com.gccloud.dataset.vo;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import java.util.List;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/7/19 17:57
 */
@Data
public class DeleteCheckVO {

    @ApiModelProperty(value = "是否可以删除")
    private Boolean canDelete = true;

    @ApiModelProperty(value = "不可删除原因")
    private List<String> reasons;

}
