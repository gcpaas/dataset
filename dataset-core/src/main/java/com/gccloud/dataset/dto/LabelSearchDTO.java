package com.gccloud.dataset.dto;

import com.gccloud.common.dto.SearchDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/7/5 10:38
 */
@Data
public class LabelSearchDTO extends SearchDTO {

    @ApiModelProperty(value = "标签名称")
    private String labelName;

    @ApiModelProperty(value = "标签类型")
    private String labelType;

}
