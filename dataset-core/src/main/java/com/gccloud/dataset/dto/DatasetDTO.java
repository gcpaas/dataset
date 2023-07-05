package com.gccloud.dataset.dto;

import com.gccloud.dataset.entity.DatasetEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/7/5 15:39
 */
@Data
public class DatasetDTO extends DatasetEntity {

    @ApiModelProperty(value = "关联标签id列表")
    private List<String> labelIds;

}
