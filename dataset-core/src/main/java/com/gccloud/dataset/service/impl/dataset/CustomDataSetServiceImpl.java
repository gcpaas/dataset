package com.gccloud.dataset.service.impl.dataset;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gccloud.common.exception.GlobalException;
import com.gccloud.common.vo.PageVO;
import com.gccloud.dataset.constant.DatasetConstant;
import com.gccloud.dataset.dao.DatasetDao;
import com.gccloud.dataset.dto.DatasetParamDTO;
import com.gccloud.dataset.dto.TestExecuteDTO;
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
    public PageVO execute(String id, List<DatasetParamDTO> params, int current, int size) {
        if (StringUtils.isBlank(id)) {
            throw new GlobalException("数据集id不能为空");
        }
        DatasetEntity entity = this.getById(id);
        if (entity == null) {
            throw new GlobalException("数据集不存在");
        }
        CustomDataSetConfig config = (CustomDataSetConfig) entity.getConfig();
        String sql = config.getSqlProcess();
        // 参数预处理
        params = paramsClient.handleParams(params);
        // 参数替换
        sql = DBUtils.updateParamsConfig(sql, params);
        String dataSourceId = entity.getSourceId();
        DatasourceEntity datasource = datasourceService.getInfoById(dataSourceId);
        IBaseDatasourceService buildService = datasourceServiceFactory.build(datasource.getSourceType());
        DataVO dataVO = buildService.executeSqlPage(datasource, sql, current, size);
        return (PageVO) dataVO.getData();
    }


    @Override
    public Object execute(String id, List<DatasetParamDTO> params) {
        if (StringUtils.isBlank(id)) {
            throw new GlobalException("数据集id不能为空");
        }
        final List<DatasetParamDTO> finalParams = params;
        DatasetEntity entity = this.getById(id);
        if (entity == null) {
            throw new GlobalException("数据集不存在");
        }
        if (DatasetConstant.DatasetCache.OPEN.equals(entity.getCache())) {
            return DATASET_CACHE.get(id, key -> getData(finalParams, entity));
        }
        return getData(finalParams, entity);
    }

    /**
     * 获取数据
     * @param finalParams
     * @param entity
     * @return
     */
    private Object getData(List<DatasetParamDTO> finalParams, DatasetEntity entity) {
        CustomDataSetConfig config = (CustomDataSetConfig) entity.getConfig();
        String sql = config.getSqlProcess();
        String dataSourceId = entity.getSourceId();
        List<DatasetParamDTO> paramList = paramsClient.handleParams(finalParams);
        // 参数替换
        sql = DBUtils.updateParamsConfig(sql, paramList);
        DatasourceEntity datasource = datasourceService.getInfoById(dataSourceId);
        IBaseDatasourceService buildService = datasourceServiceFactory.build(datasource.getSourceType());
        DataVO dataVO = buildService.executeSql(datasource, sql);
        return dataVO.getData();
    }

    @Override
    public DataVO execute(TestExecuteDTO executeDTO) {
        List<DatasetParamDTO> params = executeDTO.getParams();
        // 参数预处理
        params = paramsClient.handleParams(params);
        String sql = executeDTO.getScript();
        // 参数替换
        sql = DBUtils.updateParamsConfig(sql, params);
        DatasourceEntity datasource = datasourceService.getInfoById(executeDTO.getDataSourceId());
        IBaseDatasourceService buildService = datasourceServiceFactory.build(datasource.getSourceType());
        DataVO dataVO;
        Integer current = executeDTO.getCurrent();
        Integer size = executeDTO.getSize();
        if (size != null && current != null) {
            dataVO = buildService.executeSqlPage(datasource, sql, current, size);
        } else {
            dataVO = buildService.executeSql(datasource, sql);
        }
        return dataVO;
    }

}