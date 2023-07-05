package com.gccloud.dataset.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.gccloud.common.entity.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/7/5 10:12
 */
@Data
@TableName("ds_label")
@ApiModel("标签")
public class LabelEntity extends SuperEntity {

    @ApiModelProperty(value = "标签名称")
    private String labelName;

    @ApiModelProperty(value = "标签类型")
    private String labelType;

    @ApiModelProperty(value = "标签说明")
    private String labelDesc;

}
