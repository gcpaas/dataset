package com.gccloud.dataset.dto;

import com.gccloud.common.dto.SearchDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/6/1 10:58
 */
@Data
public class DatasetSearchDTO extends SearchDTO {

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "数据集编码")
    private String code;

    @ApiModelProperty(value = "种类id")
    private String typeId;

    @ApiModelProperty(value = "数据集类型")
    private List<String> datasetType;

    @ApiModelProperty(value = "所属数据源id")
    private String sourceId;

    @ApiModelProperty(value = "关联标签id列表")
    private List<String> labelIds;

    @ApiModelProperty(value = "数据集id列表")
    private List<String> datasetIds;

    @ApiModelProperty(value = "定位id，用于获取该数据集id位于的分页位置")
    private String positionId;


}
