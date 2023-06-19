package com.gccloud.dataset.entity.config;

import com.gccloud.dataset.dto.DatasetParamDTO;
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
public class GroovyDataSetConfig extends BaseDataSetConfig {

    @ApiModelProperty(value = "脚本")
    private String script;

    @ApiModelProperty(value = "输出字段描述")
    private Map<String, Object> fieldDesc;


}
