package com.gccloud.dataset.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.gccloud.common.entity.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author pan.shun
 * @since 2021/9/7 10:58
 */
@ToString(callSuper = true)
@ApiModel("种类树")
@TableName("ds_category_tree")
@Data
public class CategoryEntity extends SuperEntity {

    @ApiModelProperty(value = "id序列")
    private String ids;

    @ApiModelProperty(value = "种类树名称")
    private String name;

    @ApiModelProperty(value = "父级ID")
    private String parentId;

    @ApiModelProperty(value = "分类树类型")
    private String type;

}
