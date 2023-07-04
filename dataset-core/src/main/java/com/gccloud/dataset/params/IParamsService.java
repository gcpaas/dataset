package com.gccloud.dataset.params;

import com.gccloud.dataset.dto.DatasetParamDTO;

import java.util.List;

/**
 * 数据集参数处理，可通过实现该接口来自定义参数处理逻辑
 * @author hongyang
 * @version 1.0
 * @date 2023/5/22 13:58
 */
public interface IParamsService {


    /**
     * 处理数据集参数
     * @param params 数据集参数
     * @return 处理后的数据集参数
     */
    List<DatasetParamDTO> handleParams(List<DatasetParamDTO> params);


    /**
     * 自定义处理脚本
     * @param datasetType 数据集类型
     * @param script 脚本
     * @return 处理后的脚本
     */
    String handleScript(String datasetType, String script);

}
