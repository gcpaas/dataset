package com.gccloud.dataset.entity.config;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/6/1 10:43
 */
@Data
public class OriginalDataSetConfig extends BaseDataSetConfig {

    @ApiModelProperty(value = "关联数据源id")
    private String sourceId;

    @ApiModelProperty(value = "表名称")
    private String tableName;

    @ApiModelProperty(value = "选择的字段，逗号分隔")
    private String fieldInfo;

    @ApiModelProperty(value = "去重标识 0去重 1不去重")
    private Integer repeatStatus;

    @ApiModelProperty(value = "字段描述")
    private Map<String, Object> fieldDesc;

    /**
     * 暂未使用
     */
    @ApiModelProperty(value = "数据缓存字段")
    private String cacheField;

}
