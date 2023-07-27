package com.gccloud.dataset.entity.config;

import com.gccloud.dataset.constant.DatasetConstant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * http接口数据集配置
 * @author hongyang
 * @version 1.0
 * @date 2023/6/1 10:43
 */
@Data
public class HttpDataSetConfig extends BaseDataSetConfig {

    @ApiModelProperty("数据集类型")
    private String datasetType = DatasetConstant.DataSetType.HTTP;

    @ApiModelProperty(value = "请求地址")
    private String url;

    @ApiModelProperty(value = "请求类型 GET POST")
    private String method;

    @ApiModelProperty(value = "请求方式 前端 后端")
    private String requestType;

    @ApiModelProperty(value = "请求头")
    private List<Map<String, Object>> headers;

    @ApiModelProperty(value = "请求参数")
    private List<Map<String, Object>> params;

    @ApiModelProperty(value = "请求体")
    private String body;

    @ApiModelProperty(value = "请求脚本")
    private String requestScript;

    @ApiModelProperty(value = "响应脚本")
    private String responseScript;

}
