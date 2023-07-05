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
    private String datasetType;

    @ApiModelProperty(value = "模块编码")
    private String moduleCode;

    @ApiModelProperty(value = "所属数据源id")
    private String sourceId;

    @ApiModelProperty(value = "关联标签id列表")
    private List<String> labelIds;





}
