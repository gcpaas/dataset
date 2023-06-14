package com.gccloud.dataset.service.impl.dataset;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gccloud.common.exception.GlobalException;
import com.gccloud.common.vo.PageVO;
import com.gccloud.dataset.constant.DatasetConstant;
import com.gccloud.dataset.dao.DatasetDao;
import com.gccloud.dataset.dto.DatasetParamDTO;
import com.gccloud.dataset.entity.DatasetEntity;
import com.gccloud.dataset.entity.DatasourceEntity;
import com.gccloud.dataset.entity.config.CustomDataSetConfig;
import com.gccloud.dataset.params.ParamsClient;
import com.gccloud.dataset.service.IBaseDataSetService;
import com.gccloud.dataset.service.IBaseDatasourceService;
import com.gccloud.dataset.service.factory.DatasourceServiceFactory;
import com.gccloud.dataset.service.impl.datasource.BaseDatasourceServiceImpl;
import com.gccloud.dataset.utils.DBUtils;
import com.gccloud.dataset.vo.DataVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/6/1 11:20
 */
@Slf4j
@Service(DatasetConstant.DataSetType.CUSTOM)
public class CustomDataSetServiceImpl extends ServiceImpl<DatasetDao, DatasetEntity> implements IBaseDataSetService {

    @Resource
    private DatasourceServiceFactory datasourceServiceFactory;

    @Resource
    private BaseDatasourceServiceImpl datasourceService;

    @Resource
    private ParamsClient paramsClient;


    @Override
    public PageVO getPageData(String sql, String dataSourceId, String id, List<DatasetParamDTO> params, int current, int size) {
        if (StringUtils.isBlank(sql) && StringUtils.isBlank(id)) {
            throw new GlobalException("sql和数据集id不能同时为空");
        }
        if (StringUtils.isBlank(sql)) {
            DatasetEntity entity = this.getById(id);
            CustomDataSetConfig config = (CustomDataSetConfig) entity.getConfig();
            sql = config.getSqlProcess();
            dataSourceId = entity.getSourceId();
        }
        // 参数预处理
        params = paramsClient.handleParams(params);
        // 参数替换
        sql = DBUtils.updateParamsConfig(sql, params);
        DatasourceEntity datasource = datasourceService.getInfoById(dataSourceId);
        IBaseDatasourceService buildService = datasourceServiceFactory.build(datasource.getSourceType());
        DataVO dataVO = buildService.executeSqlPage(datasource, sql, current, size);
        return dataVO.getPageData();
    }

    @Override
    public Object getData(String sql, String dataSourceId, String id, List<DatasetParamDTO> params) {
        if (StringUtils.isBlank(sql) && StringUtils.isBlank(id)) {
            throw new GlobalException("sql和数据集id不能同时为空");
        }
        if (StringUtils.isBlank(sql)) {
            DatasetEntity entity = this.getById(id);
            CustomDataSetConfig config = (CustomDataSetConfig) entity.getConfig();
            sql = config.getSqlProcess();
            dataSourceId = entity.getSourceId();
        }
        // 参数预处理
        params = paramsClient.handleParams(params);
        // 参数替换
        sql = DBUtils.updateParamsConfig(sql, params);
        DatasourceEntity datasource = datasourceService.getInfoById(dataSourceId);
        IBaseDatasourceService buildService = datasourceServiceFactory.build(datasource.getSourceType());
        DataVO dataVO = buildService.executeSql(datasource, sql);
        return dataVO.getData();
    }

    @Override
    public List<Map<String, Object>> getStructure(String sql, String dataSourceId, String id, List<DatasetParamDTO> params) {
        if (StringUtils.isBlank(sql) && StringUtils.isBlank(id)) {
            throw new GlobalException("sql和数据集id不能同时为空");
        }
        if (StringUtils.isBlank(sql)) {
            DatasetEntity entity = this.getById(id);
            CustomDataSetConfig config = (CustomDataSetConfig) entity.getConfig();
            sql = config.getSqlProcess();
            dataSourceId = entity.getSourceId();
        }
        // 参数预处理
        params = paramsClient.handleParams(params);
        // 参数替换
        sql = DBUtils.updateParamsConfig(sql, params);
        DatasourceEntity datasource = datasourceService.getInfoById(dataSourceId);
        IBaseDatasourceService buildService = datasourceServiceFactory.build(datasource.getSourceType());
        // 主要目的是获取结构，所以只查询一条数据
        DataVO dataVO = buildService.executeSqlPage(datasource, sql, 1, 1);
        return dataVO.getStructure();
    }
}
