package com.gccloud.dataset.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gccloud.common.entity.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author pan.shun
 * @since 2021/9/6 14:49
 */
@Data
@TableName("ds_datasource")
@ApiModel("数据源配置")
public class DatasourceEntity extends SuperEntity {

    @ApiModelProperty(value = "数据源名称 ")
    private String sourceName;

    @ApiModelProperty(value = "数据源类型")
    private String sourceType;

    @ApiModelProperty(value = "连接驱动")
    private String driverClassName;

    @ApiModelProperty(value = "连接url")
    private String url;

    @ApiModelProperty(value = "主机")
    private String host;

    @ApiModelProperty(value = "端口")
    private Integer port;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "密码")
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    private String password;

    @ApiModelProperty(value = "模块编码")
    private String moduleCode;

    @ApiModelProperty(value = "是否可编辑，0 不可编辑 1 可编辑")
    private Integer editable;

    @ApiModelProperty(value = "备注")
    private String remark;
}
