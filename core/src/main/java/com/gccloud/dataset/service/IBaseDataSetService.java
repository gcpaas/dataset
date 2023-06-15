package com.gccloud.dataset.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gccloud.common.service.ISuperService;
import com.gccloud.common.utils.BeanConvertUtils;
import com.gccloud.common.vo.PageVO;
import com.gccloud.dataset.dto.DatasetParamDTO;
import com.gccloud.dataset.dto.DatasetSearchDTO;
import com.gccloud.dataset.entity.DatasetEntity;
import com.gccloud.dataset.entity.config.BaseDataSetConfig;
import com.gccloud.dataset.vo.DatasetInfoVO;
import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/6/1 10:56
 */
public interface IBaseDataSetService extends ISuperService<DatasetEntity> {

    AsyncCache<String, Object> DATASET_CACHE = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).buildAsync();



    /**
     * 列表查询
     * @param searchDTO
     * @return
     */
    default List<DatasetEntity> getList(DatasetSearchDTO searchDTO) {
        LambdaQueryWrapper<DatasetEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(searchDTO.getName()), DatasetEntity::getName, searchDTO.getName());
        queryWrapper.eq(StringUtils.isNotBlank(searchDTO.getTypeId()), DatasetEntity::getTypeId, searchDTO.getTypeId());
        queryWrapper.eq(StringUtils.isNotBlank(searchDTO.getDatasetType()), DatasetEntity::getDatasetType, searchDTO.getDatasetType());
        queryWrapper.eq(StringUtils.isNotBlank(searchDTO.getModuleCode()), DatasetEntity::getModuleCode, searchDTO.getModuleCode());
        queryWrapper.eq(StringUtils.isNotBlank(searchDTO.getSourceId()), DatasetEntity::getSourceId, searchDTO.getSourceId());
        queryWrapper.orderByDesc(DatasetEntity::getUpdateDate);
        return this.list(queryWrapper);
    }

    /**
     * 分页查询
     * @param searchDTO
     * @return
     */
    default PageVO<DatasetEntity> getPage(DatasetSearchDTO searchDTO) {
        LambdaQueryWrapper<DatasetEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(searchDTO.getName()), DatasetEntity::getName, searchDTO.getName());
        queryWrapper.eq(StringUtils.isNotBlank(searchDTO.getTypeId()), DatasetEntity::getTypeId, searchDTO.getTypeId());
        queryWrapper.eq(StringUtils.isNotBlank(searchDTO.getDatasetType()), DatasetEntity::getDatasetType, searchDTO.getDatasetType());
        queryWrapper.eq(StringUtils.isNotBlank(searchDTO.getModuleCode()), DatasetEntity::getModuleCode, searchDTO.getModuleCode());
        queryWrapper.eq(StringUtils.isNotBlank(searchDTO.getSourceId()), DatasetEntity::getSourceId, searchDTO.getSourceId());
        queryWrapper.orderByDesc(DatasetEntity::getUpdateDate);
        return this.page(searchDTO, queryWrapper);
    }

    /**
     * 新增
     * @param entity
     * @return
     */
    default String add(DatasetEntity entity) {
        this.save(entity);
        return entity.getId();
    }


    /**
     * 修改
     * @param entity
     * @return
     */
    default void update(DatasetEntity entity) {
        this.updateById(entity);
    }


    /**
     * 删除
     * @param id
     * @return
     */
    default void delete(String id) {
        this.removeById(id);
    }

    /**
     * 名称重复校验
     * @param id
     * @param name
     * @param moduleCode
     * @return
     */
    default boolean checkNameRepeat(String id, String name, String moduleCode) {
        LambdaQueryWrapper<DatasetEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DatasetEntity::getName, name);
        queryWrapper.eq(StringUtils.isNotBlank(moduleCode), DatasetEntity::getModuleCode, moduleCode);
        queryWrapper.ne(StringUtils.isNotBlank(id), DatasetEntity::getId, id);
        return this.count(queryWrapper) > 0;
    }


    /**
     * 根据id查询数据集详情
     * @param id
     * @return
     */
    default DatasetInfoVO getInfoById(String id) {
        DatasetEntity entity = this.getById(id);
        DatasetInfoVO datasetInfoVO = BeanConvertUtils.convert(entity, DatasetInfoVO.class);
        BaseDataSetConfig config = entity.getConfig();
        datasetInfoVO.setFields(config.getFieldList());
        datasetInfoVO.setParams(config.getParamsList());
        return datasetInfoVO;
    }

    /**
     * 检查是否需要由后端执行
     * @param datasetId
     * @return
     */
    default boolean checkBackendExecutionNeeded(String datasetId) {
        return true;
    }


    /**
     * 获取分页数据（数据集执行）
     * 当script不为空时，执行script，否则根据id执行数据集配置
     * @param script 不同的数据集类型，传的值不同，如：sql、存储过程、脚本
     * @param id
     * @param dataSourceId
     * @param params
     * @param current
     * @param size
     * @return
     */
    default PageVO<Object> getPageData(String script, String dataSourceId, String id, List<DatasetParamDTO> params, int current, int size) {
        return null;
    }


    /**
     * 获取数据（数据集执行）
     * 当script不为空时，执行script，否则根据id执行数据集配置
     * @param script
     * @param id
     * @param dataSourceId
     * @param params
     * @return
     */
    Object getData(String script, String dataSourceId, String id, List<DatasetParamDTO> params);


    /**
     * 获取数据结构
     * @param script
     * @param id
     * @param dataSourceId
     * @param params
     * @return
     */
    default List<Map<String, Object>> getStructure(String script, String dataSourceId, String id, List<DatasetParamDTO> params) {
        return null;
    }


}
