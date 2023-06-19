package com.gccloud.dataset.dto;

import com.gccloud.common.dto.SearchDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 数据集执行DTO
 * @author zhang.zeJun
 * @date 2022-11-18-16:15
 */
@ApiModel
@Data
public class ExecuteDTO extends SearchDTO {

    @ApiModelProperty(value = "数据集类型")
    private String dataSetType;

    @ApiModelProperty(value = "数据集id")
    private String dataSetId;

    @ApiModelProperty(value = "参数")
    private List<DatasetParamDTO> params;

}
