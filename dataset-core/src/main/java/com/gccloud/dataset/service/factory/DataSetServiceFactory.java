package com.gccloud.dataset.service.factory;

import com.gccloud.dataset.entity.DatasetEntity;
import com.gccloud.dataset.service.IBaseDataSetService;
import com.gccloud.dataset.service.impl.dataset.BaseDatasetServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/6/1 11:42
 */
@Service
public class DataSetServiceFactory {

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private BaseDatasetServiceImpl baseDatasetService;

    /**
     * 根据数据集类型获取对应的数据集服务实现类
     * @param type
     * @return
     */
    public IBaseDataSetService build(String type) {
        return applicationContext.getBean(type, IBaseDataSetService.class);
    }

    /**
     * 根据数据集id获取对应的数据集服务实现类
     * @param id
     * @return
     */
    public IBaseDataSetService buildById(String id) {
        DatasetEntity dataset = baseDatasetService.getByIdFromCache(id);
        String datasetType = dataset.getDatasetType();
        return applicationContext.getBean(datasetType, IBaseDataSetService.class);
    }

}
