package com.gccloud.dataset.entity.config;

import com.gccloud.dataset.constant.DatasetConstant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/6/1 10:43
 */
@Data
public class JsDataSetConfig extends BaseDataSetConfig {

    @ApiModelProperty("数据集类型")
    private String datasetType = DatasetConstant.DataSetType.JS;

    @ApiModelProperty(value = "脚本")
    private String script;

    @ApiModelProperty(value = "输出字段描述")
    private Map<String, Object> fieldDesc;


}
