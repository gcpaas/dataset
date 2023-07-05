package com.gccloud.dataset.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.gccloud.dataset.entity.DatasetLabelEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/7/5 10:58
 */
@Data
public class LabelDTO {

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

    @ApiModelProperty(value = "关系集合")
    private List<DatasetLabelEntity> relList;
}
