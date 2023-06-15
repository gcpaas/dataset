package com.gccloud.dataset.vo;

import com.gccloud.common.vo.PageVO;
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

    @ApiModelProperty(value = "列表数据，不分页时使用")
    private List<Map<String, Object>> data;

    @ApiModelProperty(value = "分页数据，分页时使用")
    private PageVO<Map<String, Object>> pageData;

    @ApiModelProperty(value = "结构，非必须")
    private List<Map<String, Object>> structure;

}
