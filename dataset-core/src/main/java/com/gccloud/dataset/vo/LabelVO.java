package com.gccloud.dataset.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.gccloud.dataset.entity.LabelEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/7/5 13:47
 */
@Data
public class LabelVO extends LabelEntity {

    @ApiModelProperty(value = "数据集与标签关系json")
    private Map<String, Object> jsonData;

}
