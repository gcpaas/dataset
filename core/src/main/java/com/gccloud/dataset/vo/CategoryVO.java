package com.gccloud.dataset.vo;

import com.gccloud.common.vo.TreeVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 种类树VO
 * @author pan.shun
 * @since 2021/9/7 10:58
 */
@Data
public class CategoryVO implements TreeVo<CategoryVO> {

    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "种类树名称")
    private String name;

    @ApiModelProperty(value = "父级ID")
    private String parentId;

    @ApiModelProperty(value = "父级名称")
    private String parentName;

    @ApiModelProperty(value = "子节点列表")
    private List<CategoryVO> children;

    @ApiModelProperty(value = "表名称")
    private String type;

    @ApiModelProperty(value = "模块编码")
    private String moduleCode;

    @ApiModelProperty(notes = "创建时间")
    private Date createDate;

    @ApiModelProperty(notes = "更新时间")
    private Date updateDate;
}
