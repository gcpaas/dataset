package com.gccloud.dataset.vo;

import com.gccloud.common.vo.PageVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/6/5 16:17
 */
@Data
public class DataVO {

    @ApiModelProperty(value = "列表数据")
    private List<Map<String, Object>> data;

    @ApiModelProperty(value = "分页数据")
    private PageVO<Map<String, Object>> pageData;

    @ApiModelProperty(value = "结构")
    private List<Map<String, Object>> structure;

}
