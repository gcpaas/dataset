package com.gccloud.dataset.vo;

import com.gccloud.common.vo.PageVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 数据库工具执行结果数据封装，包含数据、结构
 * @author hongyang
 * @version 1.0
 * @date 2023/6/5 16:17
 */
@Data
public class DbDataVO {

    @ApiModelProperty(value = "数据库执行结果数据")
    private List<Map<String, Object>> data;

    @ApiModelProperty(value = "数据库执行结果数据")
    private PageVO<Map<String, Object>> pageData;

    @ApiModelProperty(value = "数据库执行结果数据结构")
    private List<Map<String, Object>> structure;

}
