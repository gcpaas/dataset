package com.gccloud.dataset.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:数据集参数
 * @Author yang.hw
 * @Date 2021/9/15 11:11
 */
@Data
public class DatasetParamDTO {

    @ApiModelProperty(value = "参数名称")
    private String name;

    /**
     * 参考：{@link com.gccloud.dataset.constant.DatasetConstant.SqlParamsType}
     */
    @ApiModelProperty(value = "参数类型")
    private String type;

    @ApiModelProperty(value = "参数值")
    private String value;

    @ApiModelProperty(value = "参数状态")
    private Integer status;

    @ApiModelProperty(value = "是否必填")
    private Integer require;

    @ApiModelProperty(value = "备注")
    private String remark;
}
