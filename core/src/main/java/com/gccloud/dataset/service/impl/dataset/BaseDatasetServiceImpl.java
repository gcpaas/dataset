package com.gccloud.dataset.service.impl.dataset;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gccloud.dataset.dao.DatasetDao;
import com.gccloud.dataset.dto.DatasetParamDTO;
import com.gccloud.dataset.entity.DatasetEntity;
import com.gccloud.dataset.service.IBaseDataSetService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据集基础服务，仅提供数据集的基础操作（增、删、改、查），未实现具体的数据集执行逻辑
 * @author hongyang
 * @version 1.0
 * @date 2023/6/1 13:37
 */
@Service("baseDataSetService")
public class BaseDatasetServiceImpl extends ServiceImpl<DatasetDao, DatasetEntity> implements IBaseDataSetService {

    @Override
    public Object getData(String sql, String datasourceId, String id, List<DatasetParamDTO> params) {
        return null;
    }

}
