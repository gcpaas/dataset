package com.gccloud.dataset.service;

import com.gccloud.common.service.ISuperService;
import com.gccloud.dataset.entity.DatasetLabelEntity;
import com.gccloud.dataset.entity.LabelEntity;
import com.gccloud.dataset.vo.DatasetLabelVO;

import java.util.List;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/7/5 10:32
 */
public interface IDatasetLabelService extends ISuperService<DatasetLabelEntity> {


    /**
     * 根据标签id获取数据集信息
     * @param labelId
     * @return
     */
    List<DatasetLabelVO> getDatasetByLabelId(String labelId);

    /**
     * 根据数据集id获取标签信息
     * @param datasetId
     * @return
     */
    List<LabelEntity> getLabelByDatasetId(String datasetId);

    /**
     * 根据数据集id和标签id删除关系
     * @param datasetId
     * @param labelId
     */
    void delete(String datasetId, String labelId);

    /**
     * 根据数据集id删除关系
     * @param datasetId
     */
    void deleteByDatasetId(String datasetId);

    /**
     * 根据标签id删除关系
     * @param labelId
     */
    void deleteByLabelId(String labelId);

    /**
     * 根据数据集id批量新增
     * @param datasetId
     * @param labelIds
     */
    void addByDatasetId(String datasetId, List<String> labelIds);

    /**
     * 根据标签id批量新增
     * @param labelId
     * @param datasetIds
     */
    void addByLabelId(String labelId, List<String> datasetIds);


    /**
     * 根据标签id获取数据集id列表
     * @param labelIds
     * @return
     */
    List<String> getDatasetIdsByLabelIds(List<String> labelIds);


    /**
     * 根据标签id获取数据集id列表
     * @param labelIds 标签id列表
     * @param allMatch 是否全部匹配，true：全部匹配，false：任意匹配
     * @return
     */
    List<String> getDatasetIdsByLabelIds(List<String> labelIds, boolean allMatch);

    /**
     * 根据数据集id获取标签id列表
     * @param datasetId
     * @return
     */
    List<String> getLabelIdsByDatasetId(String datasetId);


}
