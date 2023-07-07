package com.gccloud.dataset.vo;

import com.gccloud.dataset.dto.DatasetParamDTO;
import com.gccloud.dataset.entity.LabelEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 数据集信息VO
 * @author hongyang
 * @version 1.0
 * @date 2023/6/7 13:44
 */
@Data
public class DatasetInfoVO {

    @ApiModelProperty(value = "数据集ID")
    private String id;

    @ApiModelProperty(value = "数据集名称")
    private String name;

    @ApiModelProperty(value = "分类ID")
    private String typeId;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "数据集类型")
    private String datasetType;

    @ApiModelProperty(value = "数据源id")
    private String sourceId;

    @ApiModelProperty(value = "模块编码")
    private String moduleCode;

    @ApiModelProperty(value = "是否可编辑，0 不可编辑 1 可编辑")
    private Integer editable;

    @ApiModelProperty(value = "数据集字段信息，可选key参考本类中的常量，其中必须包含FIELD_NAME、FIELD_DESC")
    private List<Map<String, Object>> fields;

    @ApiModelProperty(value = "参数配置信息")
    private List<DatasetParamDTO> params;

    @ApiModelProperty(value = "关联标签列表")
    private List<LabelEntity> labelList;

    /**
     * 数据集字段信息:字段名称
     */
    public static final String FIELD_NAME = "fieldName";

    /**
     * 数据集字段信息:字段类型
     */
    public static final String FIELD_TYPE = "fieldType";

    /**
     * 数据集字段信息:字段描述
     */
    public static final String FIELD_DESC = "fieldDesc";

    /**
     * 数据集字段信息:字段排序
     */
    public static final String FIELD_ORDER = "orderNum";

    /**
     * 数据集字段信息:字段来源
     */
    public static final String FIELD_SOURCE = "sourceTable";


}
