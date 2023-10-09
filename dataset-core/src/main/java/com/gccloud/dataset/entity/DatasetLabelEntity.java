package com.gccloud.dataset.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gccloud.common.utils.EmptyAsNullDeserializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 数据集与标签关联表
 * @author hongyang
 * @version 1.0
 * @date 2023/7/5 10:13
 */
@Data
@TableName("ds_dataset_label")
@ApiModel("数据集与标签关联表")
public class DatasetLabelEntity {

    @TableId
    @JsonDeserialize(using = EmptyAsNullDeserializer.class)
    @ApiModelProperty(notes = "主键")
    private String id;

    @ApiModelProperty(value = "关联数据集ID")
    private String datasetId;

    @ApiModelProperty(value = "关联标签ID")
    private String labelId;


}
