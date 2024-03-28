package com.gccloud.dataset.dto;

import com.gccloud.common.dto.SearchDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/6/1 10:58
 */
@Data
public class CategorySearchDTO extends SearchDTO {

    @ApiModelProperty(value = "分类树类别")
    private String type;

}
