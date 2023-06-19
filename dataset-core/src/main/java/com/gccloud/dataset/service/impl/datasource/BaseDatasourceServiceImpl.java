package com.gccloud.dataset.service.impl.datasource;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gccloud.dataset.dao.DatasourceDao;
import com.gccloud.dataset.entity.DatasourceEntity;
import com.gccloud.dataset.service.IBaseDatasourceService;
import com.gccloud.dataset.vo.DataVO;
import org.springframework.stereotype.Service;

/**
 * 数据源基础服务，仅提供数据源的基础操作（增、删、改、查），未实现具体的数据源执行逻辑
 * @author hongyang
 * @version 1.0
 * @date 2023/6/2 11:06
 */
@Service("baseDatasourceService")
public class BaseDatasourceServiceImpl extends ServiceImpl<DatasourceDao, DatasourceEntity> implements IBaseDatasourceService {

    @Override
    public DataVO executeSql(DatasourceEntity datasource, String sql) {
        return null;
    }
}
