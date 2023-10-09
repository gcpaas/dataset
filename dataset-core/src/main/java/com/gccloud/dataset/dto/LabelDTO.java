package com.gccloud.dataset.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gccloud.common.utils.EmptyAsNullDeserializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/7/5 10:58
 */
@Data
public class LabelDTO {

    @JsonDeserialize(using = EmptyAsNullDeserializer.class)
    @ApiModelProperty(notes = "主键")
    private String id;

    @ApiModelProperty(value = "标签名称")
    private String labelName;

    @ApiModelProperty(value = "标签类型")
    private String labelType;

    @ApiModelProperty(value = "更新前标签类型")
    private String oldLabelType;

    @ApiModelProperty(value = "标签说明")
    private String labelDesc;

}
