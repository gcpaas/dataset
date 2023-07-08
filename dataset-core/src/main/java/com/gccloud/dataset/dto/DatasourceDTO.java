package com.gccloud.dataset.dto;

import com.gccloud.dataset.entity.DatasourceEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/6/1 10:58
 */
@Data
public class DatasourceDTO extends DatasourceEntity {

    @ApiModelProperty(value = "其他配置")
    private Map<String, Object> config;

}
