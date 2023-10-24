package com.gccloud.dataset.entity.config;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/6/1 10:43
 */
@Data
public class CustomDataSetConfig extends BaseDataSetConfig {

    @ApiModelProperty(value = "数据源id")
    private String sourceId;

    @ApiModelProperty(value = "自定义Sql")
    private String sqlProcess;

    @ApiModelProperty(value = "字段描述")
    private Map<String, Object> fieldDesc;

    @ApiModelProperty(value = "语法类型 normal:普通 mybatis:mybatis")
    private String syntaxType;

    /**
     * 暂未使用
     */
    @ApiModelProperty(value = "数据集编码")
    private String code;

    /**
     * 暂未使用
     */
    @ApiModelProperty(value = "结构缓存")
    private String cacheField;

    /**
     * 暂未使用
     */
    @ApiModelProperty(value = "结果转换脚本")
    private String script;


}
