package com.gccloud.dataset.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 数据集的返回数据封装，包含数据、分页数据、结构
 * @author hongyang
 * @version 1.0
 * @date 2023/6/5 16:17
 */
@Data
public class DataVO {

    @ApiModelProperty(value = "数据集执行结果数据")
    private Object data;

    @ApiModelProperty(value = "数据集执行结果的数据结构，非必须")
    private List<Map<String, Object>> structure;

    public DataVO() {
    }

    public DataVO(Object data, List<Map<String, Object>> structure) {
        this.data = data;
        this.structure = structure;
    }

}
