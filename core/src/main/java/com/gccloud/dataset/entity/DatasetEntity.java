package com.gccloud.dataset.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gccloud.common.entity.SuperEntity;
import com.gccloud.dataset.entity.config.BaseDataSetConfig;
import com.gccloud.dataset.typehandler.BaseDataSetConfigTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author zhang.zeJun
 * @date 2022-11-14-9:57
 */
@Data
@TableName("ds_dataset")
@ApiModel("主数据集")
public class DatasetEntity extends SuperEntity {

    @ApiModelProperty(value = "数据集名称")
    private String name;

    @ApiModelProperty(value = "分类ID")
    private String typeId;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "数据集类型")
    private String datasetType;

    @ApiModelProperty(value = "模块编码")
    private String moduleCode;

    @ApiModelProperty(value = "是否可编辑，0 不可编辑 1 可编辑")
    private Integer editable;

    @ApiModelProperty(value = "数据源id")
    private String sourceId;

    @ApiModelProperty(value = "唯一编码")
    private String code;

    @ApiModelProperty("缓存时间, 单位分钟, 0 表示不缓存")
    private Integer cacheTime;

    @ApiModelProperty(value = "具体数据集配置")
    @TableField(typeHandler = BaseDataSetConfigTypeHandler.class)
    private BaseDataSetConfig config;


}
