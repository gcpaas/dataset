package com.gccloud.dataset.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gccloud.common.exception.GlobalException;
import com.gccloud.common.service.ISuperService;
import com.gccloud.common.utils.BeanConvertUtils;
import com.gccloud.common.vo.PageVO;
import com.gccloud.dataset.dto.DatasetParamDTO;
import com.gccloud.dataset.dto.DatasetSearchDTO;
import com.gccloud.dataset.dto.TestExecuteDTO;
import com.gccloud.dataset.entity.DatasetEntity;
import com.gccloud.dataset.entity.config.BaseDataSetConfig;
import com.gccloud.dataset.vo.DataVO;
import com.gccloud.dataset.vo.DatasetInfoVO;
import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/6/1 10:56
 */
public interface IBaseDataSetService extends ISuperService<DatasetEntity> {

    /**
     * 数据集结果缓存
     */
    AsyncCache<String, Object> DATASET_CACHE = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).buildAsync();


    /**
     * 列表查询
     * @param searchDTO
     * @return
     */
    default List<DatasetEntity> getList(DatasetSearchDTO searchDTO) {
        return this.list(this.getQueryWrapper(searchDTO));
    }

    /**
     * 分页查询
     * @param searchDTO
     * @return
     */
    default PageVO<DatasetEntity> getPage(DatasetSearchDTO searchDTO) {
        return this.page(searchDTO, this.getQueryWrapper(searchDTO));
    }

    /**
     * 数据集查询的条件组装
     * @param searchDTO
     * @return
     */
    default LambdaQueryWrapper<DatasetEntity> getQueryWrapper(DatasetSearchDTO searchDTO) {
        LambdaQueryWrapper<DatasetEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(searchDTO.getName()), DatasetEntity::getName, searchDTO.getName());
        queryWrapper.eq(StringUtils.isNotBlank(searchDTO.getTypeId()), DatasetEntity::getTypeId, searchDTO.getTypeId());
        queryWrapper.in(CollectionUtils.isNotEmpty(searchDTO.getDatasetType()), DatasetEntity::getDatasetType, searchDTO.getDatasetType());
        queryWrapper.eq(StringUtils.isNotBlank(searchDTO.getModuleCode()), DatasetEntity::getModuleCode, searchDTO.getModuleCode());
        queryWrapper.eq(StringUtils.isNotBlank(searchDTO.getSourceId()), DatasetEntity::getSourceId, searchDTO.getSourceId());
        if (searchDTO.getDatasetIds() != null && searchDTO.getDatasetIds().size() > 0) {
            queryWrapper.in(DatasetEntity::getId, searchDTO.getDatasetIds());
        }
        queryWrapper.orderByDesc(DatasetEntity::getUpdateDate);
        return queryWrapper;
    }

    /**
     * 新增
     * @param entity
     * @return
     */
    default String add(DatasetEntity entity) {
        if (StringUtils.isBlank(entity.getCode())) {
            // 随机生成编码
            entity.setCode(RandomStringUtils.randomAlphanumeric(10));
        }
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
        DATASET_CACHE.synchronous().invalidate(entity.getId());
    }


    /**
     * 删除
     * @param id
     * @return
     */
    default void delete(String id) {
        this.removeById(id);
        DATASET_CACHE.synchronous().invalidate(id);
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
     * 根据code查询数据集详情
     * @param code
     * @return
     */
    default DatasetEntity getByCode(String code, String moduleCode) {
        LambdaQueryWrapper<DatasetEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DatasetEntity::getCode, code);
        queryWrapper.eq(StringUtils.isNotBlank(moduleCode), DatasetEntity::getModuleCode, moduleCode);
        List<DatasetEntity> list = this.list(queryWrapper);
        if (list.isEmpty()) {
            throw new GlobalException("数据集不存在");
        }
        if (list.size() > 1) {
            throw new GlobalException("数据集编码重复");
        }
        return list.get(0);
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
     * @param id
     * @param params
     * @param current
     * @param size
     * @return
     */
    default PageVO<Object> execute(String id, List<DatasetParamDTO> params, int current, int size) {
        return null;
    }


    /**
     * 获取数据（数据集执行）
     * @param datasetId
     * @param params
     * @return
     */
    Object execute(String datasetId, List<DatasetParamDTO> params);


    /**
     * 数据集执行测试
     * 在数据集新增、更新前需要先通过测试，确保数据集可用
     * @param executeDTO
     * @return
     */
    default DataVO execute(TestExecuteDTO executeDTO) {
        return null;
    }


}
